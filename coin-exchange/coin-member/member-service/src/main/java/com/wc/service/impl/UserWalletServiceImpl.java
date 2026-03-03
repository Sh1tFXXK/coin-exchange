package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.UserWallet;
import com.wc.mapper.UserWalletMapper;
import com.wc.service.UserWalletService;
@Service
public class UserWalletServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet> implements UserWalletService{

    @Override
    public Page<UserWallet> findByPage(Page<UserWallet> page, Long userId) {
        return page(page,new LambdaQueryWrapper<UserWallet>().eq(UserWallet::getUserId ,userId));
    }
}
