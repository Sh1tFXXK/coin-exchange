package com.wc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wc.domain.SysRole;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    String getUserRoleCode(Long userId);

}