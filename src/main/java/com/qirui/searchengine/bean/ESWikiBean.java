package com.qirui.searchengine.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class ESWikiBean implements Serializable {
    private String id;
    private String title;
    private String brief;
    private String info;
    private String body;
    private String url;
    private Timestamp indextime;

    public ESWikiBean(){
    }

    public ESWikiBean(String id, String title, String brief, String info, String body, String url) {
        this.id = id;
        this.title = title;
        this.brief = brief;
        this.info = info;
        this.body = body;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getIndextime() {
        return indextime;
    }

    public void setIndextime(Timestamp indextime) {
        this.indextime = indextime;
    }

}
