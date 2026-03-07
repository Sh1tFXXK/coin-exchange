package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserAddressService extends IService<UserAddress>{


    Page<UserAddress> findByPage(Page<UserAddress> page, Long userId);

    List<UserAddress> getUserAddressByUserId(Long aLong);

    UserAddress getUserAddressByUserIdAndCoinId(String userId, Long coinId);
}
