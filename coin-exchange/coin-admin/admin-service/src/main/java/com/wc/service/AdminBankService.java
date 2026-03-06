package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.AdminBank;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wc.dto.AdminBankDto;

import java.util.List;

public interface AdminBankService extends IService<AdminBank>{


    Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard);

    List<AdminBankDto> getAllAdminBanks();
}
