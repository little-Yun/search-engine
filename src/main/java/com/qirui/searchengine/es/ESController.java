package com.qirui.searchengine.es;

import com.qirui.searchengine.bean.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "ES测试接口", tags = {"ES测试接口"})
@RestController
@RequestMapping("/es")
public class ESController {
    @Autowired
    private ESService esService;


    @ApiOperation(value = "es聚合查询接口", notes = "es聚合查询接口")
    @RequestMapping(value = "/query/agg", method = RequestMethod.GET)
    @CrossOrigin(value = "http://localhost:8888")
    public ResponseBean query(String param, int from) {
        return esService.query(param, from);
    }


    @ApiOperation(value = "es创建索引接口", notes = "es创建索引接口")
    @RequestMapping(value = "/create/index", method = RequestMethod.POST)
    public ResponseBean createIndex(@RequestParam String indexName) {
        return esService.createIndex(indexName);
    }

}