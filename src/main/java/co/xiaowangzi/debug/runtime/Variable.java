package co.xiaowangzi.debug.runtime;

public class Variable implements java.io.Serializable{
    private String name;
    private Object value;
    private Boolean isList;
    private Boolean isObject;
    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getList() {
        return isList;
    }

    public void setList(Boolean list) {
        isList = list;
    }

    public Boolean getObject() {
        return isObject;
    }

    public void setObject(Boolean object) {
        isObject = object;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
