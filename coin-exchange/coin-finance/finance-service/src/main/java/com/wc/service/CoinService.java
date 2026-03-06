package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wc.domain.Coin;
import com.wc.dto.CoinDto;

import java.util.List;

public interface CoinService extends IService<Coin> {

    Page<Coin> findByPage(String name, String type, Byte status, String title, String walletType, Page<Coin> page);

    List<Coin> getCoinsByStatus(Byte status);

    List<CoinDto> findList(List<Long> coinIds);

    /**
     * 使用货币的名称来查询货币
     * @param coinName
     * @return
     */
    Coin getCoinByCoinName(String coinName);
}
