package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.utils.JacksonPrinter;

//import co.xiaowangzi.debug.utils.GsonUtils;

public class Food<T> implements java.io.Serializable {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Food<T> success(T data){
        Food<T> food = new Food<>();
        food.setCode(0);
        food.setMsg("success");
        food.setData(data);
        return food;
    }

    public String toJson(){
        return JacksonPrinter.write2JsonStr(this);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
