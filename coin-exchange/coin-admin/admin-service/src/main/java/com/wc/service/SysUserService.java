package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.Collection;

public interface SysUserService extends IService<SysUser>{


    Page<SysUser> findByPage(Page<SysUser> page, String mobile, String fullname);

    boolean addUser(SysUser sysUser);

    boolean removeByIds(Collection< ? extends Serializable> idList);
}
