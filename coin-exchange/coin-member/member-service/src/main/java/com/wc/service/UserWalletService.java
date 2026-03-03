package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.UserWallet;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserWalletService extends IService<UserWallet>{


    Page<UserWallet> findByPage(Page<UserWallet> page, Long userId);
}
