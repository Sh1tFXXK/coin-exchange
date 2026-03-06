package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.dto.AdminBankDto;
import com.wc.mappers.AdminBankDtoMappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.AdminBankMapper;
import com.wc.domain.AdminBank;
import com.wc.service.AdminBankService;
@Service
public class AdminBankServiceImpl extends ServiceImpl<AdminBankMapper, AdminBank> implements AdminBankService{

    @Autowired
    private AdminBankMapper adminBankMapper;

    @Override
    public Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard) {
        return page(page, new LambdaQueryWrapper<AdminBank>().like(!StringUtils.isEmpty(bankCard),AdminBank::getBankCard,bankCard));
    }


    /**
     * 查询所有的银行开启信息
     *
     * @return
     */
    @Override
    public List<AdminBankDto> getAllAdminBanks() {
        List<AdminBank> adminBanks = list(new LambdaQueryWrapper<AdminBank>().eq(AdminBank::getStatus, 1));
        if (CollectionUtils.isEmpty(adminBanks)){
            return Collections.emptyList() ;
        }
        List<AdminBankDto> adminBankDtos = AdminBankDtoMappers.INSTANCE.toConvertDto(adminBanks);
        return adminBankDtos;
    }

}
