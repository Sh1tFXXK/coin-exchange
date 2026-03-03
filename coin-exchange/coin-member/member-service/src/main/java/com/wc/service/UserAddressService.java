package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserAddressService extends IService<UserAddress>{


    Page<UserAddress> findByPage(Page<UserAddress> page, Long userId);
}
