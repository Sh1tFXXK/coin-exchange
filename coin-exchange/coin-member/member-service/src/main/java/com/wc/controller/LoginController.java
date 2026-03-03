package com.wc.controller;

import com.wc.model.LoginForm;
import com.wc.model.LoginUser;
import com.wc.model.R;
import com.wc.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags ="登录的控制器")
public class LoginController {


    @Autowired
    private LoginService loginService;



    @PostMapping("/login")
    @ApiOperation(value ="会员的登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="loginForm",value ="登录的表单参数")
    })
    public R <LoginUser>login(@RequestBody @Validated LoginForm loginForm) {
        LoginUser loginUser= loginService.login(loginForm) ;
        return R.ok(loginUser) ;
    }
}
