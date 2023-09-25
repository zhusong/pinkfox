package co.xiaowangzi.debug.clazz;

public class SearchingBridge implements java.io.Serializable{
    private String label;
    private String value;

    public static SearchingBridge of(String value, String label){
        SearchingBridge searchingBridge = new SearchingBridge();
        searchingBridge.setValue(value);
        searchingBridge.setLabel(label);
        return searchingBridge;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
