package com.wc.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.CoinServerMapper;
import com.wc.domain.CoinServer;
import com.wc.service.CoinServerService;

@Service
public class CoinServerServiceImpl extends ServiceImpl<CoinServerMapper, CoinServer> implements CoinServerService {

}

