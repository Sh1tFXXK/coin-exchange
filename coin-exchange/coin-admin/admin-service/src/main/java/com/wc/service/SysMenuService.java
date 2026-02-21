package com.wc.service;

import com.wc.domain.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysMenuService extends IService<SysMenu>{


    List<SysMenu> getMenusByUserId(Long userId);
}
