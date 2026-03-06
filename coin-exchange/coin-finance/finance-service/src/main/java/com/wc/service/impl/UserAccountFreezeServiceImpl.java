package com.wc.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.UserAccountFreeze;
import com.wc.mapper.UserAccountFreezeMapper;
import com.wc.service.UserAccountFreezeService;

@Service
public class UserAccountFreezeServiceImpl extends ServiceImpl<UserAccountFreezeMapper, UserAccountFreeze> implements UserAccountFreezeService {

}

