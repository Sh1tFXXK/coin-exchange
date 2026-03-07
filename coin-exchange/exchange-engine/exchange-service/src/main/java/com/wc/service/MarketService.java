package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wc.domain.Market;
import dto.MarketDto;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface MarketService extends IService<Market> {


    /**
     * 分页查询市场的配置
     *
     * @param page        分页参数
     * @param tradeAreaId 交易区域的ID
     * @param status      状态
     * @return
     */
    Page<Market> findByPage(Page<Market> page, Long tradeAreaId, Byte status);

    List<Market> getMarkersByTradeAreaId(Long id);

    Market getMarkerBySymbol(@NotNull String symbol);

    MarketDto findByCoinId(Long buyCoinId, Long sellCoinId);

    List<Market> queryByAreaId(Long id);
}