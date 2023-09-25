package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.BreakPoint;
import co.xiaowangzi.debug.clazz.Clazz;
import co.xiaowangzi.debug.log.Log;
import co.xiaowangzi.debug.utils.*;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.jexl3.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

//需要保护好自己
public class RuntimeContext {

    /* Current point */
    protected static volatile BreakPoint lastBreakPoint;

    /* Support Debug all classes */
    protected static volatile Map<String, Clazz> clazzMap;

    protected static volatile Dish dish = Dish.silent;

    protected static volatile boolean debuging = false;

    protected static volatile boolean muteAllBreakPoints = false;

    protected static volatile ClazzTree clazzTree;

    protected static volatile boolean isChanging = false;

    protected static final Object lock = new Object();

    static {
        //Load all compiled classes.
        clazzMap = ClazzLoader.loadClasses();
        clazzTree = new ClazzTree(new ClazzNode("Root", "root"));
        for (Map.Entry<String, Clazz> entry : clazzMap.entrySet()) {
            clazzTree.addElement(entry.getValue().getModuleName() + "/src/main/java/" + entry.getValue().getClassFullQualifiedName().replaceAll("\\.", "/") + ".java");
        }
    }

    public static void initStatic(){
        //加载一下类
    }

    public static String jarName(Class clazz) throws URISyntaxException {
        String jarPath = clazz
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();

        // Get name of the JAR file
        return jarPath.substring(jarPath.lastIndexOf("/") + 1);
    }

    /**
     * 断点只有增加或者删除，系统运行无法自行删除断点或者增加断点
     * 在自然运行中，如果没有任何断点，则直接运行到结束
     * 在有断点时，运行到该断点while true，如果遇上for循环，该断点处循环依然能够被持续执行。所以不能在执行完一个断点后，就将其删除
     *
     */
    protected static void preExecute(BreakPoint breakPoint) {
        if(dish == Dish.resume){
            //nothing do.
        }

        if(dish == Dish.stepInto){
            if(breakPoint.getClazz().containRuntimeBreakPoint(breakPoint)){
                breakPoint.setShouldRun(1);
            } else {
                breakPoint.setShouldRun(2);
            }
        }

        if (dish == Dish.stepOver) {
            //下一步，到该类的下一行，如果方法结束，则直接断点到下一个断点，如果没有下一个断点，则运行到结束
            if(BreakPoint.sameClassMethod(lastBreakPoint, breakPoint)){
                if(breakPoint.getClazz().containRuntimeBreakPoint(breakPoint)){
                    breakPoint.setShouldRun(1);
                } else {
                    breakPoint.setShouldRun(2);
                }
            } else{
                //进入到下一个
                return;
            }
        }

        if(dish == Dish.stepOut){
            //step out，方法内有断点，则到下一个断点，如果没有断点，则到方法结束，也就是会自动在方法结束的地方增加一个断点。
            //首先不包含次断点，运行到断点结束
            if(!breakPoint.getClazz().containRuntimeBreakPoint(breakPoint)){
                //如果是同一个方法，则return
                if(BreakPoint.sameClassMethod(lastBreakPoint, breakPoint)){
                    return;
                } else {
                    //不是同一个方法，则应该断
                    breakPoint.setShouldRun(2);
                }
            }
            //如果方法不一样？
        }

        if(breakPoint.getClazz().containRuntimeBreakPoint(breakPoint)){
            //如果包含此断点，则线程此处循环
            breakPoint.setShouldRun(1);
        }

        dish = Dish.silent;
        if(threadShouldStopAtPoint(breakPoint)){
            isChanging = true;
        }
    }

    /**
     * 处理当前断点，一直在此处循环
     */
    protected static void executeCurrentPoint(BreakPoint breakPoint) {
        debuging = true;

        if(lastBreakPoint == null){
            lastBreakPoint = breakPoint;
        }

        threadDelay();

        switch (dish) {
            case resume:
                resume(breakPoint);
                return;
            case stepOver:
                stepOver(breakPoint);
                return;
            case stepInto:
                stepInto(breakPoint);
                return;
            case stop:
                stop(breakPoint);
                return;
            case flush:
                flush(breakPoint);
                return;
            case stepOut:
                stepOut(breakPoint);
                return;
            default:
                break;
        }

        //当前线程的名称
        breakPoint.setThreadName(Thread.currentThread().getName());
        //更新当前正在执行的断点
        if(!BreakPoint.sameBreakPoint(lastBreakPoint, breakPoint)){
            lastBreakPoint = breakPoint;
        }
        //执行完后静默，等待新的dish
        dish = Dish.silent;
    }

