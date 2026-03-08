package com.wc.match;

import com.wc.model.Order;
import com.wc.model.OrderBooks;

/**
 * 撮合/交易的接口定义
 */
public interface MatchService {

    /**
     * 进行订单的撮合交易
     * @param order
     */
    void match(OrderBooks orderBooks, Order order) ;
}
