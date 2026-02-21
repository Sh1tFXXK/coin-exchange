package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.WebConfigMapper;
import com.wc.domain.WebConfig;
import com.wc.service.WebConfigService;
@Service
public class WebConfigServiceImpl extends ServiceImpl<WebConfigMapper, WebConfig> implements WebConfigService{

    @Override
    public Page<WebConfig> findByPage(Page<WebConfig> page, String name, String type) {
        return page(page,new LambdaQueryWrapper<WebConfig>().like(!StringUtils.isEmpty(name),WebConfig::getName,name)
                .like(!StringUtils.isEmpty(type),WebConfig::getType,type));
    }
}
