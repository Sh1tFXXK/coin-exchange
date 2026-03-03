package com.wc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("修改登录密码参数")
public class UpdateLoginParam {

    @ApiModelProperty("旧密码")
    private String oldpassword;

    @ApiModelProperty("新密码")
    private  String newpassword;

    @ApiModelProperty("验证码")
    private String validateCode;
}
