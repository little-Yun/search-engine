package com.qirui.searchengine.es.batch;

import com.qirui.searchengine.bean.ESWikiBean;
import com.qirui.searchengine.bean.persisent.WikiRepository;
import com.qirui.searchengine.utils.RedisUtils;
import com.qirui.searchengine.utils.TimeUtils;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ESBatchService {
    private static final Logger logger = LoggerFactory.getLogger(ESBatchService.class);
    @Autowired
    private BulkProcessor bulkProcessor;
    @Autowired
    private WikiRepository wikiRepository;
    @Autowired
    private RedisUtils redisUtils;

    public void writeMysqlDataToES(Pageable pageable, String index) {
        Page<ESWikiBean> page = wikiRepository.getAllRecord(pageable);
        if (page.isEmpty()) {
            return;
        }
        List<ESWikiBean> dataList = page.getContent();
        Map<String, Object> map;
        int count = 1;
        boolean flag = true;
        try {
            while (flag == true) {
                for (int i = 0; i < dataList.size(); i++) {
                    map = esWikiToMap(dataList.get(i));
                    bulkProcessor.add(new IndexRequest(index, "_doc", dataList.get(i).getId().toString()).source(map));
                }
                count++;
                if (page.hasNext()) {
                    pageable = pageable.next();
                    page = wikiRepository.getAllRecord(pageable);
                    dataList = page.getContent();
                    if (dataList.size() < 100) {
                        break;
                    }
                } else {
                    flag = false;
                }
                redisUtils.setString("page", String.valueOf(pageable.getPageNumber() + 1));
                if (count % 5 == 0) {
                    bulkProcessor.flush();
                    Thread.sleep(1000L);
                }
            }
            bulkProcessor.flush();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                boolean terminatedFlag = bulkProcessor.awaitClose(150L, TimeUnit.SECONDS);
                bulkProcessor.close();
                logger.info("" + terminatedFlag);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public Map esWikiToMap(ESWikiBean esWiki) {
        Map<String, Object> map = new HashMap<>();
//        map.put("id", esWiki.getId());
        map.put("url", esWiki.getUrl());
        map.put("title", esWiki.getTitle());
        map.put("brief", esWiki.getBrief());
        map.put("info", esWiki.getInfo());
        map.put("body", esWiki.getBody());
        map.put("indextime", TimeUtils.now());
        return map;
    }
}
