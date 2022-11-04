package com.qirui.searchengine.webmagic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Redis缓存实现网页去重和增量查询
 */
@Component
public class RedisScheduler extends DuplicateRemovedScheduler implements
        MonitorableScheduler, DuplicateRemover {
    private JedisPool pool;
    private URL url = null;

    @Autowired
    public RedisScheduler(JedisPool pool) {
        this.pool = pool;
        setDuplicateRemover(this);
    }

    /**
     * DuplicateRemover接口的方法，
     * 顾名思义就是重复检查删除对应的
     * getQueueKey方法为获得Redis List队列的待抓取队列
     *
     * @param task
     */
    @Override
    public void resetDuplicateCheck(Task task) {
        Jedis jedis = pool.getResource();
        try {
            jedis.del(getSetKey(task));
        } finally {
            pool.returnBrokenResource(jedis);
        }
    }

    /**
     * DuplicateRemover接口的方法，
     * 顾名思义就是jedis的sismember方法判断是否入set
     * 是则入，否则抛弃
     * getSetKey方法为获得Redis Set的已抓取队列
     *
     * @param task
     */
    @Override
    public boolean isDuplicate(Request request, Task task) {
        Jedis jedis = pool.getResource();
        try {
            url = new URL(request.getUrl().split("/\\d+")[0]);
            boolean isDuplicate = jedis.sismember(getSetKey(task), url.getPath());
            if (!isDuplicate) {
                jedis.sadd(getSetKey(task), url.getPath());
            }
            return isDuplicate;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            pool.returnResource(jedis);
        }
        return false;
    }

    /**
     * 当不重复时，将请求的URL入待抓取
     * getQueueKey方法为获得Redis List的待抓取队列
     */
    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        Jedis jedis = pool.getResource();
        try {
            jedis.rpush(getQueueKey(task), request.getUrl());
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
     * 取出队列首部的请求URL
     */
    @Override
    public synchronized Request poll(Task task) {
        Jedis jedis = pool.getResource();
        try {
            String url = jedis.lpop(getQueueKey(task));
            if (url == null) {
                return null;
            }
            Request request = new Request(url);
            return request;
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.scard(getQueueKey(task));
            return size.intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.llen(getQueueKey(task));
            return size.intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }

    private String getSetKey(Task task) {
        //
        return "set_" + task.getUUID();
    }

    private String getQueueKey(Task task) {
        //
        return "queue_" + task.getUUID();
    }
}