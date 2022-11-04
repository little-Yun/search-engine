package com.qirui.searchengine.es.batch;

import com.qirui.searchengine.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Api(value = "ES批量测试接口", tags = {"ES批量测试接口"})
@RestController
@RequestMapping("/es")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class ESBatchController {
    private static Logger logger = LoggerFactory.getLogger(ESBatchController.class);
    @Autowired
    private ESBatchService esBatchService;
    @Autowired
    private RedisUtils redisUtils;

    @ApiOperation(value = "es批量导入接口", notes = "es批量导入接口")
    @RequestMapping(value = "/defaultBulkData", method = RequestMethod.GET)
    @ResponseBody
    public String defaultBulkData() {
        String pageStr = redisUtils.getString("page");
        int page = 0;
        if (StringUtils.hasText(pageStr)) {
            page = Integer.valueOf(pageStr);
        }
        Pageable pageable = PageRequest.of(page, 100, Sort.by("wikiId"));
        esBatchService.writeMysqlDataToES(pageable, "wiki_es");
        return "/es/defaultBulkData";
    }

    @ApiOperation(value = "es批量导入接口(自定义index)", notes = "es批量导入接口")
    @RequestMapping(value = "/bulkData", method = RequestMethod.POST)
    @ResponseBody
    public String bulkData(String index) {
        if (redisUtils.hasKey("page_" + index) == false) {
            redisUtils.setString("page_" + index, 0 + "");
        }
        int page = Integer.valueOf(redisUtils.getString("page_" + index));
        Pageable pageable = PageRequest.of(page, 100, Sort.by("wikiId"));
        esBatchService.writeMysqlDataToES(pageable, index);
        return "/es/bulkData";
    }

    @Scheduled(cron = "0 05 03 ? * *")
    public void bulkTask() {
        logger.info("定时批量任务执行");
        defaultBulkData();
    }

}
