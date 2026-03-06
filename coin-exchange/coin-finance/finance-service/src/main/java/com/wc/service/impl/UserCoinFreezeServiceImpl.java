package com.wc.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.UserCoinFreeze;
import com.wc.mapper.UserCoinFreezeMapper;
import com.wc.service.UserCoinFreezeService;

@Service
public class UserCoinFreezeServiceImpl extends ServiceImpl<UserCoinFreezeMapper, UserCoinFreeze> implements UserCoinFreezeService {

}

