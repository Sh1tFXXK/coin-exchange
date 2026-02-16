package com.wc.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.SysRolePrivilegeUser;
import com.wc.mapper.SysRolePrivilegeUserMapper;
import com.wc.service.SysRolePrivilegeUserService;
@Service
public class SysRolePrivilegeUserServiceImpl extends ServiceImpl<SysRolePrivilegeUserMapper, SysRolePrivilegeUser> implements SysRolePrivilegeUserService{

}
