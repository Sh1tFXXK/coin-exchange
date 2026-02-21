package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.WorkIssue;
import com.wc.mapper.WorkIssueMapper;
import com.wc.service.WorkIssueService;
@Service
public class WorkIssueServiceImpl extends ServiceImpl<WorkIssueMapper, WorkIssue> implements WorkIssueService{

    @Override
    public Page<WorkIssue> findByPage(Page<WorkIssue> page, Integer status, String startTime, String endTime) {
        return page(page,new LambdaQueryWrapper<WorkIssue>()
                .eq(status != null,WorkIssue::getStatus,status)
                .between(!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime),WorkIssue::getCreated,startTime,endTime+ " 23:59:59"));
    }
}
