package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.BreakPoint;
import co.xiaowangzi.debug.clazz.Clazz;
import co.xiaowangzi.debug.utils.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 格式化代码
 */
public class SourceFormatter {

    public static final String breakPointStatementPrefix = "co.xiaowangzi.debug.runtime.BreakPointExecutor.execute(";
    public static final int breakPointStatementPrefixLength = breakPointStatementPrefix.length();

    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static String source(String classFullQualifiedName){
        return format(RuntimeContext.clazzMap.get(classFullQualifiedName));
    }

    /**
     * 将下面的代码
     *
     * <pre>
     *
     * public class Simple {
     *     public void sum(int a, int b){
     *         System.out.println(a);
     *     }
     * }
     *
     * </pre>
     *
     * 格式化成：
     *
     * <pre>
     *
     * public class Simple {
     *
     *   public Simple() {
     *     super();
     *   }
     *
     *   public void sum(int a, int b) {
     *       <div class='point-line-base point-line-normal '><span class='point-normal point-base' onchange='handleChange(event)' id='point|com.jd.debug.tools.web.test.Simple|0'></span>System.out.println(a);</div>
     *   }
     * }
     *
     * </pre>
     */
    public static String format(Clazz clazz){
        if(clazz == null){
            RuntimeContext.initStatic();
            return null;
        }

        if(!StringUtils.isEmpty(clazz.getAfterParseStableHtmlSource())){
            //有稳定版本，则format运行时
            return formatRuntimeHtmlSource(clazz);
        } else {
            //没稳定版本，则异步获取稳定版本
            asyncClassStableHtmlSource(clazz);
        }

        //否则获取运行时稳定版本
        String astSource = clazz.getHtmlSource();

        int begin = astSource.indexOf(breakPointStatementPrefix);
        int end = astSource.indexOf(";", begin) + 1;

//        如果行数太多怎么整
        while (begin != -1 && end != -1) {
            while (astSource.charAt(end) == '\n' || astSource.charAt(end) == '\r' || astSource.charAt(end) == ' ') {
                astSource = astSource.substring(0, end) + astSource.substring(end + 1);
            }
            // 断点整条语句
            String breakPointStatement = astSource.substring(begin, end);
            // 获取每一行的pointIndex
            final int pointIndex = Integer.parseInt(breakPointStatement.substring(breakPointStatement.indexOf(breakPointStatementPrefix) + breakPointStatementPrefixLength, breakPointStatement.indexOf(",", breakPointStatement.indexOf(breakPointStatementPrefix) + breakPointStatementPrefixLength)));
            final int status =
                    //断点中
                    thisPointIndexExecuting(pointIndex, clazz) ? 2 :
                    //仅选中
                    thisPointIndexSelected(pointIndex, clazz) ? 1 : 0;
            String checkBoxStatement = pointBreakPoint(status, pointIndex,  clazz);
            // 替换checkbox语句
            astSource = astSource.replace(breakPointStatement, checkBoxStatement);
            // 替换checkbox语句后的第一个";"成"</div>;"
            int checkBoxStatementIndex = astSource.indexOf(checkBoxStatement);
            astSource = StringUtils.replaceFirstFrom(astSource, checkBoxStatementIndex, ";", ";</div>");

            begin = astSource.indexOf(breakPointStatementPrefix);
            end = astSource.indexOf(";", begin) + 1;
        }
        astSource = StringUtils.unicodeToStr(astSource);

        return astSource;
    }

    private static void asyncClassStableHtmlSource(Clazz clazz) {
        executorService.submit(() -> clazz.setAfterParseStableHtmlSource(classStableHtmlSource(clazz)));
    }

