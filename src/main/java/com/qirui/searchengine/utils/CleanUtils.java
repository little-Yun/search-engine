package com.qirui.searchengine.utils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class CleanUtils {
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

}
