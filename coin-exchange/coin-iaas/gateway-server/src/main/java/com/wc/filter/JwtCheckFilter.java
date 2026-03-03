package com.wc.filter;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class JwtCheckFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${no.require.uris:/admin/login,/user/gt/register,/user/login,/user/users/register,/user/sms/sendTo,/user/users/setPassword}")
    private Set<String> noRequireUris;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(!isRequiredToken(exchange)){
            return  chain.filter(exchange);
        }
        String token = getUserToken(exchange);
        if(StringUtils.isEmpty(token)){
            return buildNoAuthorizationResult(exchange);
        }
        Boolean hasKey = redisTemplate.hasKey(token);
        if(hasKey!=null && hasKey){
            return  chain.filter(exchange);
        }
        return buildNoAuthorizationResult(exchange);
    }

    /**
     * 给他用户
     * @return
     */
    private Mono<Void> buildNoAuthorizationResult(ServerWebExchange exchange) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.getHeaders().set("Content-Type","application/json");
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error","NoAuthorization");
        jsonObject.put("errMsg","Token is null or error");
        DataBuffer wrap = serverHttpResponse.bufferFactory().wrap(jsonObject.toJSONString().getBytes());
        return serverHttpResponse.writeWith(Flux.just(wrap));
    }

    /**
     * 获取用户token从请求头
     * @param exchange
     * @return
     */
    private String getUserToken(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return token ==null? null: token.replace("bearer","");
    }

    private boolean isRequiredToken(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        if(noRequireUris.contains(path)){
            return false;
        }
        return Boolean.TRUE;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
