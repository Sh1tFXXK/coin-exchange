package com.wc.service;

import com.wc.domain.UserAuthInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserAuthInfoService extends IService<UserAuthInfo>{


    List<UserAuthInfo> getUserAuthInfoByCode(Long authCode);

    List<UserAuthInfo> getUserAuthInfoById(Long id);
}
