package co.xiaowangzi.debug.clazz;


import java.util.Arrays;
import java.util.Objects;


public class BreakPoint implements java.io.Serializable{
    /**所在的类*/
    private String classFullQualifiedName;
    /**断点索引*/
    private Integer pointIndex;
    /**局部变量的值*/
    private Object[] variables;
    /**方法索引*/
    private Integer methodIndex;
    /**非静态方法是有值this，否则静态方法为null*/
    private Object thees;
    /**方法名称*/
    private String methodName;
    /**属于的类*/
    private Clazz clazz;
    /**是否是静态方法*/
    private Boolean staticMethod;
    /**0不可执行，1有断点可执行，2无断点可执行*/
    private int shouldRun = 0;
    /**内部类*/
    private String innerClassName;
    /**线程名称*/
    private String threadName;
    /**断点表达式(可选)*/
    private String expression;

    public static BreakPoint of(Integer pointIndex, String methodName, String classFullQualifiedName, Integer methodIndex, Object thees, Object ... variables){
        BreakPoint breakPoint = new BreakPoint();
        breakPoint.setClassFullQualifiedName(classFullQualifiedName);
        breakPoint.setMethodIndex(methodIndex);
        breakPoint.setVariables(variables);
        breakPoint.setThees(thees);
        breakPoint.setPointIndex(pointIndex);
        breakPoint.setMethodName(methodName);
        breakPoint.setStaticMethod(thees == null);
        return breakPoint;
    }

    public static BreakPoint of(String classFullQualifiedName, Integer pointIndex){
        BreakPoint breakPoint = new BreakPoint();
        breakPoint.setClassFullQualifiedName(classFullQualifiedName);
        breakPoint.setPointIndex(pointIndex);
        return breakPoint;
    }

    public static boolean sameClassMethod(BreakPoint bp1, BreakPoint bp2) {
        return bp1.getClassFullQualifiedName().equals(bp2.getClassFullQualifiedName()) && bp1.getMethodIndex().equals(bp2.getMethodIndex()) && bp1.getMethodName().equals(bp2.getMethodName());
    }

    public static boolean sameBreakPoint(BreakPoint bp1, BreakPoint bp2){
        if(bp1 == null || bp2 == null){
            return false;
        }
        return bp1.getClassFullQualifiedName().equals(bp2.getClassFullQualifiedName()) && bp1.getPointIndex().equals(bp2.getPointIndex());
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getInnerClassName() {
        return innerClassName;
    }

    public void setInnerClassName(String innerClassName) {
        this.innerClassName = innerClassName;
    }

    public Boolean getStaticMethod() {
        return staticMethod;
    }

    public void setStaticMethod(Boolean staticMethod) {
        this.staticMethod = staticMethod;
    }

    public Clazz getClazz() {
        return clazz;
    }

    public int getShouldRun() {
        return shouldRun;
    }

    public void setShouldRun(int shouldRun) {
        this.shouldRun = shouldRun;
    }

    public void setClazz(Clazz clazz) {
        this.clazz = clazz;
    }

    public String getClassFullQualifiedName() {
        return classFullQualifiedName;
    }

    public void setClassFullQualifiedName(String classFullQualifiedName) {
        this.classFullQualifiedName = classFullQualifiedName;
    }

    public Integer getPointIndex() {
        return pointIndex;
    }

    public void setPointIndex(Integer pointIndex) {
        this.pointIndex = pointIndex;
    }

    public Object[] getVariables() {
        return variables;
    }

    public void setVariables(Object[] variables) {
        this.variables = variables;
    }

    public Integer getMethodIndex() {
        return methodIndex;
    }

    public void setMethodIndex(Integer methodIndex) {
        this.methodIndex = methodIndex;
    }

    public Object getThees() {
        return thees;
    }

    public void setThees(Object thees) {
        this.thees = thees;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BreakPoint that = (BreakPoint) o;
        return classFullQualifiedName.equals(that.classFullQualifiedName) && pointIndex.equals(that.pointIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classFullQualifiedName, pointIndex);
    }

    @Override
    public String toString() {
        return "BreakPoint{" +
                "classFullQualifiedName='" + classFullQualifiedName + '\'' +
                ", pointIndex=" + pointIndex +
                ", variables=" + (variables != null ? Arrays.toString(variables) : null) +
                ", methodIndex=" + methodIndex +
                ", thees=" + thees +
                ", methodName='" + methodName + '\'' +
                ", clazz=" + clazz +
                ", expression=" + expression +
                '}';
    }
}
