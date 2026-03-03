package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.UserAuthInfo;
import com.wc.mapper.UserAuthInfoMapper;
import com.wc.service.UserAuthInfoService;

import java.util.Collections;
import java.util.List;

@Service
public class UserAuthInfoServiceImpl extends ServiceImpl<UserAuthInfoMapper, UserAuthInfo> implements UserAuthInfoService{

    @Override
    public List<UserAuthInfo> getUserAuthInfoByCode(Long authCode) {
        return list(new LambdaQueryWrapper<UserAuthInfo>().eq(UserAuthInfo::getAuthCode,authCode));
    }

    @Override
    public List<UserAuthInfo> getUserAuthInfoById(Long id) {
        List<UserAuthInfo> list = list(new LambdaQueryWrapper<UserAuthInfo>().eq(UserAuthInfo::getId, id));
        return list == null ? Collections.emptyList() : list;
    }
}
