package com.wc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(value = "登录成功的用户")
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser {

    @ApiModelProperty(value = "用户名称")
    private String username;

    @ApiModelProperty(value = "访问token")
    private String access_token;

    @ApiModelProperty(value = "token  过期时间")
    private Long expire;

    @ApiModelProperty(value = "刷新时间")
    private  String refresh_token;
}
