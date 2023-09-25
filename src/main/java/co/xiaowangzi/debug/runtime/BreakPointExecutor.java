package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.BreakPoint;
import co.xiaowangzi.debug.clazz.Clazz;
import co.xiaowangzi.debug.log.Log;
import org.omg.SendingContext.RunTime;

import java.lang.reflect.Field;


public class BreakPointExecutor {

    public static void execute(Integer pointIndex, String methodName, String innerClassName, String classFullQualifiedName, Integer methodIndex, Object thees, Object ... variables) {
        try {
            if (RuntimeContext.muteAllBreakPoints) {
                return;
            }

            //没有断点，则不浪费时间
            if (Chef.runtimeBreakPointsStack.size() == 0) {
                return;
            }

            BreakPoint breakPoint = BreakPoint.of(pointIndex, methodName, classFullQualifiedName, methodIndex, thees, variables);
            breakPoint.setInnerClassName(innerClassName);

            Clazz clazz = RuntimeContext.clazzMap.get(breakPoint.getClassFullQualifiedName());
            if (clazz == null) { //如果clazz为空，则表明，属于非debug支持的类
                return;
            }
            breakPoint.setClazz(clazz);
            try {
                clazz.setRuntimeClass(Class.forName(classFullQualifiedName));
            } catch (Exception e) {
                Log.error("class full qualified name " + classFullQualifiedName + ", break point " + breakPoint + ", set runtime class exception", e);
            }
            //防止其他线程，没有任何断点，在执行BreakPoint.execute时更新dish，如果此处没有断点，并且dish是resume或者silent，直接返回
            if (!breakPoint.getClazz().containRuntimeBreakPoint(breakPoint) && !sameThread()) {
                return;
            }
            synchronized (RuntimeContext.lock) {
                //前置处理
                RuntimeContext.preExecute(breakPoint);
                try {
                    //如果当前有断点，则一直在这里执行，但不阻塞，while循环
                    while (RuntimeContext.threadShouldStopAtPoint(breakPoint)) {
                        //执行
                        RuntimeContext.executeCurrentPoint(breakPoint);
                    }
                } catch (Exception e) {
                    Log.error("执行DEBUG断点发生异常，原因", e);
                }
                RuntimeContext.afterExecute(breakPoint);
            }
        } catch (Exception e){
            Log.error("执行DEBUG断点发生异常，原因", e);
        }
    }
    public static boolean sameThread(){
        if(RuntimeContext.lastBreakPoint == null){
            return false;
        }
        return RuntimeContext.lastBreakPoint.getThreadName().equals(Thread.currentThread().getName());
    }

    public static StackFrame newStackFrame(Integer pointIndex, String methodName, String classFullQualifiedName, Object thees, Object ... variables){
        StackFrame stackFrame = new StackFrame();
        stackFrame.setThees(thees);
        stackFrame.setPointIndex(pointIndex);
        stackFrame.setVariables(variables);
        stackFrame.setMethodName(methodName);
        stackFrame.setClassFullQualifiedName(classFullQualifiedName);
        return stackFrame;
    }

    public static void main(String[] args) {
    }
}
