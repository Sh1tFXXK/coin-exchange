package com.wc.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.ForexClosePositionOrderMapper;
import com.wc.domain.ForexClosePositionOrder;
import com.wc.service.ForexClosePositionOrderService;

@Service
public class ForexClosePositionOrderServiceImpl extends ServiceImpl<ForexClosePositionOrderMapper, ForexClosePositionOrder> implements ForexClosePositionOrderService {

}