    private static String formatRuntimeHtmlSource(Clazz clazz) {
        String classStableHtmlSource = clazz.getAfterParseStableHtmlSource();
        for (int i = 0; i < clazz.getBreakPointVariables().size(); i++){
            int status = thisPointIndexExecuting(i, clazz) ? 2 : thisPointIndexSelected(i, clazz) ? 1 : 0;
            if(status != 0){
                String newString = pointBreakPoint(status, i, clazz);
                classStableHtmlSource = classStableHtmlSource.replace(rawString(i, clazz), newString);
            }
        }
        return classStableHtmlSource;
    }

    public static String pointBreakPoint(int status, int pointIndex, Clazz clazz){
        final String pointCss = status == 2 ? "point-debuging" : status == 1 ? "point-selected" : "point-normal";
        final String pointLineCss = status == 2 ? "point-line-executing" : status == 1 ? "point-line-selected" : "point-line-normal";
        return "<div class='point-line-base " + pointLineCss + " '><span class='" +  pointCss + " point-base' oncontextmenu='breakPointClicked(event)' onclick='breakPointClicked(event)' status=" + status + " id='point|" + clazz.getClassFullQualifiedName() + "|" + pointIndex + "'></span>";
    }

    private static String classStableHtmlSource(Clazz clazz){
        String astSource = clazz.getHtmlSource();

        int begin = astSource.indexOf(breakPointStatementPrefix);
        int end = astSource.indexOf(";", begin) + 1;

        while (begin != -1 && end != -1) {
            while (astSource.charAt(end) == '\n' || astSource.charAt(end) == '\r' || astSource.charAt(end) == ' ') {
                astSource = astSource.substring(0, end) + astSource.substring(end + 1);
            }
            // 断点整条语句
            String breakPointStatement = astSource.substring(begin, end);
            // 获取每一行的pointIndex
            final int pointIndex = Integer.parseInt(breakPointStatement.substring(breakPointStatement.indexOf(breakPointStatementPrefix) + breakPointStatementPrefixLength,
                    breakPointStatement.indexOf(",",
                    breakPointStatement.indexOf(breakPointStatementPrefix) + breakPointStatementPrefixLength)));
            //parse成stable
            astSource = formatEachStatement(astSource, pointIndex, breakPointStatement, clazz);

            //下一个
            begin = astSource.indexOf(breakPointStatementPrefix);
            end = astSource.indexOf(";", begin) + 1;
        }
        astSource = StringUtils.unicodeToStr(astSource);
        return astSource;
    }

    private static String rawString(int pointIndex, Clazz clazz){
        return pointBreakPoint(0, pointIndex, clazz);
    }

    private static String formatEachStatement(String astSource, int pointIndex, String breakPointStatement, Clazz clazz){
        final String rawString = rawString(pointIndex, clazz);
        astSource = astSource.replace(breakPointStatement, rawString);
        int stableDivIndex = astSource.indexOf(rawString);
        // 替换stableDivIndex语句后的第一个";"成"</div>;"
        return StringUtils.replaceFirstFrom(astSource, stableDivIndex, ";", ";</div>");
    }

    public static String stableDiv(int pointIndex){
        return "<debug point=" + pointIndex + "</debug>";
    }

    private static boolean thisPointIndexSelected(int pointIndex, Clazz clazz) {
        return clazz.containRuntimeBreakPoint(BreakPoint.of(clazz.getClassFullQualifiedName(), pointIndex));
    }

    private static boolean thisPointIndexExecuting(int pointIndex, Clazz clazz) {
        if(RuntimeContext.lastBreakPoint == null || !RuntimeContext.isDebuging()){
            return false;
        }
        //断点的不是同一个
        if(!RuntimeContext.lastBreakPoint.getClassFullQualifiedName().equals(clazz.getClassFullQualifiedName())){
            return false;
        }
        if((RuntimeContext.lastBreakPoint.getShouldRun() == 1 || RuntimeContext.lastBreakPoint.getShouldRun() == 2) && RuntimeContext.lastBreakPoint.getPointIndex() == pointIndex){
            return true;
        }

        return BreakPoint.sameBreakPoint(clazz.getBreakPoint(pointIndex), RuntimeContext.lastBreakPoint);
    }
}
