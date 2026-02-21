package com.wc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.wc.domain.SysMenu;
import com.wc.feigh.JwtToken;
import com.wc.feigh.OAuth2FeignClient;
import com.wc.model.LoginResult;
import com.wc.service.SysLoginService;
import com.wc.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysLoginServiceImpl implements SysLoginService {

    @Autowired
    private OAuth2FeignClient oAuth2FeignClient;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${basic.token:Basic Y29pbi1hcGk6Y29pbi1zZWNyZXQ=}")
    private String basicToken;

    /**
     *
     * 登录的实现
     *
     * @param username
     * @param password
     * @return 登
     * @paramusername 用户名
     * @parampassword 用户的密码
     */
    @Override
    public LoginResult login(String username, String password) {
        log.info("用户{}开始登录",username);
        ResponseEntity<JwtToken> tokenResponseEntity = oAuth2FeignClient.getToken("password", username, password,"admin_type",basicToken);
        if (tokenResponseEntity.getStatusCode() != HttpStatus.OK) {
            throw  new ApiException(ApiErrorCode.FAILED);
        }
        JwtToken body = tokenResponseEntity.getBody();
        log.info("远程调用服务成功，{}", JSON.toJSONString(body,true));
        String  accessToken = body.getAccessToken();
        Jwt jwt = JwtHelper.decode(accessToken);
        String claims = jwt.getClaims();
        JSONObject jsonJwt = JSON.parseObject(claims);
        Long userId = Long.valueOf(jsonJwt.getString("user_name"));

        List<SysMenu> menus = sysMenuService.getMenusByUserId(userId);
        JSONArray authoritiesArray = jsonJwt.getJSONArray("authorities");
        List<SimpleGrantedAuthority> authorities = authoritiesArray.stream()
                .map(authoritiesJson-> new SimpleGrantedAuthority(authoritiesJson.toString()))
                .collect(Collectors.toList());

        //redis
        stringRedisTemplate.opsForValue().set(accessToken,"", body.getExpiresIn(),TimeUnit.SECONDS);

         return new LoginResult(body.getTokenType()+" "+accessToken,menus,authorities);
    }
}
