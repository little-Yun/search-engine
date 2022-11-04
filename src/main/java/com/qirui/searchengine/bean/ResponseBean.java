package com.qirui.searchengine.bean;

public class ResponseBean {
    //状态码
    private Integer code;
    //返回信息
    private String message;
    //返回的数据
    private Object data;

    public ResponseBean(int i, String message, Object o) {
        this.code = i;
        this.message = message;
        this.data = o;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}