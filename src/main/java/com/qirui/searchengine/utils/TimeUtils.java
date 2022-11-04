package com.qirui.searchengine.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class TimeUtils {

    public static String now() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime time = LocalDateTime.now();
        String localTime = df.format(time);
        return localTime;
    }

}

