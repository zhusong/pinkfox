package co.xiaowangzi.debug.clazz;

public class BreakPointBridge implements java.io.Serializable{
    private String classFullQualifiedName;
    private Integer pointIndex;
    private Integer id;
    private Boolean isDebuging;
    private String classSimpleName;
    private Boolean hasExpression;

    public Boolean getHasExpression() {
        return hasExpression;
    }

    public void setHasExpression(Boolean hasExpression) {
        this.hasExpression = hasExpression;
    }

    public String getClassSimpleName() {
        return classSimpleName;
    }

    public void setClassSimpleName(String classSimpleName) {
        this.classSimpleName = classSimpleName;
    }

    public Boolean getDebuging() {
        return isDebuging;
    }

    public void setDebuging(Boolean debuging) {
        isDebuging = debuging;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
