package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.UserAddress;
import com.wc.mapper.UserAddressMapper;
import com.wc.service.UserAddressService;
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService{

    @Override
    public Page<UserAddress> findByPage(Page<UserAddress> page, Long userId) {
        return page(page,new
                LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId,userId));
    }
}
