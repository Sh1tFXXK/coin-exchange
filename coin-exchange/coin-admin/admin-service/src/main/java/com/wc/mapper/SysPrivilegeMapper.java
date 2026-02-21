package com.wc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wc.domain.SysPrivilege;

import java.util.Set;

public interface SysPrivilegeMapper extends BaseMapper<SysPrivilege> {
    Set<Long> getPrivilegesByRoleId(Long roleId);
}