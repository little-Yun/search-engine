package com.qirui.searchengine.webmagic.service;

import com.qirui.searchengine.bean.persisent.WikiRepository;
import com.qirui.searchengine.utils.RedisUtils;
import com.qirui.searchengine.bean.persisent.Wiki;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.sql.Timestamp;

@Component
public class PipelineService implements Pipeline {
    private static Logger logger = LoggerFactory.getLogger(PipelineService.class);
    private final WikiRepository wikiRepository;

    @Autowired
    public PipelineService(WikiRepository wikiRepository) {
        this.wikiRepository = wikiRepository;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        Wiki wiki = resultItems.get("wiki_data");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        wiki.setCreatedTime(timestamp);
        wikiRepository.save(wiki);
        logger.info("保存{}" + wiki);
    }
}
