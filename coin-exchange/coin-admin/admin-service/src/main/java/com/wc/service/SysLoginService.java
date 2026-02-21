package com.wc.service;

import com.wc.model.LoginResult;

/**
 *
 登录的接口
 */
public interface SysLoginService{
/**
 *
 登录的实现
 * @paramusername
 *
用户名
 * @parampassword
 *
 *
用户的密码
 * @return
 *
登录的结果
 */
    LoginResult login(String username , String password) ;
}