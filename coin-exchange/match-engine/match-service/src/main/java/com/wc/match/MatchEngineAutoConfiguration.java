package com.wc.match;

import com.lmax.disruptor.EventHandler;
import com.wc.disruptor.OrderEvent;
import com.wc.disruptor.OrderEventHandler;
import com.wc.model.OrderBooks;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;

@Configuration
@EnableConfigurationProperties(value = MatchEngineProperties.class)
public class MatchEngineAutoConfiguration {

    private MatchEngineProperties matchEngineProperties;


    public MatchEngineAutoConfiguration(MatchEngineProperties matchEngineProperties) {
        this.matchEngineProperties = matchEngineProperties;
    }


    @Bean("eventHandlers")
    public EventHandler<OrderEvent>[] eventHandlers() {
        // 获取配置中的所有交易对符号及其精度配置
        Map<String, MatchEngineProperties.CoinScale> symbols = matchEngineProperties.getSymbols();
        Set<Map.Entry<String, MatchEngineProperties.CoinScale>> entries = symbols.entrySet();
        
        // 初始化事件处理器数组，大小为交易对数量
        EventHandler<OrderEvent>[] eventHandlers = new EventHandler[symbols.size()];
        int i = 0;
        
        // 遍历每个交易对配置，创建对应的订单簿和事件处理器
        for (Map.Entry<String, MatchEngineProperties.CoinScale> entry : entries) {
            String symbol = entry.getKey();
            MatchEngineProperties.CoinScale value = entry.getValue();
            OrderBooks orderBooks;
            
            // 根据是否存在精度配置，选择相应的构造函数初始化订单簿
            if (value != null) {
                // 存在精度配置：使用指定的币精度和基础币精度
                orderBooks = new OrderBooks(symbol, value.getCoinScale(), value.getBaseCoinScale());
            } else {
                // 无精度配置：使用默认构造函数
                orderBooks = new OrderBooks(symbol);
            }
            
            // 将创建的订单簿封装到事件处理器中并存入数组
            eventHandlers[i++] = new OrderEventHandler(orderBooks);
        }
        
        return eventHandlers;
    }

}
