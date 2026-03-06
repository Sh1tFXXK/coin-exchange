package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wc.domain.AdminAddress;

public interface AdminAddressService  extends IService<AdminAddress> {
    Page<AdminAddress> findByPage(Page<AdminAddress> page, Long coinId);
}
