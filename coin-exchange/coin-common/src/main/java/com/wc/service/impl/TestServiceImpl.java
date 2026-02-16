package com.wc.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.wc.model.WebLog;
import com.wc.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    /**
     *
     * 通过username
     * 查询weblog
     *
     * @param username
     *
     */
    @Cached(name="com.bjsxt.service.impl.TestServiceImpl:",key="#username",cacheType= CacheType.BOTH)
    public WebLog get(String username) {
        WebLog webLog=new WebLog();
        webLog.setUsername(username);
        webLog.setResult("ok");
        return webLog;
    }
}
