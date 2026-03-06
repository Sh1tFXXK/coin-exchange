package com.wc.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.ForexCoinMapper;
import com.wc.domain.ForexCoin;
import com.wc.service.ForexCoinService;

@Service
public class ForexCoinServiceImpl extends ServiceImpl<ForexCoinMapper, ForexCoin> implements ForexCoinService {

}

