package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.CashWithdrawAuditRecord;
import com.wc.domain.CashWithdrawals;
import com.wc.model.CashSellParam;

public interface CashWithdrawalsService {
    Page<CashWithdrawals> findByPage(Page<CashWithdrawals> page, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime);

    boolean updateWithdrawalsStatus(Long userId, CashWithdrawAuditRecord cashWithdrawAuditRecord);

    boolean sell(Long userId, CashSellParam cashSellParam);
}
