package co.xiaowangzi.debug.clazz;

import java.io.Serializable;
import java.util.*;

public class Clazz implements Serializable {

    /**源码*/
    private String htmlSource;
    private String rawSourceString;
    private String afterParseStableHtmlSource;

    /**类的运行时断点*/
    private final Set<BreakPoint> runtimeBreakPoints = new HashSet<>();

    //每个断点的可见局部变量
    private List<List<String>> breakPointVariables;
    //本类字段
    private List<String> fields;
    //本类全限定名
    private String classFullQualifiedName;
    //父类全限定名
    private String parentClassFullQualifiedName;
    //所在jar的名称
    private String moduleName;
    //内部类的属性
    private Map<String, List<String>> innerClassFields = new HashMap<>();

    private transient Class<?> runtimeClass;

    public Class<?> getRuntimeClass() {
        return runtimeClass;
    }

    public void setRuntimeClass(Class<?> runtimeClass) {
        this.runtimeClass = runtimeClass;
    }

    public String getRawSourceString() {
        return rawSourceString;
    }

    public void setRawSourceString(String rawSourceString) {
        this.rawSourceString = rawSourceString;
    }

    public Clazz(){}

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Set<BreakPoint> getRuntimeBreakPoints() {
        return runtimeBreakPoints;
    }

    public boolean removeRuntimeBreakPoint(BreakPoint breakPoint){
        if(!breakPoint.getClassFullQualifiedName().equals(classFullQualifiedName)){
            return false;
        }
        breakPoint.setShouldRun(0);
        return runtimeBreakPoints.remove(breakPoint);
    }

    public boolean addRuntimeBreakPoint(BreakPoint breakPoint){
        if(!breakPoint.getClassFullQualifiedName().equals(classFullQualifiedName)){
            return false;
        }
        return runtimeBreakPoints.add(breakPoint);
    }

    public boolean containRuntimeBreakPoint(BreakPoint breakPoint){
        if(!breakPoint.getClassFullQualifiedName().equals(classFullQualifiedName)){
            return false;
        }
        if(runtimeBreakPoints.size() <= 0){
            return false;
        }
        BreakPoint runtimeBreakPoint = runtimeBreakPoints.stream().filter(e -> e.getPointIndex().equals(breakPoint.getPointIndex())).findAny().orElse(null);
        if (runtimeBreakPoint != null) {
            breakPoint.setExpression(runtimeBreakPoint.getExpression());
            return true;
        } else {
            return false;
        }
    }

    public BreakPoint getBreakPoint(int breakPointIndex){
        return runtimeBreakPoints.stream().filter(e->e.getPointIndex() == breakPointIndex).findAny().orElse(null);
    }

    public boolean replaceRuntimeBreakPoint(BreakPoint breakPoint) {
        return this.addRuntimeBreakPoint(breakPoint);
    }

    public Clazz(List<List<String>> breakPointVariables, List<String> fields) {
        this.breakPointVariables = breakPointVariables;
        this.fields = fields;
    }

//    public Clazz(List<Set<String>> breakPointVariablesSet, Set<String> fields){
//        this.breakPointVariables = breakPointVariablesSet;
//        this.fields = fields;
//    }


    public String getAfterParseStableHtmlSource() {
        return afterParseStableHtmlSource;
    }

    public void setAfterParseStableHtmlSource(String afterParseStableHtmlSource) {
        this.afterParseStableHtmlSource = afterParseStableHtmlSource;
    }

    public String getClassFullQualifiedName() {
        return classFullQualifiedName;
    }

    public void setClassFullQualifiedName(String classFullQualifiedName) {
        this.classFullQualifiedName = classFullQualifiedName;
    }

    public String getHtmlSource() {
        return htmlSource;
    }

    public void setHtmlSource(String htmlSource) {
        this.htmlSource = htmlSource;
    }

//    public List<List<String>> getBreakPointVariables() {
//        return breakPointVariables;
//    }

    public String getParentClassFullQualifiedName() {
        return parentClassFullQualifiedName;
    }

    public void setParentClassFullQualifiedName(String parentClassFullQualifiedName) {
        this.parentClassFullQualifiedName = parentClassFullQualifiedName;
    }

    public List<String> getFields() {
        return fields;
    }


//    public List<Set<String>> getBreakPointVariables() {
//        return breakPointVariables;
//    }

    public List<List<String>> getBreakPointVariables() {
        return breakPointVariables;
    }
//
//    public Set<String> getFields() {
//        return fields;
//    }


    public Map<String, List<String>> getInnerClassFields() {
        return innerClassFields;
    }

    public List<String> addInnerFields(String innerClassname, String field){
        List<String> strings = innerClassFields.computeIfAbsent(innerClassname, k -> new ArrayList<>());
        strings.add(field);
        return strings;
    }

    public void setInnerClassFields(Map<String, List<String>> innerClassFields) {
        this.innerClassFields = innerClassFields;
    }

}
