package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.EntrustOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wc.domain.ExchangeTrade;
import com.wc.param.OrderParam;
import com.wc.vo.TradeEntrustOrderVo;

public interface EntrustOrderService extends IService<EntrustOrder>{

    Page<EntrustOrder> findByPage(Page<EntrustOrder> page, Long userId, String symbol, Integer type);

    Page<TradeEntrustOrderVo> getHistoryEntrustOrder(Page<EntrustOrder> page, String symbol, Long userId);

    Page<TradeEntrustOrderVo> getEntrustOrder(Page<EntrustOrder> page, String symbol, Long userId);

    Boolean createEntrustOrder(Long userId, OrderParam orderParam);

    void cancleEntrustOrder(Long orderId);
}