package com.wc.vo;

import com.wc.domain.User;
import com.wc.domain.UserAuthAuditRecord;
import com.wc.domain.UserAuthInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(value = "用户认证的详细信息")
public class UseAuthInfoVo implements Serializable {


    @ApiModelProperty(value = "用户")
    private User user;


    @ApiModelProperty(value = "用户认证的详细信息")
    private List<UserAuthInfo> userAuthInfoList;

    @ApiModelProperty(value = "用户审核历史")
    private List<UserAuthAuditRecord> userAuthAuditRecordList;
}
