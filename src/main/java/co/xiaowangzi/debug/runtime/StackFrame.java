package co.xiaowangzi.debug.runtime;

import java.util.List;

public class StackFrame {
    private Object thees;
    private String classFullQualifiedName;
    private Integer pointIndex;
    private String methodName;
    private Object[] variables;

    public Object getThees() {
        return thees;
    }

    public void setThees(Object thees) {
        this.thees = thees;
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

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getVariables() {
        return variables;
    }

    public void setVariables(Object[] variables) {
        this.variables = variables;
    }

}
