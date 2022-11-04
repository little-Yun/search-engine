package com.qirui.searchengine.webmagic;

import com.qirui.searchengine.webmagic.service.PageProcessorService;
import com.qirui.searchengine.webmagic.service.PipelineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;


@Api(value = "webmagic测试接口", tags = {"wemagic测试接口"})
@CrossOrigin
@RestController
@RequestMapping("/wiki")
public class SpiderController implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(SpiderController.class);
    @Autowired
    private PageProcessorService pageProcessorService;
    @Autowired
    private PipelineService pipelineService;
    @Autowired
    private RedisScheduler redisScheduler;

    @ApiOperation(value = "webmagic测试运行接口", notes = "webmagic测试运行接口")
    @RequestMapping(value = "/webmagic", method = RequestMethod.GET)
    public void initBaikeData() {
        Spider spider = Spider.create(pageProcessorService)
                .addUrl("https://baike.baidu.com/item/%E6%B9%96%E5%8D%97/228213")
                .addPipeline(pipelineService)
                .thread(8);
        try {
            spider.run();
        } catch (Exception e) {
            logger.error("spider exception:", e);
        }
    }

    @Override
    public void run(String... args) throws Exception {
//        logger.info("自启动抓取任务执行");
//        initBaikeData();
    }
}
