package com.wc.match.impl;

import com.wc.domain.ExchangeTrade;
import com.wc.enums.OrderDirection;
import com.wc.match.MatchService;
import com.wc.match.MatchServiceFactory;
import com.wc.match.MatchStrategy;
import com.wc.model.MergeOrder;
import com.wc.model.Order;
import com.wc.model.OrderBooks;
import com.wc.model.TradePlate;
import com.wc.rocket.Source;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class LimitPriceMatchServiceImpl implements MatchService, InitializingBean {


    @Autowired
    private Source source;

    /**
     * 进行订单的撮合交易
     *
     * @param orderBooks
     * @param order
     */
    @Override
    public void match(OrderBooks orderBooks, Order order) {
        if (order.isCancelOrder()) {
            orderBooks.cancelOrder(order);
            Message<String> message = MessageBuilder.withPayload(order.getOrderId()).build();
            source.cancelOrderOut().send(message);
            return; // 取消单的操作
        }


        // 1 进行数据的校验
        if (order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        // 2 获取一个挂单队列
        Iterator<Map.Entry<BigDecimal, MergeOrder>> markerQueueIterator = null;
        if (order.getOrderDirection() == OrderDirection.BUY) {
            markerQueueIterator = orderBooks.getCurrentLimitPriceIterator(OrderDirection.SELL);
        } else {
            markerQueueIterator = orderBooks.getCurrentLimitPriceIterator(OrderDirection.BUY);
        }


        // 是否退出循环
        boolean exitLoop = false;

        // 已经完成的订单
        List<Order> completedOrders = new ArrayList<>();
        // 产生的交易记录
        List<ExchangeTrade> exchangeTrades = new ArrayList<>();

        // 3 循环我们的队列
        while (markerQueueIterator.hasNext() && !exitLoop) {
            Map.Entry<BigDecimal, MergeOrder> markerOrderEntry = markerQueueIterator.next();
            BigDecimal markerPrice = markerOrderEntry.getKey();
            MergeOrder markerMergeOrder = markerOrderEntry.getValue();
            // 我花10 块钱买东西 ,别人的东西如果大于10 块 ,我就买不了
            if (order.getOrderDirection() == OrderDirection.BUY && order.getPrice().compareTo(markerPrice) < 0) {
                break;
            }

            // 我出售一个东西 10 ,结果有个人花5块钱
            if (order.getOrderDirection() == OrderDirection.SELL && order.getPrice().compareTo(markerPrice) > 0) {
                break;
            }
            Iterator<Order> markerIterator = markerMergeOrder.iterator();
            while (markerIterator.hasNext()) {
                Order marker = markerIterator.next();

                ExchangeTrade exchangeTrade = processMath(order, marker, orderBooks);
                exchangeTrades.add(exchangeTrade);
                if (order.isCompleted()) { // 经过一圈的吃单,我吃饱了
                    completedOrders.add(order);
                    exitLoop = true; // 退出最外层的循环
                    break;  // 退出当前的MergeOrder的循环
                }

                if (marker.isCompleted()) {// MergeOrder 的一个小的订单完成了
                    completedOrders.add(marker);
                    markerIterator.remove();
                }

            }

            if (markerMergeOrder.size() == 0) { // MergeOrder 已经吃完了
                markerQueueIterator.remove(); // 将该MergeOrder 从树上移除掉
            }

        }

        // 4 若我们的订单没有完成
        if (order.getAmount().compareTo(order.getTradedAmount()) > 0) {
            orderBooks.addOrder(order);
        }

        if (exchangeTrades.size() > 0) {
            // 5 发送交易记录
            handlerExchangeTrades(exchangeTrades);

        }
        if (completedOrders.size() > 0) {

            // 6 发送已经成交的交易记录
            completedOrders(completedOrders);
        }


    }

    /**
     * 进行委托单的匹配撮合交易
     *
     * @param taker  吃单
     * @param marker 挂单
     * @return ExchangeTrade 交易记录
     */
    private ExchangeTrade processMath(Order taker, Order marker, OrderBooks orderBooks) {
        // 1 定义交易的变量
        // 成交的价格
        BigDecimal dealPrice = marker.getPrice();
        // 成交的数量
        BigDecimal turnoverAmount = BigDecimal.ZERO;
        // 本次需要的数量
        BigDecimal needAmount = calcTradeAmount(taker); // 10  20
        // 本次提供给你的数量
        BigDecimal providerAmount = calcTradeAmount(marker); // 20 10


        turnoverAmount = needAmount.compareTo(providerAmount) <= 0 ? needAmount : providerAmount;

        if (turnoverAmount.compareTo(BigDecimal.ZERO) == 0) {
            return null; // 无法成交
        }

        // 设置本次吃单的成交数据
        taker.setTradedAmount(taker.getTradedAmount().add(turnoverAmount));
        BigDecimal turnoverTaker = turnoverAmount.multiply(dealPrice).setScale(orderBooks.getCoinScale(), RoundingMode.HALF_UP);
        taker.setTurnover(turnoverTaker);

        // 设置本次挂单的成交数据
        marker.setTradedAmount(marker.getTradedAmount().add(turnoverAmount));
        BigDecimal markerTurnover = turnoverAmount.multiply(dealPrice).setScale(orderBooks.getBaseCoinScale(), RoundingMode.HALF_UP);
        marker.setTurnover(markerTurnover);

        ExchangeTrade exchangeTrade = new ExchangeTrade();

        exchangeTrade.setAmount(turnoverAmount); // 设置购买的数量
        exchangeTrade.setPrice(dealPrice);  // 设置购买的价格
        exchangeTrade.setTime(System.currentTimeMillis()); // 设置成交的时间
        exchangeTrade.setSymbol(orderBooks.getSymbol());  // 设置成交的交易对
        exchangeTrade.setDirection(taker.getOrderDirection());  // 设置交易的方法
        exchangeTrade.setSellOrderId(marker.getOrderId()); // 设置出售方的id
        exchangeTrade.setBuyOrderId(taker.getOrderId()); // 设置买方的id

        exchangeTrade.setBuyTurnover(taker.getTurnover()); // 设置买方的交易额
        exchangeTrade.setSellTurnover(marker.getTurnover()); // 设置卖方的交易额

        /**
         * 处理盘口:
         *  我们的委托单肯定是: 将挂单的数据做了一部分消耗
         */
        if (marker.getOrderDirection() == OrderDirection.BUY) {
            // 减少挂单的数据量
            orderBooks.getBuyTradePlate().remove(marker, turnoverAmount);
        } else {
            orderBooks.getSellTradePlate().remove(marker, turnoverAmount);
        }

        return exchangeTrade;

    }

    /**
     * 计算本次的交易额
     *
     * @param order
     * @return
     */
    private BigDecimal calcTradeAmount(Order order) {

        return order.getAmount().subtract(order.getTradedAmount());

    }

    /**
     * 发送盘口数据,供以后我们前端的数据更新
     *
     * @param tradePlate
     */
    private void sendTradePlateData(TradePlate tradePlate) {
        Message<TradePlate> message = MessageBuilder
                .withPayload(tradePlate)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();
        source.plateOut().send(message);
    }

    /***
     * 订单的完成
     * @param completedOrders
     */
    private void completedOrders(List<Order> completedOrders) {

        Message<List<Order>> message = MessageBuilder
                .withPayload(completedOrders)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();
        source.completedOrdersOut().send(message);
    }

    /**
     * 处理订单的记录
     *
     * @param exchangeTrades
     */
    private void handlerExchangeTrades(List<ExchangeTrade> exchangeTrades) {

        Message<List<ExchangeTrade>> message = MessageBuilder
                .withPayload(exchangeTrades)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();
        source.exchangeTradesOut().send(message);
        log.info("本次成交的记录为:" + exchangeTrades);
        log.info("本次成交的记录为:" + exchangeTrades);

    }


//你的问题非常敏锐！确实，理论上可以“需要时再查找”，但在高并发撮合引擎的场景下，采用**启动时注册（报到）**主要有以下三个核心原因：
//
//1.  **性能极致优化（避免运行时开销）**：
//    撮合引擎对延迟极其敏感（通常要求微秒级）。如果每次来一个订单都去工厂里遍历或查找“哪个策略是限价策略”，会消耗宝贵的 CPU 时间。通过启动时注册，工厂内部通常会将策略存储在 `HashMap` 中，运行时可以直接通过 `MatchStrategy.LIMIT_PRICE` 这个 Key **O(1) 时间复杂度**直接拿到实例，零延迟。
//
//2.  **快速失败（Fail-Fast）机制**：
//    在系统启动阶段（`afterPropertiesSet`），如果注册失败（比如工厂为空、策略冲突），系统会立即报错并停止启动。这能确保上线的系统一定是配置完整的。如果是“按需调用”，可能直到第一笔真实交易进来时才发现策略没配置好，导致生产事故。
//
//3.  **解耦与扩展性**：
//    业务代码（如订单入口）只需要依赖抽象的 `MatchServiceFactory` 和枚举 `MatchStrategy`，不需要知道具体的实现类 `LimitPriceMatchServiceImpl` 是否存在。未来如果要增加“市价撮合”或“冰山订单”，只需新写一个类并在启动时注册，主流程代码一行都不用改。
//
//**总结**：这就好比打仗前先把武器库整理好（启动注册），士兵上战场直接拿钥匙开对应的柜子（O(1) 获取）；而不是等敌人冲过来了，再去仓库里翻找武器在哪里（运行时查找）。
//
//虽然逻辑上不需要改动代码即可实现该模式，但为了回应你的思考，我在代码中增加了注释来明确这一设计意图：

    @Override
    public void afterPropertiesSet() throws Exception {
        // 在 Spring 容器初始化完成时，立即将当前服务注册到工厂中。
        // 目的：
        // 1. 性能优化：将策略预加载到内存 Map 中，避免撮合时实时查找带来的延迟。
        // 2. 快速失败：若注册失败则在启动阶段报错，防止运行中出现“找不到策略”的严重异常。
        // 3. 解耦：业务调用方仅依赖策略枚举，无需感知具体实现类。
        MatchServiceFactory.addMatchService(MatchStrategy.LIMIT_PRICE, this);
    }
}
