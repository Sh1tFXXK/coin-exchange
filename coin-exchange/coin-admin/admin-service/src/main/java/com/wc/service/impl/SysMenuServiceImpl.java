package com.wc.service.impl;

import com.wc.service.SysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.SysMenuMapper;
import com.wc.domain.SysMenu;
import com.wc.service.SysMenuService;
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService{


    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> getMenusByUserId(Long userId) {
        if(sysRoleService.isSuperAdmin(userId)){
            return list();
        }
        return sysMenuMapper.selectMenuByUserId(userId);
    }

}
