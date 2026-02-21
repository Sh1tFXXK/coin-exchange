package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.SysPrivilege;
import com.wc.mapper.SysPrivilegeMapper;
import com.wc.service.SysPrivilegeService;
import org.springframework.util.CollectionUtils;

@Service
public class SysPrivilegeServiceImpl extends ServiceImpl<SysPrivilegeMapper, SysPrivilege> implements SysPrivilegeService{


    @Autowired
    private SysPrivilegeMapper sysPrivilegeMapper;

    @Override
    public List<SysPrivilege> getAllSysPrivilege(Long id, Long roleId) {
        List<SysPrivilege> sysPrivileges = list(new LambdaQueryWrapper<SysPrivilege>().eq(SysPrivilege::getId, id));
        if(CollectionUtils.isEmpty(sysPrivileges)){
            return  Collections.emptyList();
        }
        for (SysPrivilege sysPrivilege : sysPrivileges) {
            Set<Long> currentPrivilegeIds = sysPrivilegeMapper.getPrivilegesByRoleId(roleId);;
            if(currentPrivilegeIds.contains(sysPrivilege.getId())){
                sysPrivilege.setOwn(1);
            }
        }
        return  sysPrivileges;
    }
}
