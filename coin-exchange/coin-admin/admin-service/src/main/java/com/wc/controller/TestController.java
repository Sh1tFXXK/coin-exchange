package com.wc.controller;

import com.wc.domain.SysUser;
import com.wc.model.R;
import com.wc.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "后台管理系统的测试接口")
public class TestController {

    @Autowired
    private SysUserService sysUserService;


    @ApiOperation(value = "查询用户详情")
    @GetMapping("/user/ingfo/{id}")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id",value = "用户id")
    )
    public R<SysUser> getSysUserInfo(@PathVariable("id") Long id){
        SysUser byId = sysUserService.getById(id);
        return R.ok(byId);
    }
}
