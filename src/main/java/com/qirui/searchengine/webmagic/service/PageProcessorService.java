package com.qirui.searchengine.webmagic.service;

import com.qirui.searchengine.bean.persisent.Wiki;
import com.qirui.searchengine.utils.CleanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
public class PageProcessorService implements PageProcessor {

    @Autowired
    public CleanUtils cleanUtils;

    @Override
    public void process(Page page) {
        int statusCode = page.getStatusCode();
        if (statusCode == HttpConstant.StatusCode.CODE_200) {
            Html html = page.getHtml();
            String url = page.getUrl().get();
            List<String> links = page.getHtml().links().regex("http://baike\\.baidu\\.com/item/%.*|https://baike\\.baidu\\.com/item/%.*").all();
            for (int i = 0; i < links.size() - 1; i++) {
                if (!links.get(i).contains(url)) {
                    log.info("添加请求url---> " + links.get(i));
                    page.addTargetRequest(new Request(links.get(i)));
                }
            }
            String wikiId = UUID.randomUUID().toString().replaceAll("-", "");
            Wiki wikiData = getWikiData(url, html, wikiId);
            if (!wikiData.getWikiTitle().isEmpty() && !wikiData.getWikiBrief().isEmpty()) {
                page.putField("wiki_data", wikiData);
            }
        } else {
            log.error("请求url：{} 错误" + page.getUrl());
        }
    }


    private Wiki getWikiData(String url, Html html, String balkeId) {
        Wiki wiki = new Wiki();
        wiki.setWikiId(balkeId);
        String wikiTitle = html.xpath(
                "//dl[@class='lemmaWgt-lemmaTitle']/" +
                        "dd[@class='lemmaWgt-lemmaTitle-title']/span/h1/text()").toString();
        String wikiTitleAdd = html.xpath(
                "//dl[@class='lemmaWgt-lemmaTitle']/" +
                        "dd[@class='lemmaWgt-lemmaTitle-title']/span/h2/text()").toString();
        wiki.setWikiTitle(wikiTitle + (wikiTitleAdd == null ? "" : cleanUtils.cleanTitle(wikiTitleAdd)));
        String wikiBreif = html.xpath(
                "//div[@class='lemma-summary']/" +
                        "div[@class='para']").all().toString();
        wikiBreif = wikiBreif.length() > 500 ? wikiBreif.substring(0, 500) : wikiBreif;
        wiki.setWikiBrief(wikiBreif == null ? "无" : cleanUtils.clean(wikiBreif));
        wiki.setWikiUrl(url);
        String wikiInfo = html.xpath(
                "//div[@class='basic-info cmn-clearfix']" +
                        "/dl[@class='basicInfo-block basicInfo-left']" +
                        "/dd[@class='basicInfo-item value']").all().toString();
        wiki.setWikiInfo(wikiInfo == null ? "无" : cleanUtils.clean(wikiInfo));
        String wikiBody = html.xpath(
                "//div[@class='main-content']/div[@class='para']").all().toString();
        wikiBody = wikiBody.length() > 500 ? wikiBody.substring(0, 500) : wikiBody;
        wiki.setWikiBody(wikiBody == null ? "无" : cleanUtils.clean(wikiBody));
        return wiki;
    }

    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    }
}