    private static void stepOut(BreakPoint breakPoint) {
        //循环停止
        breakPoint.setShouldRun(0);
    }

    private static void flush(BreakPoint breakPoint) {

        dish = Dish.silent;
        breakPoint.setShouldRun(0);
        lastBreakPoint = null;
    }

    private static void stop(BreakPoint breakPoint) {
        try {
            Thread.currentThread().stop();
        } catch (Exception e){}
    }

    protected static void afterExecute(BreakPoint breakPoint) {
        debuging = false;
        isChanging = false;
        //如果是循环，则需要
        if(breakPoint.getClazz().containRuntimeBreakPoint(breakPoint)){
            breakPoint.setShouldRun(1);
        } else {
            breakPoint.setShouldRun(2);
        }
    }

    private static void stepOver(BreakPoint breakPoint) {
        //循环停止
        breakPoint.setShouldRun(0);
    }

    private static void stepInto(BreakPoint breakPoint) {
        //循环停止
        breakPoint.setShouldRun(0);
    }

    private static void resume(BreakPoint breakPoint) {
        //循环停止
        breakPoint.setShouldRun(0);
        lastBreakPoint = null;
    }

    private static void threadDelay() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException ignored) { }
    }

    /**
     * 展示当前堆栈信息和变量
     */
    private static List<Variable> executeShowFramesAndVariables(BreakPoint currentBreakPoint) {
        try {
            List<Variable> localVariables = breakPointLocalVariables(currentBreakPoint);
            List<Variable> fields = clazzFieldsVariables(currentBreakPoint);
            fields.addAll(localVariables);
            return fields;
        } catch (Exception e) {
            Log.error("获取变量的值异常", e);
        }
        return null;
    }

    public static List<Variable> clazzFieldsVariables(BreakPoint currentBreakPoint){
        boolean innerClass;
        List<String> fieldsVariables = null;

        if(!StringUtils.isEmpty(currentBreakPoint.getInnerClassName())){
            //如果是内部类，则内部类加入
            List<String> innerClassFields = currentBreakPoint.getClazz().getInnerClassFields().get(currentBreakPoint.getInnerClassName());
            if(innerClassFields != null && innerClassFields.size() > 0){
                fieldsVariables = innerClassFields;
            }
            innerClass = true;
        } else {
            List<String> fields = currentBreakPoint.getClazz().getFields();
            if(fields != null && fields.size() > 0){
                fieldsVariables = currentBreakPoint.getClazz().getFields();
            }
            innerClass = false;
        }

        List<Variable> variables = new ArrayList<>();
        if(fieldsVariables == null || fieldsVariables.size() <= 0){
            return variables;
        }

        for(String variable : fieldsVariables){
            //static方法过滤this变量
            if(variable.startsWith("this.") && currentBreakPoint.getStaticMethod()){
                continue;
            }
            //父类同理
            if(variable.startsWith("super.") && currentBreakPoint.getStaticMethod() && variable.contains(".instance.")){
                continue;
            }
            Variable variableObj = new Variable();
            variableObj.setFullName(variable);
            variableObj.setName(variableShortName(variable, currentBreakPoint.getClassFullQualifiedName()));
            variableObj.setValue(fieldsVariableValue(currentBreakPoint, variable, innerClass));
            if(variableObj.getValue() != null){
                variableObj.setObject(!isPrimitiveOrPrimitiveWrapperOrString(variableObj.getValue().getClass()));
                variableObj.setList(variableObj.getValue() instanceof List);
                variableObj.setValue(variableObj.getValue() != null ? variableObj.getValue().toString() : null);
            }
            variables.add(variableObj);
        }

        return variables;
    }

    public static List<Variable> breakPointLocalVariables(BreakPoint breakPoint){
        Object[] localVariables = breakPoint.getVariables();
        List<Variable> localVariableValues = new ArrayList<>();

        List<String> localVariablesName = breakPoint.getClazz().getBreakPointVariables().get(breakPoint.getPointIndex());
        if(CollectionUtils.isEmpty(localVariablesName) || localVariables == null || localVariables.length <= 0){
            Log.info("当前断点无任何局部变量");
            return localVariableValues;
        }

//        Log.warn("all breakpoint " + breakPoint.getClazz().getBreakPointVariables().toString());

        if(localVariables.length != localVariablesName.size()){
            Log.warn("持久化class中写入的局部变量数量与断点的局部变量数量不匹配" + "，断点" + breakPoint.getPointIndex() + "，局部变量名称列表 " + localVariablesName.toString() + ", 局部变量值列表 " + Arrays.toString(localVariables));
        }

        for(String localVariableName : localVariablesName){
            final int index = breakPoint.getClazz().getBreakPointVariables().get(breakPoint.getPointIndex()).indexOf(localVariableName.trim());
            if(index == -1){
                Log.error("变量名【" + localVariableName + "】在持久化中未找到对应的变量名称");
                continue;
            }
            if(localVariables.length < index + 1){
                Log.error("变量名【" + localVariableName + "】虽然在持久化中存在，但在执行上下文中已越界, 索引 " + index + "，断点" + breakPoint.getPointIndex() + "，局部变量名称列表 " + localVariablesName.toString() + ", 局部变量值列表 " + Arrays.toString(localVariables));
                continue;
            }
            Object value = breakPoint.getVariables()[index];
            Variable variableObj = new Variable();
            variableObj.setFullName(localVariableName);
            variableObj.setName(variableShortName(localVariableName, breakPoint.getClassFullQualifiedName()));
            variableObj.setValue(value == null ? null : value.toString());
            variableObj.setObject(value != null && !isPrimitiveOrPrimitiveWrapperOrString(value.getClass()));
            variableObj.setList(value != null && variableObj.getValue() instanceof List);
            localVariableValues.add(variableObj);
        }
        return localVariableValues;
    }

    private static Object localVariableValue(BreakPoint breakPoint, String localVariableName){
        Object[] localVariables = breakPoint.getVariables();
        int index = breakPoint.getClazz().getBreakPointVariables().get(breakPoint.getPointIndex()).indexOf(localVariableName.trim());
        if(index == -1){
            Log.error("变量名【" + localVariableName + "】在持久化中未找到对应的变量名称");
            return null;
        }
        if(localVariables.length < index + 1){
            Log.error("变量名【" + localVariableName + "】虽然在持久化中存在，但在执行上下文中已越界");
            return null;
        } else {
            return breakPoint.getVariables()[index];
        }
    }

    private static String variableShortName(String variable, String classFullQualifiedName) {
        if(variable.startsWith("this")){
            return variable.replace("this.", "");
        }
        if(variable.startsWith(classFullQualifiedName)){
            return variable.replace(classFullQualifiedName + ".", "");
        }
        return variable;
    }

    /**
     * 是否是普通类型或者普通包装类型，以及String类型
     */
    public static boolean isPrimitiveOrPrimitiveWrapperOrString(Class<?> type) {
        return (type.isPrimitive() && type != void.class) ||
                type == Double.class || type == Float.class || type == Long.class ||
                type == Integer.class || type == Short.class || type == Character.class ||
                type == Byte.class || type == Boolean.class || type == String.class;
    }

    /**
     * 查询变量
     */
    protected static List<Variable> queryVariables(){
        return executeShowFramesAndVariables(lastBreakPoint);
    }

    /**
     * 是否处于debug中
     */
    protected static boolean isDebuging(){
        return debuging;
    }

    /**
     * 展示变量
     */
    private static Object fieldsVariableValue(BreakPoint breakPoint, String variable, boolean innerClass){
        try {
            //获取当前类
            Object value = null;
            final String trim = variable.substring(variable.lastIndexOf(".") + 1).trim();
            if (variable.startsWith("this.")) {
                Class<?> aClass = Class.forName(innerClass ? breakPoint.getInnerClassName() : breakPoint.getClassFullQualifiedName());
                Field declaredField = aClass.getDeclaredField(trim);
                declaredField.setAccessible(true);
                value = declaredField.get(breakPoint.getThees());
                return value;
            }
            if (variable.startsWith("super.")) {
                Class<?> aClass = Class.forName(breakPoint.getClazz().getParentClassFullQualifiedName());
                Field declaredField = aClass.getDeclaredField(trim);
                declaredField.setAccessible(true);
                if(variable.contains(".static.")){
                    value = declaredField.get(null);
                }
                if(variable.contains(".instance.")){
                    value = declaredField.get(breakPoint.getThees());
                }
                return value;
            }
            Class<?> aClass = Class.forName(variable.substring(0, variable.lastIndexOf(".")));
            Field declaredField = aClass.getDeclaredField(trim);
            declaredField.setAccessible(true);
            value = declaredField.get(null);
            return value;
        } catch (Exception e) {
            Log.error("获取变量【" + variable + "】的值异常， 异常原因", e);
        } finally { }
        return null;
    }

    public static Pair<Boolean, String> jsonVariable(String variableName){
        final Object value;
        if(!variableName.contains(".")){
            value = localVariableValue(lastBreakPoint, variableName);
        } else {
            value = fieldsVariableValue(lastBreakPoint, variableName, !StringUtils.isEmpty(lastBreakPoint.getInnerClassName()));
        }
        if(value == null){
            return Pair.of(false, "变量" + variableName + "的值可能为空或者获取失败");
        }
        String jsonValue;
        boolean good = false;
        try {
            jsonValue = JacksonPrinter.write2JsonStr(value);
            good = true;
        } catch (Exception e){
            jsonValue = "变量" + variableName + "的值可能存在循环引用或其他原因导致Json失败";
        }
        return Pair.of(good, jsonValue);
    }

    /**
     * 线程在该断点处是否应该停止
     */
    public static boolean threadShouldStopAtPoint(BreakPoint breakPoint) {
        boolean containBreakPoint = breakPoint.getClazz().containRuntimeBreakPoint(breakPoint);
        if(containBreakPoint){
            //替换breakPoint，补充breakPoint的一些数据
            breakPoint.getClazz().replaceRuntimeBreakPoint(breakPoint);
            //此处不能直接返回true，否则在该断点因为contain会一直while true
        }
        if((breakPoint.getShouldRun() == 1 || breakPoint.getShouldRun() == 2) && parseExpression(breakPoint)){
            return true;
        }
        return false;
    }

    /**
     * 解析breakpoint表达式
     */
    public static Boolean parseExpression(BreakPoint breakPoint) {

        if (StringUtils.isEmpty(breakPoint.getExpression())) {
            return true;
        }
        JexlEngine jexl = new JexlBuilder().create();
        JexlContext context = new MapContext();
        // 往表达式引擎上下文中塞入breakpoint的所有变量值
        List<Variable> variables = executeShowFramesAndVariables(breakPoint);

        for (Variable variable : variables) {
            Object value = variable.getValue();
            if (value instanceof String) {
                value = parseString((String) value);
            }
            context.set(variable.getName(), value);
        }
        // 解析表达式
        JexlExpression jexlExpression = jexl.createExpression(breakPoint.getExpression());
        Object evaluate = jexlExpression.evaluate(context);
        if (evaluate instanceof Boolean) {
            return (Boolean) evaluate;
        } else {
            return false;
        }
    }

    /**
     * String 转 map或者list
     */
    public static Object parseString(String string) {
        if (string.startsWith("{")) {
            string = string.replace(" ", "");
            String[] keyValuePairs = string.substring(1, string.length() - 1).split(",");
            Map map = new HashMap<>();
            for (String pair : keyValuePairs) {
                String[] entry = pair.split("=");
                String key = entry[0].trim();
                String value = entry[1].trim();
                map.put(key, value);
            }
            return map;
        } else if (string.startsWith("[")) {
            string = string.replace(" ", "");
            String[] elements = string.substring(1, string.length() - 1).split(",");
            List list = new ArrayList<>();
            for (String element : elements) {
                list.add(element);
            }
            return list;
        } else {
            return string;
        }
    }
}
