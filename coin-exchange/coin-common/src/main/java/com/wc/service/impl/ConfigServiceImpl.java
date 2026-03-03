package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.Config;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.ConfigMapper;
import com.wc.service.ConfigService;
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService{

    @Override
    public Page<Config> findByPage(Page<Config> page, String type, String name, String code) {

        return page(page,new LambdaQueryWrapper<Config>()
                .like(!StringUtils.isEmpty(type),Config::getType ,type)
                .like(!StringUtils.isEmpty(name),Config::getName ,name)
                .like(!StringUtils.isEmpty(code),Config::getCode ,code));
    }

    /**
     * 通过的规则的Code 查询签名
     *
     * @param code 签名的code
     * @return config value
     */
    @Override
    public Config getConfigByCode(String code) {
        return getOne(new LambdaQueryWrapper<Config>().eq(Config::getCode,code));
    }

}
