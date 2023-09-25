package co.xiaowangzi.debug.clazz;

public class RequestHtmlBridge implements java.io.Serializable{
    private Integer id;
    /**源码*/
    private String htmlSource;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHtmlSource() {
        return htmlSource;
    }

    public void setHtmlSource(String htmlSource) {
        this.htmlSource = htmlSource;
    }
}
