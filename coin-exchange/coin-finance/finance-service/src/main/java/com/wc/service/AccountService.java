package com.wc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wc.domain.Account;
import com.wc.vo.UserTotalAccountVo;

import java.math.BigDecimal;

public interface AccountService  extends IService<Account> {

    /**
     * 暂时锁定用户的资产
     * @param userId
     *  用户的id
     * @param coinId
     * 币种的id
     * @param mum
     * 锁定的金额
     * @param type
     *      资金流水的类型
     * @param orderId
     *      订单的Id
     * @param fee
     *  本次操作的手续费
     */
    void lockUserAmount(Long userId, Long coinId, BigDecimal mum, String type, Long orderId, BigDecimal fee);


    /**
      * 用户资金的划转
      * @param adminId
      * @param userId
      * @param coinId
      * @param num
      * @param fee
      * @param remark
      * @param businessType
      * @param direction
      * @return
      */
     Boolean transferAccountAmount(Long adminId, Long userId, Long coinId,Long orderNum , BigDecimal num, BigDecimal fee,String remark,String businessType,Byte direction);

     /**
      * 给用户扣减钱
      * @param adminId
      *   操作的人
      * @param userId
      * 用户的id
      * @param coinId
      * 币种的id
      * @param orderNum
      * 订单的编号
      * @param num
      * 扣减的余额
      * @param fee
      * 费用
      * @param remark
      * 备注
      * @param businessType
      * 业务类型
      * @param direction
      * 方向
      * @return
      */
     Boolean decreaseAccountAmount(Long adminId, Long userId, Long coinId, Long orderNum ,BigDecimal num, BigDecimal fee,String remark, String businessType, byte direction);

     Account findByUserAndCoin(Long userId, String coinName);

    UserTotalAccountVo getUserTotalAccount(Long userId);
}
