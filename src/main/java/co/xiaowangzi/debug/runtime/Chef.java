package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.*;
import co.xiaowangzi.debug.log.Log;
import co.xiaowangzi.debug.utils.Id;
import co.xiaowangzi.debug.utils.Pair;
import co.xiaowangzi.debug.utils.StringUtils;

import java.util.*;

/**
 * 厨师做饭
 */
public class Chef {

    protected volatile static List<BreakPoint> runtimeBreakPointsStack = new LinkedList<>();

    /**
     * 放行
     */
    public static synchronized boolean resume(){
        RuntimeContext.dish = Dish.resume;
        Log.info("执行resume成功");
        return true;
    }

    /**
     * 进入方法，如果无法进入，则到下一个断点，否则该方法第一行自动成为断点，因为无法将下一个breakpoint加入到runtime当中，所以必须留给preExecute
     */
    public static synchronized boolean stepInto() {
        //重制操作类型
        RuntimeContext.dish = Dish.stepInto;
        Log.info("执行step into成功");
        return true;
    }

    public static synchronized boolean stepOver(){
        RuntimeContext.dish = Dish.stepOver;
        Log.info("执行step over成功");
        return true;
    }

    /**
     * 增加断点
     */
    public static synchronized boolean addBreakPoint(Pair<String, Integer> breakPoint) {
        final Clazz clazz = RuntimeContext.clazzMap.get(breakPoint.getKey());
        BreakPoint b = BreakPoint.of(breakPoint.getLeft(), breakPoint.getRight());
        b.setClazz(clazz);
        clazz.addRuntimeBreakPoint(b);
        runtimeBreakPointsStack.add(b);
        Log.info(breakPoint.getKey() + "添加断点成功");
        return true;
    }

    /**
     * 去除断点
     */
    public static synchronized boolean removeBreakPoint(Pair<String, Integer> breakPoint) {
        final Clazz clazz = RuntimeContext.clazzMap.get(breakPoint.getKey());
        BreakPoint b = BreakPoint.of(breakPoint.getLeft(), breakPoint.getRight());
        clazz.removeRuntimeBreakPoint(b);
        runtimeBreakPointsStack.remove(b);
        if(runtimeBreakPointsStack.size() == 0){
            RuntimeContext.muteAllBreakPoints = false;
        }
        Log.info(breakPoint.getKey() + "移除断点成功");
        return true;
    }

    /**
     * 当前断点的前置处理
     *
     * 用户在上一个断点时的操作，此时在当前断点下如何处理。
     *
     * 如果用户操作放行，则自动到下一个类的断点处。如果此后没有断点，则运行到结束
     * 如果用户操作进入，则标记当前是进入，那么下一个执行的方法检查标记存在，则自动成为断点
     * 如果用户操作下一步，则需要是相同方法下一步，否则执行到下一个断点。
     *
     */



    public static synchronized boolean stop(){
        Log.warn("正在执行stop，请稍等....");
        RuntimeContext.dish = Dish.stop;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
        init();
        Log.warn("stop执行完成！");
        return true;
    }

    public static synchronized boolean flush() {
        Log.warn("正在执行flush，将清除所有的断点和运行状态，请稍等....");
        init();
        RuntimeContext.dish = Dish.flush;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        Log.warn("执行flush完成！");
        return true;
    }


    public static synchronized boolean stepOut(){
        RuntimeContext.dish = Dish.stepOut;
        Log.info("执行step out成功");
        return true;
    }

    public static synchronized boolean clearAllBreakPoints(boolean resetShouldRun){
        if(runtimeBreakPointsStack.size() <= 0){
            return true;
        }
        for(BreakPoint breakPoint : runtimeBreakPointsStack){
            if(breakPoint != null){
                breakPoint.getClazz().removeRuntimeBreakPoint(breakPoint);
                if(resetShouldRun){
                    breakPoint.setShouldRun(0);
                }
            }
        }

        runtimeBreakPointsStack = new LinkedList<>();
        RuntimeContext.muteAllBreakPoints = false;
        Log.info("清空所有断点成功");

        return true;
    }

    protected static void init() {
        RuntimeContext.debuging = false;
        RuntimeContext.isChanging = false;
        RuntimeContext.muteAllBreakPoints = false;
        RuntimeContext.dish = Dish.silent;
        //移除所有的breakpoint
        Chef.clearAllBreakPoints(true);

    }

    public static synchronized boolean reRun(){
        return true;
    }

    public static Object variables() {
        BreakPoint currentBreakPoint = RuntimeContext.lastBreakPoint;
        if(currentBreakPoint == null){
            return null;
        }
        return RuntimeContext.queryVariables();
    }

    public static synchronized Object queryExecutingLog(){
        return Log.queryDebugRuntimeLog();
    }

    public static synchronized Object showAllBreakPoints(){
        List<BreakPointBridge> breakPointBridges = new ArrayList<>(runtimeBreakPointsStack.size());
        for (BreakPoint breakPoint : runtimeBreakPointsStack){
            BreakPointBridge breakPointBridge = new BreakPointBridge();
            breakPointBridge.setPointIndex(breakPoint.getPointIndex());
            breakPointBridge.setClassFullQualifiedName(breakPoint.getClassFullQualifiedName());
            breakPointBridge.setClassSimpleName(breakPoint.getClassFullQualifiedName().substring(breakPoint.getClassFullQualifiedName().lastIndexOf(".") + 1));
            breakPointBridge.setId(Id.getId(breakPoint.getClassFullQualifiedName()));
            breakPointBridge.setHasExpression(!StringUtils.isEmpty(breakPoint.getExpression()));
            if(RuntimeContext.isDebuging()){
                breakPointBridge.setDebuging(BreakPoint.sameBreakPoint(BreakPoint.of(breakPoint.getClassFullQualifiedName(), breakPoint.getPointIndex()), RuntimeContext.lastBreakPoint));
            } else {
                breakPointBridge.setDebuging(false);
            }
            breakPointBridges.add(breakPointBridge);
        }
        Collections.reverse(breakPointBridges);
        return breakPointBridges;
    }

    public static void muteAllBreakPoint() {
        RuntimeContext.muteAllBreakPoints = true;
        Log.info("mute所有断点成功");
    }

    public static void cancelMuteAllBreakPoint() {
        RuntimeContext.muteAllBreakPoints = false;
        Log.info("取消mute所有断点成功");
    }

        public static boolean isBreakPointChanged() {
        if(RuntimeContext.isChanging){
            RuntimeContext.isChanging = false;
            return true;
        }
        return false;
    }

    public static Object searching(String query) {
        List<SearchingBridge> searchingBridges = new ArrayList<>();
        for(Map.Entry<String, Clazz> entry : RuntimeContext.clazzMap.entrySet()){
            final String classFullQualifiedName = entry.getKey();
            final String classname = classFullQualifiedName.substring(classFullQualifiedName.lastIndexOf(".") + 1).trim();
            if(StringUtils.containsIgnoreCase(classname, query)){
                searchingBridges.add(SearchingBridge.of(entry.getKey(), entry.getKey()));
            }
        }
        return searchingBridges;
    }

    public static Pair<Boolean, String> debugVariableJsonPrint(String variable) {
        return RuntimeContext.jsonVariable(variable);
    }
}
