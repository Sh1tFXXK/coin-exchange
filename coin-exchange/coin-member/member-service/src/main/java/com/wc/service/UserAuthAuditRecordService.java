package com.wc.service;

import com.wc.domain.UserAuthAuditRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserAuthAuditRecordService extends IService<UserAuthAuditRecord>{


     List<UserAuthAuditRecord> getUserAuthAuditRecordList(Long id);
}
