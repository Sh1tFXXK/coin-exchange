package com.wc.config;

import cn.hutool.core.lang.Snowflake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGenConfig {

    @Value("${id.machine:0}")
    private int machineCode;

    @Value("${id.app:0}")
    private int appCode;

    @Bean
    public Snowflake snowflake(){
        Snowflake snowflake = new Snowflake(machineCode, appCode);
        return snowflake;
    };
}
