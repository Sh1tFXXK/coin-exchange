package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.Account;
import com.wc.domain.AccountDetail;
import com.wc.domain.Coin;
import com.wc.domain.Config;
import com.wc.mapper.AccountMapper;
import com.wc.service.AccountDetailService;
import com.wc.service.AccountService;
import com.wc.service.CoinService;
import com.wc.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private CoinService coinService;

    @Autowired
    private ConfigService configService;


    /**
     * 暂时锁定用户的资产
     *
     * @param userId  用户的id
     * @param coinId  币种的id
     * @param mum     锁定的金额
     * @param type    资金流水的类型
     * @param orderId 订单的Id
     * @param fee
     */
    @Override
    public void lockUserAmount(Long userId, Long coinId, BigDecimal mum, String type, Long orderId, BigDecimal fee) {
        Account account = getOne(new LambdaQueryWrapper<Account>().eq(Account::getUserId, userId)
                .eq(Account::getCoinId, coinId)
        );
        if (account == null) {
            throw new IllegalArgumentException("您输入的资产类型不存在");
        }
        BigDecimal balanceAmount = account.getBalanceAmount();
        if (balanceAmount.compareTo(mum) < 0) { // 库存的操作
            throw new IllegalArgumentException("账号的资金不足");
        }
        account.setBalanceAmount(balanceAmount.subtract(mum));
        account.setFreezeAmount(account.getFreezeAmount().add(mum));
        boolean updateById = updateById(account);
        if (updateById) {  // 增加流水记录
            AccountDetail accountDetail = new AccountDetail(
                    null,
                    userId,
                    coinId,
                    account.getId(),
                    account.getId(), // 如果该订单时邀请奖励,有我们的ref的account ,否则,值和account 是一样的
                    orderId,
                    (byte) 2,
                    type,
                    mum,
                    fee,
                    "用户提现",
                    null,
                    null,
                    null
            );
            accountDetailService.save(accountDetail);
        }
    }


    /**
     * 查询某个用户的货币资产
     *
     * @param userId   用户的id
     * @param coinName 货币的名称
     * @return
     */
    @Override
    public Account findByUserAndCoin(Long userId, String coinName) {
        Coin coin = coinService.getCoinByCoinName(coinName);
        if (coin == null) {
            throw new IllegalArgumentException("货币不存在");
        }
        Account account = getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getUserId, userId)
                .eq(Account::getCoinId, coin.getId())
        );
        if (account == null) {
            throw new IllegalArgumentException("该资产不存在");
        }

        Config sellRateConfig = configService.getConfigByCode("USDT2CNY");
        account.setSellRate(new BigDecimal(sellRateConfig.getValue())); // 出售的费率

        Config setBuyRateConfig = configService.getConfigByCode("CNY2USDT");
        account.setBuyRate(new BigDecimal(setBuyRateConfig.getValue())); // 买进来的费率

        return account;
    }

    @Autowired
    private AccountDetailService accountDetailService;

    /**
     * 用户资金的划转
     *
     * @param adminId
     * @param userId
     * @param coinId
     * @param num
     * @param fee
     * @return
     */
    @Override
    public Boolean transferAccountAmount(Long adminId, Long userId, Long coinId, Long orderNum, BigDecimal num, BigDecimal fee, String remark, String businessType, Byte direction) {
        Account coinAccount = getCoinAccount(coinId, userId);
        if (coinAccount == null) {
            throw new IllegalArgumentException("用户当前的该币种的余额不存在");
        }
        // 增加一条流水记录
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setCoinId(coinId);
        accountDetail.setUserId(userId);
        accountDetail.setAmount(num);
        accountDetail.setFee(fee);
        accountDetail.setOrderId(orderNum);
        accountDetail.setAccountId(coinAccount.getId());
        accountDetail.setRefAccountId(coinAccount.getId());
        accountDetail.setRemark(remark);
        accountDetail.setBusinessType(businessType);
        accountDetail.setDirection(direction);
        accountDetail.setCreated(new Date());
        boolean save = accountDetailService.save(accountDetail);
        if (save) { // 用户余额的增加
            coinAccount.setBalanceAmount(coinAccount.getBalanceAmount().add(num));
            boolean updateById = updateById(coinAccount);
            return updateById;
        }
        return save;
    }

    /**
     * 给用户扣减钱
     *
     * @param adminId      操作的人
     * @param userId       用户的id
     * @param coinId       币种的id
     * @param orderNum     订单的编号
     * @param num          扣减的余额
     * @param fee          费用
     * @param remark       备注
     * @param businessType 业务类型
     * @param direction    方向
     * @return
     */
    @Override
    public Boolean decreaseAccountAmount(Long adminId, Long userId, Long coinId, Long orderNum, BigDecimal num, BigDecimal fee, String remark, String businessType, byte direction) {
        Account coinAccount = getCoinAccount(coinId, userId);
        if (coinAccount == null) {
            throw new IllegalArgumentException("账户不存在");
        }
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUserId(userId);
        accountDetail.setCoinId(coinId);
        accountDetail.setAmount(num);
        accountDetail.setFee(fee);
        accountDetail.setAccountId(coinAccount.getId());
        accountDetail.setRefAccountId(coinAccount.getId());
        accountDetail.setRemark(remark);
        accountDetail.setBusinessType(businessType);
        accountDetail.setDirection(direction);
        boolean save = accountDetailService.save(accountDetail);
        if (save) { // 新增了流水记录
            BigDecimal balanceAmount = coinAccount.getBalanceAmount();
            BigDecimal result = balanceAmount.add(num.multiply(BigDecimal.valueOf(-1)));
            if (result.compareTo(BigDecimal.ONE) > 0) {
                coinAccount.setBalanceAmount(result);
                return updateById(coinAccount);
            } else {
                throw new IllegalArgumentException("余额不足");
            }
        }
        return false;
    }

    /**
     * 获取用户的某种币的资产
     *
     * @param coinId
     * @param userId
     * @return
     */
    private Account getCoinAccount(Long coinId, Long userId) {

        return getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getCoinId, coinId)
                .eq(Account::getUserId, userId)
                .eq(Account::getStatus, 1)
        );
    }
}
