package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wc.domain.Config;

public interface ConfigService extends IService<Config>{


    Page<Config> findByPage(Page<Config> page, String type, String name, String code);

    /**
     * 通过的规则的Code 查询签名
     * @param code
     *  签名的code
     * @return
     * config value
     */
    Config getConfigByCode(String code);

}
