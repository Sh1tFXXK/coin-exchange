package com.wc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wc.domain.SysMenu;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {
    List<SysMenu> selectMenuByUserId(Long userId);
}