package co.xiaowangzi.debug.compiletime;

import com.google.auto.service.AutoService;
import co.xiaowangzi.debug.clazz.Clazz;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@AutoService(Processor.class)
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CompilerProcessor extends AbstractProcessor {

    public static TreeMaker treeMaker;
    public static Name.Table names;
    public static Context context;
    public static JavacElements elementUtils;
    public static CopyOnWriteArrayList<Element> elements;
    public boolean lock = false;
    public static Integer threadNum = Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        //elementUtils use to process element
        elementUtils = (JavacElements) processingEnv.getElementUtils();
        context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context).table;
    }


    public synchronized static TreeMaker getTreeMaker(){
        return treeMaker;
    }

    //class sync
    public synchronized static Element getElement() {
        Element element = null;
        try {
            element = elements.get(0);
            elements.remove(0);
        } catch (Exception e) {
            return null;
        }
        return element;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final String pinkfox = System.getProperty("pinkfox");
        if(!"true".equals(pinkfox)){
            return true;
        }
        try {
            if(lock){
                return false;
            }
            lock = true;
            //Processor.process could be called multiple times (e.g. if compiled sources contains a multiple annotations).
            elements = new CopyOnWriteArrayList<>(roundEnv.getRootElements());

            //并行处理
            Map<String, Clazz> clazzMap = new ConcurrentHashMap<>();
            List<Future<Map<String, Clazz>>> futureList = new ArrayList<>();
            for (int i = 0; i < threadNum; i++) {
                Future<Map<String, Clazz>> future = executorService.submit(new AstCallable());
                futureList.add(future);
            }
            for(Future<Map<String, Clazz>> future : futureList){
                clazzMap.putAll(future.get());
            }

            //filer use to create a new file
            FileObject resource = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/services/co.xiaowangzi.debug.ClassMap");
            OutputStream outputStream = resource.openOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(clazzMap);
            oos.flush();
            oos.close();
            outputStream.close();

            executorService.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


}
