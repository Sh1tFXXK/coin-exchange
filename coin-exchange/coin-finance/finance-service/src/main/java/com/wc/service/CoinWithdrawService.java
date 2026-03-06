package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.CoinWithdraw;

public interface CoinWithdrawService {
    Page<CoinWithdraw> findByPage(Page<CoinWithdraw> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime);

    Page<CoinWithdraw> findUserCoinWithdraw(Long userId, Long coinId, Page<CoinWithdraw> page);
}
