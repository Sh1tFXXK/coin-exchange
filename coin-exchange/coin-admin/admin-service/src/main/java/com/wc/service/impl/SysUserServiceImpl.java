package com.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.SysUserRole;
import com.wc.mapper.SysUserRoleMapper;
import com.wc.service.SysUserRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.SysUserMapper;
import com.wc.domain.SysUser;
import com.wc.service.SysUserService;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService{



    @Autowired
    private  SysUserRoleService sysUserRoleService;

    @Override
    public Page<SysUser> findByPage(Page<SysUser> page, String mobile, String fullName) {

        Page<SysUser> userPage = page(page, new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.isNotBlank(mobile), SysUser::getMobile, mobile)
                .like(StringUtils.isNotBlank(fullName), SysUser::getFullname, fullName));

        List<SysUser> records = userPage.getRecords();
        if(!CollectionUtils.isEmpty(records)){
            for(SysUser record:records){
                List<SysUserRole> list = sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, record.getId()));
                if(!CollectionUtils.isEmpty(list)){
                    record.setRole_strings(list.stream()
                            .map(sysUserRole -> sysUserRole.getRoleId().toString())
                            .collect(Collectors.joining(",")));
                }

            }
        }
        return userPage;
    }

    @Override
    public boolean addUser(SysUser sysUser) {

        //加密。。。
        String password = sysUser.getPassword();
        String roleStrings = sysUser.getRole_strings();
        String encode = new BCryptPasswordEncoder().encode(password);
        sysUser.setPassword(encode);

        boolean save = super.save(sysUser);

        if(save) {
            if(StringUtils.isNotBlank(roleStrings)) {
                String[] roles = roleStrings.split(",");
                List<SysUserRole> roleList = new ArrayList<>(roles.length);
                for(String role : roles) {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setRoleId(Long.valueOf(role));
                    sysUserRole.setUserId(sysUser.getId());
                    roleList.add(sysUserRole);
                }
                sysUserRoleService.saveBatch(roleList);
            }
        }
        return save;
    }


    @Override
    public boolean removeByIds(Collection< ? extends Serializable> idList) {
        boolean b=super.removeByIds(idList);
        sysUserRoleService.remove(new
                LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId,idList)) ;
        return b;
    }
}
