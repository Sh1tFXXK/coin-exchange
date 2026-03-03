package com.wc.service;

import com.wc.model.LoginForm;
import com.wc.model.LoginUser;

public interface LoginService {
    LoginUser login(LoginForm loginForm);
}
