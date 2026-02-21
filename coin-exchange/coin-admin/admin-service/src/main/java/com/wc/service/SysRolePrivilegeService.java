package com.wc.service;

import com.wc.domain.SysMenu;
import com.wc.domain.SysRolePrivilege;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wc.model.RolePrivilegesParam;

import java.util.List;

public interface SysRolePrivilegeService extends IService<SysRolePrivilege>{


    List<SysMenu> findSysMenuAndPrivileges(Long roleId);

    boolean grantPrivileges(RolePrivilegesParam rolePrivilegesParam);
}
