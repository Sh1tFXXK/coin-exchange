package com.wc.service;

import com.wc.model.WebLog;

public interface TestService {
    /**
     *
     通过username
     查询weblog
     *
     */
    WebLog get(String username);
}
