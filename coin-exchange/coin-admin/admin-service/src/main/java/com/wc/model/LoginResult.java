package com.wc.model;



import com.wc.domain.SysMenu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "登录的结果")
public class LoginResult {

    @ApiModelProperty(value = "登录的token")
    private  String token;


    @ApiModelProperty(value = "该用户的菜单数据")
    private List<SysMenu> menus;

    @ApiModelProperty(value = "该用户的权限数据")
    private List<SimpleGrantedAuthority> authorities;
}
