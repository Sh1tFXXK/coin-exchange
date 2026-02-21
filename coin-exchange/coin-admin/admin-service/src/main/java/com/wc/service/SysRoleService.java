package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysRoleService extends IService<SysRole>{


    boolean isSuperAdmin(Long userId);

    Page<SysRole> findByPage(Page<SysRole> page, String name);
}
