package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.UserAuthAuditRecord;
import com.wc.mapper.UserAuthAuditRecordMapper;
import com.wc.service.UserAuthAuditRecordService;

import java.util.Collections;
import java.util.List;

@Service
public class UserAuthAuditRecordServiceImpl extends ServiceImpl<UserAuthAuditRecordMapper, UserAuthAuditRecord> implements UserAuthAuditRecordService{

    @Override
    public List<UserAuthAuditRecord> getUserAuthAuditRecordList(Long id) {
        return list(new LambdaQueryWrapper<UserAuthAuditRecord>()
                .eq(UserAuthAuditRecord::getUserId, id)
                .orderByDesc(UserAuthAuditRecord::getCreated)
                .last("limit 3"));
    }
}
