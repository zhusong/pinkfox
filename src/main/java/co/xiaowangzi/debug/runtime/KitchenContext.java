package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.DebugingContext;

public class KitchenContext implements java.io.Serializable{
    /**是否正在debuging*/
    private Boolean isDebuging;
    /**正在debug的类*/
    private String debugingClass;
    /**正在debug的断点*/
    private Integer debugingPointIndex;
    /**断点是否mute*/
    private Boolean isBreakPointsMute;
    /*日志*/
    private Object log;
    /*是否在debug过程中改变断点了*/
    private Boolean isChanging;
    /**当前正在debug的树的节点*/
    private Integer debugingClazzTreeNodeId;
    /**html源码*/
    private String html;

    private Object allBreakPoints;

    private Object variables;

    private DebugingContext debugingContext;

    public DebugingContext getDebugingContext() {
        return debugingContext;
    }

    public void setDebugingContext(DebugingContext debugingContext) {
        this.debugingContext = debugingContext;
    }

    public Object getVariables() {
        return variables;
    }

    public void setVariables(Object variables) {
        this.variables = variables;
    }

    public Object getAllBreakPoints() {
        return allBreakPoints;
    }

    public void setAllBreakPoints(Object allBreakPoints) {
        this.allBreakPoints = allBreakPoints;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Integer getDebugingClazzTreeNodeId() {
        return debugingClazzTreeNodeId;
    }

    public void setDebugingClazzTreeNodeId(Integer debugingClazzTreeNodeId) {
        this.debugingClazzTreeNodeId = debugingClazzTreeNodeId;
    }

    public Boolean getChanging() {
        return isChanging;
    }

    public void setChanging(Boolean changing) {
        isChanging = changing;
    }



    public Object getLog() {
        return log;
    }

    public void setLog(Object log) {
        this.log = log;
    }

    public String getDebugingClass() {
        return debugingClass;
    }

    public Boolean getBreakPointsMute() {
        return isBreakPointsMute;
    }

    public void setBreakPointsMute(Boolean breakPointsMute) {
        isBreakPointsMute = breakPointsMute;
    }

    public void setDebugingClass(String debugingClass) {
        this.debugingClass = debugingClass;
    }

    public Integer getDebugingPointIndex() {
        return debugingPointIndex;
    }

    public void setDebugingPointIndex(Integer debugingPointIndex) {
        this.debugingPointIndex = debugingPointIndex;
    }

    public Boolean getDebuging() {
        return isDebuging;
    }

    public void setDebuging(Boolean debuging) {
        isDebuging = debuging;
    }

}
