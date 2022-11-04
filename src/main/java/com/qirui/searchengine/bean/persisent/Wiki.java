package com.qirui.searchengine.bean.persisent;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Wiki {
    private String wikiId;
    private String wikiTitle;
    private String wikiUrl;
    private String wikiBrief;
    private String wikiInfo;
    private String wikiBody;
    private Timestamp createdTime;

    @Id
    @Column(name = "wiki_id", nullable = false,length = 300)
    public String getWikiId() {
        return wikiId;
    }

    public void setWikiId(String wikiId) {
        this.wikiId = wikiId;
    }

    @Basic
    @Column(name = "wiki_title", nullable = true, length = 300)
    public String getWikiTitle() {
        return wikiTitle;
    }

    public void setWikiTitle(String wikiTitle) {
        this.wikiTitle = wikiTitle;
    }

    @Basic
    @Column(name = "wiki_url", nullable = true, length = 300)
    public String getWikiUrl() {
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }

    @Basic
    @Column(name = "wiki_brief", nullable = true, length = 500)
    public String getWikiBrief() {
        return wikiBrief;
    }

    public void setWikiBrief(String wikiBrief) {
        this.wikiBrief = wikiBrief;
    }

    @Basic
    @Column(name = "wiki_info", nullable = true, length = 20)
    public String getWikiInfo() {
        return wikiInfo;
    }

    public void setWikiInfo(String wikiInfo) {
        this.wikiInfo = wikiInfo;
    }

    @Basic
    @Column(name = "wiki_body", nullable = true, length = 500)
    public String getWikiBody() {
        return wikiBody;
    }

    public void setWikiBody(String wikiBody) {
        this.wikiBody = wikiBody;
    }

    @Basic
    @Column(name = "created_time", nullable = true)
    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wiki wiki = (Wiki) o;
        return wikiId == wiki.wikiId &&
                Objects.equals(wikiTitle, wiki.wikiTitle) &&
                Objects.equals(wikiUrl, wiki.wikiUrl) &&
                Objects.equals(wikiBrief, wiki.wikiBrief) &&
                Objects.equals(wikiInfo, wiki.wikiInfo) &&
                Objects.equals(wikiBody, wiki.wikiBody) &&
                Objects.equals(createdTime, wiki.createdTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wikiId, wikiTitle, wikiUrl, wikiBrief, wikiInfo, wikiBody, createdTime);
    }
}
