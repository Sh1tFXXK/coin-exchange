package com.wc.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.ForexOpenPositionOrderMapper;
import com.wc.domain.ForexOpenPositionOrder;
import com.wc.service.ForexOpenPositionOrderService;

@Service
public class ForexOpenPositionOrderServiceImpl extends ServiceImpl<ForexOpenPositionOrderMapper, ForexOpenPositionOrder> implements ForexOpenPositionOrderService {

}

