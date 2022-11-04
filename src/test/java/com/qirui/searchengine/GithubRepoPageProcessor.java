package com.qirui.searchengine;

import com.qirui.searchengine.bean.persisent.Wiki;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @create: 2022-11-01 16:13
 * @description:
 **/
@Slf4j
public class GithubRepoPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);

    @Override
    public void process(Page page) {
        int statusCode = page.getStatusCode();
        if (statusCode == HttpConstant.StatusCode.CODE_200) {
            Html html = page.getHtml();
            String url = page.getUrl().get();
            List<String> links = page.getHtml().links()
                    .regex("http://baike\\.baidu\\.com/item/%.*|https://baike\\.baidu\\.com/item/%.*").all();
            for (int i = 0; i < links.size() - 1; i++) {
                if (!links.get(i).contains(url)) {
                    log.info("添加请求url---> " + links.get(i));
                    page.addTargetRequest(new Request(links.get(i)));
                }
            }

            Wiki wikiData = getWikiData(url, html);
            if (!wikiData.getWikiTitle().isEmpty() && !wikiData.getWikiBrief().isEmpty()) {
                page.putField("wiki_data", wikiData);
            }
        } else {
            log.error("请求url：{} 错误" + page.getUrl());
        }
    }

    private Wiki getWikiData(String url, Html html) {
        Wiki wiki = new Wiki();
        String wikiTitle = html.xpath(
                "//dl[@class='lemmaWgt-lemmaTitle']/" +
                        "dd[@class='lemmaWgt-lemmaTitle-title']/span/h1/text()").toString();
        String wikiTitleAdd = html.xpath(
                "//dl[@class='lemmaWgt-lemmaTitle']/" +
                        "dd[@class='lemmaWgt-lemmaTitle-title']/span/h2/text()").toString();
        wiki.setWikiTitle(wikiTitle + (wikiTitleAdd == null ? "" : cleanTitle(wikiTitleAdd)));
        String wikiBreif = html.xpath(
                "//div[@class='lemma-summary']/" +
                        "div[@class='para']").all().toString();
        wiki.setWikiBrief(wikiBreif == null ? "无" : clean(wikiBreif));
        wiki.setWikiUrl(url);
        String wikiInfo = html.xpath(
                "//div[@class='basic-info cmn-clearfix']" +
                        "/dl[@class='basicInfo-block basicInfo-left']" +
                        "/dd[@class='basicInfo-item value']").all().toString();
        wiki.setWikiInfo(wikiInfo == null ? "无" : clean(wikiInfo));
        String wikiBody = html.xpath(
                "//div[@class='main-content']/div[@class='para']").all().toString();
        wiki.setWikiBody(wikiBody == null ? "无" : clean(wikiBody));
        return wiki;
    }

    private final static Whitelist whitelist = Whitelist.none();
    private final static Pattern pattern = Pattern.compile("\\[\\d+-\\d+\\]|\\[\\d+\\]");

    public String clean(String content) {
        content = Jsoup.clean(content.substring(1, content.length() - 1), whitelist);
        return pattern.matcher(content).replaceAll("").replaceAll("\\s+", "")
                .replaceAll("&nbsp;", "");
    }

    public String cleanTitle(String content) {
        content = Jsoup.clean(content, whitelist);
        return pattern.matcher(content).replaceAll("").replaceAll("\\s+", "")
                .replaceAll("&nbsp;", "");
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new GithubRepoPageProcessor()).addUrl("https://baike.baidu.com/item/%E8%B4%BE%E9%B2%81%E6%B2%B3/8337622").thread(5).run();
    }
}
