package co.xiaowangzi.debug.clazz;

public class JsonPrintBridge implements java.io.Serializable{
    private Boolean jsonGood;
    private String jsonValue;

    public Boolean getJsonGood() {
        return jsonGood;
    }

    public void setJsonGood(Boolean jsonGood) {
        this.jsonGood = jsonGood;
    }

    public String getJsonValue() {
        return jsonValue;
    }

    public void setJsonValue(String jsonValue) {
        this.jsonValue = jsonValue;
    }
}
