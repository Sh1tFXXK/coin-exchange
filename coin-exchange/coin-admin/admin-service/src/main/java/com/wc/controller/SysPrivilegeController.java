package com.wc.controller;


import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.SysPrivilege;
import com.wc.model.R;
import com.wc.service.SysPrivilegeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;

@RestController
@RequestMapping("privilege")
@Api(tags = "权限的管理")
public class SysPrivilegeController {


    @Autowired
    private SysPrivilegeService sysPrivilegeService;

    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name ="current",value ="当前页") ,
            @ApiImplicitParam(name ="size",value ="每页显示的大小")
    })
    @PreAuthorize("hasAuthority('sys_privilege_query')")
    public R<Page<SysPrivilege>> findByPage(@ApiIgnore Page<SysPrivilege> page){
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<SysPrivilege> sysPrivilegePage = sysPrivilegeService.page(page);
        return  R.ok(sysPrivilegePage);
    }

    @PostMapping
    @ApiOperation(value ="新增一个权限")
    @PreAuthorize("hasAuthority('sys_privilege_create')")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="sysPrivilege",value ="sysPrivilege的json数据")
    })
    public R add(@RequestBody @Validated SysPrivilege sysPrivilege){
        String userIdsStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        sysPrivilege.setCreateBy(Long.valueOf(userIdsStr));
        sysPrivilege.setCreated(new Date());
        sysPrivilege.setLastUpdateTime(new Date());
        boolean save = sysPrivilegeService.save(sysPrivilege);
        if (save) {
            return  R.ok("新增权限成功");
        }
        return R.fail("新增权限失败");
    }

    @PatchMapping
    @ApiOperation(value ="修改一个权限")
    @PreAuthorize("hasAuthority('sys_privilege_update')")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="sysPrivilege",value ="sysPrivilege的json数据")
    })
    public R update(@RequestBody @Validated SysPrivilege sysPrivilege){
        String userIdStr= SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        sysPrivilege.setModifyBy(Long.valueOf(userIdStr));
        sysPrivilege.setLastUpdateTime(new Date());
        boolean save=sysPrivilegeService.updateById(sysPrivilege);
        if(save){
            return R.ok("修改成功") ;
        }
        return R.fail("修改失败");
    }


}
