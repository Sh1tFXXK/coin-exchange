package com.wc.feign;

import com.wc.dto.AdminBankDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "admin-service",path = "/adminBanks")
public interface AdminBankServiceFeign {

    @GetMapping("/list")
    List<AdminBankDto> getAllAdminBanks();
}
