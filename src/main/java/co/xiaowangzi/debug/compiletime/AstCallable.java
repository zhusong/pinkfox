package co.xiaowangzi.debug.compiletime;

import co.xiaowangzi.debug.clazz.Clazz;
import co.xiaowangzi.debug.runtime.Variable;
import co.xiaowangzi.debug.utils.MavenModuleProcessor;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Pair;
import com.sun.tools.javac.util.StringUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class AstCallable implements java.util.concurrent.Callable<Map<String, Clazz>>{

    private CountDownLatch countDownLatch;
    private Integer methodCount = 0;
    private Integer pointIndex = 0;
    private String classFullQualifiedName;
    private Boolean ifStaticMethod;
    private String methodName;
    private String innerClassName;
    public AstCallable() {

    }
    public AstCallable(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Map<String, Clazz> call() throws Exception {
        final Map<String, Clazz> clazzMap = new HashMap<>();
        Element element = CompilerProcessor.getElement(); //whole class declared, including fields and methods.
        try {
            while (element != null) {
                Clazz clazz = new Clazz(new ArrayList<>(), new ArrayList<>());

                clazz.setParentClassFullQualifiedName(((Symbol.ClassSymbol)element).getSuperclass().toString());
                clazz.setModuleName(MavenModuleProcessor.mavenModuleName(element));

                this.classFullQualifiedName = element.toString(); //such as : com.xx.test.Test
                this.pointIndex = 0;

                clazz.setClassFullQualifiedName(classFullQualifiedName);

                this.astElement(element, clazz, true);

                Pair<JCTree, JCTree.JCCompilationUnit> pair = CompilerProcessor.elementUtils.getTreeAndTopLevel(element, null, null);
                JCTree.JCCompilationUnit snd = pair.snd;
                clazz.setHtmlSource(snd.toString());

                if (springBoot(clazz.getHtmlSource())) {
                    JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) CompilerProcessor.elementUtils.getTree(element);
                    if(clazz.getHtmlSource().contains("@ServletComponentScan")){//源码包含@ServletComponentScan
                        classDecl.accept(new TreeTranslator() {
                            @Override
                            public void visitAnnotation(JCTree.JCAnnotation jcAnnotation) {
                                JCTree.JCIdent jcIdent = (JCTree.JCIdent)jcAnnotation.getAnnotationType();
                                if(jcIdent.name.contentEquals("ServletComponentScan")){
                                    jcAnnotation.args.forEach(e->{
                                        JCTree.JCAssign jcAssign = (JCTree.JCAssign)e ;
                                        ListBuffer<JCTree.JCExpression> args = new ListBuffer<>();

                                        if (jcAssign.rhs instanceof JCTree.JCNewArray) {
                                            List<JCTree.JCExpression> elems = ((JCTree.JCNewArray)jcAssign.rhs).elems;
                                            for (JCTree.JCExpression inner : elems) {
                                                args.append(inner);
                                            }
                                            args.add(CompilerProcessor.treeMaker.Literal("co.xiaowangzi.debug.runtime"));
                                        } else {
                                            args.add(jcAssign.rhs);
                                        }
                                        ((JCTree.JCNewArray) jcAssign.rhs).elems = args.toList();
                                    });
                                }
                                super.visitAnnotation(jcAnnotation);
                            }
                        });
                    } else {
                        synchronized (AstCallable.class) {
                            classDecl.mods.annotations = classDecl.mods.annotations.append(CompilerProcessor.getTreeMaker().Annotation(memberAccess("org.springframework.boot.web.servlet.ServletComponentScan"),
                                    com.sun.tools.javac.util.List.of(makeArg("value", "co.xiaowangzi.debug.runtime"))));
                        }
                    }
                }

                //next class
                element = CompilerProcessor.getElement();

                //put it into class map
                clazzMap.put(classFullQualifiedName, clazz);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return clazzMap;
    }


    public JCTree.JCExpression makeArg(String key, String value) {
        //注解需要的参数是表达式，这里的实际实现为等式对象，Ident是值，Literal是value，最后结果为a=b
        return CompilerProcessor.getTreeMaker().Assign(CompilerProcessor.getTreeMaker().Ident(CompilerProcessor.names.fromString(key)), CompilerProcessor.getTreeMaker().Literal(value));
    }

    public boolean springBoot(String htmlSource){
        return htmlSource.contains("@SpringBootApplication");
    }

    public void astElement(Element classElement, Clazz clazz, boolean outerClass){
        this.initJavacClassFields((Symbol.ClassSymbol) classElement, true, clazz, outerClass, (((Symbol.ClassSymbol) classElement).flatname).toString());

        //You are unable to access the Class the Annotation Processor is processing because the Class has not been compiled yet.
        //Instead, Java offers the analogous Elements api for reflection-style inspection of the input source.
        java.util.List<? extends Element> enclosedElements = classElement.getEnclosedElements(); //tree's element
        for(Element subElement : enclosedElements){

            //Returns the tree node corresponding to this element, or null if none can be found.
            JCTree tree = CompilerProcessor.elementUtils.getTree(subElement);
            if (tree == null) {
                continue;
            }

            //non constructor and non-abstract method
            if (!isMainMethod(subElement, tree) && !isToString(subElement, tree)&& !subElement.getModifiers().contains(Modifier.ABSTRACT) && !tree.toString().contains("<init>") && tree instanceof JCTree.JCMethodDecl) {
                methodName = ((JCTree.JCMethodDecl) tree).name.toString();

                if(!outerClass){
                    innerClassName = (((Symbol.ClassSymbol) classElement).flatname).toString();
                } else {
                    innerClassName = null;
                }
                this.initJavacClassMethods(tree, clazz);
                methodCount++;
            }
            //if inner class
            if(tree instanceof JCTree.JCClassDecl){
                astElement(subElement, clazz, false);
            }
        }
    }

    //因为会调用toString，所以必须过滤，否则死锁
    private boolean isToString(Element e, JCTree tree) {
        if(tree instanceof JCTree.JCMethodDecl){
            String methodName = ((JCTree.JCMethodDecl) tree).name.toString();
            return methodName.equals("toString");
        }
        return false;
    }

    public static boolean isMainMethod(Element e, JCTree tree){
        if(tree instanceof JCTree.JCMethodDecl){
            String methodName = ((JCTree.JCMethodDecl) tree).name.toString();
            return methodName.equals("main");
        }
        return false;
    }

    /**
     * JCTree:
     *
     * public class Demo(JCClassDecl) {
     *      private(JCModifiers) String string(JCVariableDecl);
     *      public String getString(JCMethodDecl)(){
     *          this.string(JCIdent) = "this is a string"(JCAssign);
     *          return "end"(JCReturn);
     *      }
     * }
     *
     */
    private void initJavacClassMethods(JCTree tree, Clazz clazz) {
        try {
            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) tree;
            // statements in the method
            JCTree.JCBlock body = jcMethodDecl.body;

            // method params
            com.sun.tools.javac.util.List<JCTree.JCVariableDecl> parameters = jcMethodDecl.params;

            java.util.List<String> breakPointsVariables = new ArrayList<>();
            parameters.forEach(eachParameter -> {
                breakPointsVariables.add(eachParameter.name.toString());
            });

            this.ifStaticMethod = jcMethodDecl.mods.getFlags().contains(Modifier.STATIC);

            //逐行分析代码
            ListBuffer<JCTree.JCStatement> finalStatementsBuffer = new ListBuffer<>();
            for (JCTree.JCStatement statement : body.stats /* body statement */) {
                Collection<? extends JCTree.JCStatement> collections = this.processEachStatement(statement, breakPointsVariables, clazz);
                finalStatementsBuffer.addAll(collections);
            }
            body.stats = finalStatementsBuffer.toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Collection<? extends JCTree.JCStatement> processEachStatement(JCTree.JCStatement statement, List<String> variables, Clazz clazz) {
        try {
            java.util.List<String> result = new ArrayList<>(variables); //前一步的变量
            final TreeMaker treeMaker = CompilerProcessor.treeMaker;

            // int x = 0;
            if (statement instanceof JCTree.JCVariableDecl) {  //后一步变量
                this.variableDeclare(statement, variables);
            }

            // int add = "a" + "b"
            if (statement instanceof JCTree.JCExpressionStatement) { //后一步变量
                this.expressStatement(statement, variables);
            }

            // switch(){ case : }
            if(statement instanceof JCTree.JCSwitch){

                //如果是switch
//                ListBuffer<JCTree.JCStatement> listBuffer = new ListBuffer<>();
//                JCTree.JCSwitch jcSwitch = (JCTree.JCSwitch) statement;
//                return com.sun.tools.javac.util.List.of(statement);
            }

            // for(int item : list){}
            if(statement instanceof JCTree.JCEnhancedForLoop){
                ListBuffer<JCTree.JCStatement> listBuffer = new ListBuffer<>();
                JCTree.JCEnhancedForLoop enhancedForLoop = (JCTree.JCEnhancedForLoop) statement;
                List<String> list = new ArrayList<>(variables);
                if(enhancedForLoop.body instanceof JCTree.JCExpressionStatement){
                    return processEachStatement(enhancedForLoop.body, list, clazz);
                }
                //如果是语句块
                JCTree.JCBlock body = (JCTree.JCBlock) enhancedForLoop.body;
                for (JCTree.JCStatement s : body.stats) {
                    //处理每条语句
                    listBuffer.addAll(this.processEachStatement(s, list, clazz));
                }
                ((JCTree.JCBlock) enhancedForLoop.body).stats = listBuffer.toList();
                return com.sun.tools.javac.util.List.of(enhancedForLoop);
            }

            // for(int i = 0; i < 100; i++){}
            if (statement instanceof JCTree.JCForLoop) {
                ListBuffer<JCTree.JCStatement> listBuffer = new ListBuffer<>();
                JCTree.JCForLoop forLoop = (JCTree.JCForLoop) statement;
                List<String> list = new ArrayList<>(variables);
                for(JCTree.JCStatement s : forLoop.init){
                    if(s instanceof JCTree.JCVariableDecl){
                        this.variableDeclare(s, list);
                    }
                    if(s instanceof JCTree.JCExpressionStatement){
                        this.expressStatement(s, list);
                    }
                }
                if(forLoop.body instanceof JCTree.JCExpressionStatement){
                    return processEachStatement(forLoop.body, list, clazz);
                }
                JCTree.JCBlock body = (JCTree.JCBlock) forLoop.body;
                for (JCTree.JCStatement s : body.stats) {
                    //processing for body
                    listBuffer.addAll(this.processEachStatement(s, list, clazz));
                }
                ((JCTree.JCBlock) forLoop.body).stats = listBuffer.toList();
                return com.sun.tools.javac.util.List.of(forLoop);
            }

            // if
            //处理if语句
            if (statement instanceof JCTree.JCIf) {
                ListBuffer<JCTree.JCStatement> thenListBuffer = new ListBuffer<JCTree.JCStatement>();
                ListBuffer<JCTree.JCStatement> elseListBuffer = new ListBuffer<JCTree.JCStatement>();

                JCTree.JCIf jcIf = (JCTree.JCIf) statement;
                if (jcIf.thenpart instanceof JCTree.JCExpressionStatement) {
                    JCTree.JCExpressionStatement thenPart = (JCTree.JCExpressionStatement) (jcIf.thenpart);
                    java.util.List<String> list = new ArrayList<>(variables);
                    return processEachStatement(thenPart, list, clazz);
                } else if (jcIf.thenpart instanceof JCTree.JCBlock) {
                    JCTree.JCBlock thenPart = (JCTree.JCBlock) (jcIf.thenpart);
                    List<String> list = new ArrayList<>(variables);

                    for (JCTree.JCStatement s : thenPart.stats) {
                        thenListBuffer.addAll(processEachStatement(s, list, clazz));
                    }
//                    jcIf.thenpart = treeMaker.Block(0, thenListBuffer.toList());
                    ((JCTree.JCBlock) jcIf.thenpart).stats = thenListBuffer.toList();
                }
                try {
                    if(jcIf.elsepart instanceof JCTree.JCBlock){
                        JCTree.JCBlock elsePart = (JCTree.JCBlock) (jcIf.elsepart);
                        List<String> list = new ArrayList<>(variables);
                        for (JCTree.JCStatement s : elsePart.stats) {
                            elseListBuffer.addAll(processEachStatement(s, list, clazz));
                        }
                        ((JCTree.JCBlock) jcIf.elsepart).stats = elseListBuffer.toList();
                    }

                    if(jcIf.elsepart instanceof JCTree.JCIf){
                        JCTree.JCIf elseIf = (JCTree.JCIf) (jcIf.elsepart);
                        List<String> list = new ArrayList<>(variables);
                        processEachStatement(elseIf, list, clazz);
//                        ((JCTree.JCIf) jcIf.elsepart) = elseListBuffer.toList();
//                        jcIf.elsepart.
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return com.sun.tools.javac.util.List.of(jcIf);
            }

            //处理while循环
            if (statement instanceof JCTree.JCWhileLoop) {

                ListBuffer<JCTree.JCStatement> listBuffer = new ListBuffer<JCTree.JCStatement>();
                JCTree.JCWhileLoop jcWhileLoop = (JCTree.JCWhileLoop) statement;
                if(jcWhileLoop.body instanceof JCTree.JCExpressionStatement){
                    return processEachStatement(jcWhileLoop.body, new ArrayList<>(variables), clazz);
                }
                JCTree.JCBlock body = (JCTree.JCBlock) jcWhileLoop.body;
                java.util.List<String> list = new ArrayList<>(variables);
                for (JCTree.JCStatement s : body.stats) {
                    listBuffer.addAll(processEachStatement(s, list, clazz));
                }
                ((JCTree.JCBlock) jcWhileLoop.body).stats = listBuffer.toList();
                return com.sun.tools.javac.util.List.of(jcWhileLoop);
            }
            //do {} while(true) {}
            if (statement instanceof JCTree.JCDoWhileLoop) {
                ListBuffer<JCTree.JCStatement> listBuffer = new ListBuffer<JCTree.JCStatement>();
                JCTree.JCDoWhileLoop jcDoWhileLoop = (JCTree.JCDoWhileLoop) statement;
                JCTree.JCBlock body = (JCTree.JCBlock) jcDoWhileLoop.body;
                java.util.List<String> list = new ArrayList<>(variables);
                for (JCTree.JCStatement s : body.stats) {
                    //processing block body each statement
                    listBuffer.addAll(this.processEachStatement(s, list, clazz));
                }
                ((JCTree.JCBlock) jcDoWhileLoop.body).stats = listBuffer.toList();
                return com.sun.tools.javac.util.List.of(jcDoWhileLoop);
            }
            // try {} catch (Exception e){} finally {}
            if (statement instanceof JCTree.JCTry) {
                ListBuffer<JCTree.JCStatement> listBuffer = new ListBuffer<JCTree.JCStatement>();
                JCTree.JCTry jcTry = (JCTree.JCTry) statement;
                JCTree.JCBlock body = jcTry.body;
                java.util.List<String> list = new ArrayList<>(variables);
                for (JCTree.JCStatement s : body.stats) {
                    listBuffer.addAll(processEachStatement(s, list, clazz));
                }
                jcTry.body.stats = listBuffer.toList();

                //处理catch
                ListBuffer<JCTree.JCCatch> newCatches = new ListBuffer<>();
                com.sun.tools.javac.util.List<JCTree.JCCatch> catches = jcTry.getCatches();
                for (JCTree.JCCatch jcCatch : catches) {
                    ListBuffer<JCTree.JCStatement> jcCatchListBuffer = new ListBuffer<JCTree.JCStatement>();
                    JCTree.JCBlock jcCatchBody = jcCatch.body;
                    java.util.List<String> jcCatchVarList = new ArrayList<>(variables);
                    for (JCTree.JCStatement s : jcCatchBody.stats) {
                        jcCatchListBuffer.addAll(processEachStatement(s, jcCatchVarList, clazz));
                    }
                    jcCatchBody.stats = jcCatchListBuffer.toList();
                    newCatches.add(jcCatch);
                }
                jcTry.catchers = newCatches.toList();
                //处理finally
                JCTree.JCBlock jcFinally = jcTry.finalizer;
                if (jcFinally != null) {
                    ListBuffer<JCTree.JCStatement> jcFinallyListBuffer = new ListBuffer<JCTree.JCStatement>();
                    java.util.List<String> jcFinallyList = new ArrayList<>(variables);
                    for (JCTree.JCStatement s : jcFinally.stats) {
                        jcFinallyListBuffer.addAll(processEachStatement(s, jcFinallyList, clazz));
                    }
                    jcFinally.stats = jcFinallyListBuffer.toList();
                    jcTry.finalizer = jcFinally;
                }
                return com.sun.tools.javac.util.List.of(jcTry);
            }

            //{int a = 0;System.out.println("good");}
            if (statement instanceof JCTree.JCBlock){
                ListBuffer<JCTree.JCStatement> listBuffer = new ListBuffer<>();
                JCTree.JCBlock jcBlock = (JCTree.JCBlock) statement;
                java.util.List<String> list = new ArrayList<>(variables);
                for (JCTree.JCStatement s : jcBlock.stats) {
                    //processing block body each statement
                    listBuffer.addAll(this.processEachStatement(s, list, clazz));
                }
                jcBlock.stats = listBuffer.toList();
                return com.sun.tools.javac.util.List.of(jcBlock);
            }

            clazz.getBreakPointVariables().add(result); //上一次变量
            List<JCTree.JCStatement> r = null;
            synchronized (AstCallable.class) {
                treeMaker.pos = statement.pos;
                r = com.sun.tools.javac.util.List.of(this.insertNewStatement(pointIndex++, result), statement);
            }
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return com.sun.tools.javac.util.List.of(statement);
    }

    public void variableDeclare(JCTree.JCStatement statement, List<String> variables){
        JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) statement;
        if (jcVariableDecl.init != null) {
            variables.add(jcVariableDecl.name.toString()); //['str']
        }
    }

    public void expressStatement(JCTree.JCStatement statement, List<String> variables){
        JCTree.JCExpressionStatement jcExpressionStatement = (JCTree.JCExpressionStatement) statement;
        if (jcExpressionStatement.expr instanceof JCTree.JCAssign) {
            JCTree.JCAssign jcAssign = (JCTree.JCAssign) jcExpressionStatement.expr;
            //lhs赋值语句左边表达式
            //rhs赋值语句右边表达式
            String left = jcAssign.lhs.toString();
            int end = left.length();
            end = !left.contains("[") ? end : left.indexOf("[");
            String name = left.substring(0, end);
            if (!variables.contains(name)) {
                variables.add(name);
            }
        }
    }

    public synchronized JCTree.JCStatement insertNewStatement(int breakPointIndex, java.util.List<String> result) {
        ListBuffer<JCTree.JCExpression> vars = new ListBuffer<JCTree.JCExpression>();
        TreeMaker treeMaker = CompilerProcessor.treeMaker;
        vars.add(treeMaker.Literal(breakPointIndex)); //断点数
        if(methodName != null && !"".equals(methodName)) {
            vars.add(treeMaker.Literal(methodName));
        } else {
            vars.add(treeMaker.Literal(TypeTag.BOT, null));
        }
        if(innerClassName != null && !"".equals(innerClassName)) {
            vars.add(treeMaker.Literal(innerClassName));
        } else {
            vars.add(treeMaker.Literal(""));
        }
        vars.add(treeMaker.Literal(classFullQualifiedName));//
        vars.add(treeMaker.Literal(methodCount));//方法数量
        if (ifStaticMethod) {
            vars.add(treeMaker.Literal(TypeTag.BOT, null));
        } else {
            vars.add(treeMaker.Ident(CompilerProcessor.elementUtils.getName("this")));
        }
        result.forEach(str -> {
            if (str.contains(".")) {
                vars.add(memberAccess(str));
            } else {
                vars.add(treeMaker.Ident(CompilerProcessor.elementUtils.getName(str)));
            }
        });
        return treeMaker.Exec(treeMaker.Apply(com.sun.tools.javac.util.List.nil(), memberAccess("co.xiaowangzi.debug.runtime.BreakPointExecutor.execute"), vars.toList()));
    }

    private JCTree.JCExpression memberAccess(String components) {
        TreeMaker treeMaker = CompilerProcessor.treeMaker;
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(this.getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, this.getNameFromString(componentArray[i]));
        }
        return expr;
    }

    private Name getNameFromString(String s) {
        Name.Table names = CompilerProcessor.names;
        return names.fromString(s);
    }

    private void initJavacClassFields(Symbol.ClassSymbol classSymbol, boolean thisClass, Clazz clazz, boolean outerClass, String classname) {
        Iterator<Symbol> iterator = classSymbol.members().getElements().iterator();
        try {
            while (iterator.hasNext()) {
                Symbol next = iterator.next();
                if (next.getKind().isField()) {
                    //如果是保护的或者公共的才行
                    if (thisClass) {
                        if (next.getModifiers().contains(Modifier.STATIC)) {
                            //full qualified field name
                            if (outerClass) {
                                clazz.getFields().add(classname + "." + next.name);
                            } else {
                                clazz.addInnerFields(classname, classname + "." + next.name);
                            }
                        } else {
                            if (outerClass) {
                                clazz.getFields().add("this." + next.name);
                            } else {
                                clazz.addInnerFields(classname, "this." + next.name);
                            }
                        }
                    } else {
                        //父类的字段
                        if (next.getModifiers().contains(Modifier.PROTECTED) || next.getModifiers().contains(Modifier.PUBLIC)) {
                            //静态
                            if (next.getModifiers().contains(Modifier.STATIC)) {
                                //外部类的父类的字段
                                if (outerClass) {
                                    clazz.getFields().add("super.static." + next.name);
                                } else {
                                    //内部类的父类的字段
                                    //clazz.addInnerFields(classname, classname + "." + next.name);
                                }
                            } else {
                                //外部类的父类的字段
                                if (outerClass) {
                                    clazz.getFields().add("super.instance." + next.name);
                                } else {
                                    //内部类的父类的字段
                                    //clazz.addInnerFields(classname, "super.instance." + next.name);
                                }
                            }
                        }
                    }
                }
            }
            //tsym === ClassSymbol (The defining class / interface / package / type variable.)
            if (thisClass && classSymbol.getSuperclass().tsym != null) {
                this.initJavacClassFields((Symbol.ClassSymbol) classSymbol.getSuperclass().tsym, false, clazz, outerClass, classname);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
