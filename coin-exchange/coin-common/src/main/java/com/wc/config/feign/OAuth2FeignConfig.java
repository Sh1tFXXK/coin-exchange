package com.wc.config.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class OAuth2FeignConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 1从request里面获取之前的 Authorizaton 头
        RequestAttributes requestAttributes= RequestContextHolder.getRequestAttributes();
        if(requestAttributes!=null){
            HttpServletRequest request=((ServletRequestAttributes) requestAttributes).getRequest();
            String header=request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isNotBlank(header)) {
                log.info("本次传递的token为{}",header);
                requestTemplate.header(HttpHeaders.AUTHORIZATION,header);
            }
        }else {
            log.info("无法获取token,因为没有上下文的环境");
        }
    }
}
