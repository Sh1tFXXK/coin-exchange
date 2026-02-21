package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.AdminBankMapper;
import com.wc.domain.AdminBank;
import com.wc.service.AdminBankService;
@Service
public class AdminBankServiceImpl extends ServiceImpl<AdminBankMapper, AdminBank> implements AdminBankService{

    @Override
    public Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard) {
        return page(page, new LambdaQueryWrapper<AdminBank>().like(!StringUtils.isEmpty(bankCard),AdminBank::getBankCard,bankCard));
    }
}
