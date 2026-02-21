package com.wc.service;

import com.wc.domain.SysPrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysPrivilegeService extends IService<SysPrivilege>{


    List<SysPrivilege> getAllSysPrivilege(Long id, Long roleId);
}
