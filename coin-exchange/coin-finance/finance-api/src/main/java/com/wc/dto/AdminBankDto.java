package com.wc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "银行卡参数")
public class AdminBankDto {
    @ApiModelProperty(value = "开户人名称")
    private String name;

    @ApiModelProperty(value = "开户银行")
    private String bankName;

    @ApiModelProperty(value = "开户银行卡号")
    private String bankCard;
}
