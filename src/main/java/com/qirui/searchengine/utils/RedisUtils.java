package com.qirui.searchengine.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public Long removeValuesFromRedisSet(String key, List<String> values) {
        long removeCount = 0;
        String[] valueArray = list2String(values);
        if (valueArray != null) {
            removeCount = redisTemplate.opsForSet().remove(key, valueArray);
        }
        return removeCount;
    }

    private String[] list2String(List<String> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            String[] array = new String[list.size()];
            int i = 0;
            for (String str : list) {
                array[i++] = str;
            }
            return array;
        }
        return null;
    }

    public void setString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        log.info("重置{}的值{} ", key, value);
    }

    public String getString(String key) {
        //
        return redisTemplate.opsForValue().get(key);
    }

    public boolean hasKey(String key) {
        //
        return redisTemplate.hasKey(key);
    }
}
