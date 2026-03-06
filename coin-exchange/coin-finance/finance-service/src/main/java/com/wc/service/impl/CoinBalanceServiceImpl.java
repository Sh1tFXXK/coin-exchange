package com.wc.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.CoinBalance;
import com.wc.mapper.CoinBalanceMapper;
import com.wc.service.CoinBalanceService;

@Service
public class CoinBalanceServiceImpl extends ServiceImpl<CoinBalanceMapper, CoinBalance> implements CoinBalanceService {

}

