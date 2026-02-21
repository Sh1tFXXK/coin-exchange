package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wc.domain.SysMenu;
import com.wc.domain.SysPrivilege;
import com.wc.model.RolePrivilegesParam;
import com.wc.service.SysMenuService;
import com.wc.service.SysPrivilegeService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.SysRolePrivilegeMapper;
import com.wc.domain.SysRolePrivilege;
import com.wc.service.SysRolePrivilegeService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class SysRolePrivilegeServiceImpl extends ServiceImpl<SysRolePrivilegeMapper, SysRolePrivilege> implements SysRolePrivilegeService{


    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysPrivilegeService sysPrivilegeService;

    @Autowired
    private SysRolePrivilegeService sysRolePrivilegeService;

    @Override
    public List<SysMenu> findSysMenuAndPrivileges(Long roleId) {
        List<SysMenu> list = sysMenuService.list();
        if (CollectionUtils.isEmpty(list)) {
            return  Collections.emptyList();
        }
        List<SysMenu> rootMenu = list.stream().filter(SysMenu -> SysMenu.getParentId() == null).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rootMenu)) {
            return Collections.emptyList();
        }
        List<SysMenu> subMenu = new ArrayList<>();
        rootMenu.forEach(menu -> {
            subMenu.addAll(getChildMenus(menu.getId(),roleId,list));
        });
        return subMenu;
    }

    @Override
    @Transactional
    public boolean grantPrivileges(RolePrivilegesParam rolePrivilegesParam) {

        Long roleId = rolePrivilegesParam.getRoleId();
        sysRolePrivilegeService.remove(new LambdaQueryWrapper<SysRolePrivilege>().eq(SysRolePrivilege::getRoleId, roleId));

            List<Long> privilegeIds = rolePrivilegesParam.getPrivilegeIds();
            if(!CollectionUtils.isEmpty(privilegeIds)){
                ArrayList<SysRolePrivilege> sysRolePrivileges = new ArrayList<>();
                privilegeIds.forEach(privilegeId -> {
                    SysRolePrivilege sysRolePrivilege = new SysRolePrivilege();
                    sysRolePrivilege.setRoleId(roleId);
                    sysRolePrivilege.setPrivilegeId(privilegeId);
                    sysRolePrivileges.add(sysRolePrivilege);
                });
                //新增的值
                return sysRolePrivilegeService.saveBatch(sysRolePrivileges);
            }
        return true;
    }

    /**
     * 获取子菜单
     * @param menuId
     * @param roleId
     * @param sysMenuList
     * @return
     */
    private List<SysMenu> getChildMenus(Long menuId, Long roleId, List<SysMenu> sysMenuList) {
        List<SysMenu> children = new ArrayList<>();
        for (SysMenu child : sysMenuList) {
            if(child.getParentId().equals(menuId)){
                child.setChilds(getChildMenus(child.getId(),roleId,sysMenuList));
                children.add(child);
                List<SysPrivilege> sysPrivileges= sysPrivilegeService.getAllSysPrivilege(child.getId(), roleId);
                child.setPrivileges(sysPrivileges);
            }
        }
        return children;
    }

}
