package co.xiaowangzi.debug.clazz;

public class BreakPointExpression {
    private String classFullQualifiedName;
    private Integer pointIndex;
    private String expression;

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

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}


