package com.wc.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.ForexAccountMapper;
import com.wc.domain.ForexAccount;
import com.wc.service.ForexAccountService;

@Service
public class ForexAccountServiceImpl extends ServiceImpl<ForexAccountMapper, ForexAccount> implements ForexAccountService {

}

