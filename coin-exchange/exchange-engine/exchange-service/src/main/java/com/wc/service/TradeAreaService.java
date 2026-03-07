package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wc.domain.TradeArea;
import com.wc.vo.TradeAreaMarketVo;
import dto.TradeAreaDto;

import java.util.List;

public interface TradeAreaService extends IService<TradeArea> {


    /**
     * 分页查询交易区域
     *
     * @param page   分页参数
     * @param name   交易区域的名称
     * @param status 交易区域的状态
     * @return
     */
    Page<TradeArea> findByPage(Page<TradeArea> page, String name, Byte status);

    List<TradeArea> findAll(Byte status);

    List<TradeAreaMarketVo> findTradeAreaMarket();

    List<TradeAreaMarketVo> getUserFavoriteMarkets(Long userId);

    List<TradeAreaDto> findAllTradeAreaAndMarket();
}