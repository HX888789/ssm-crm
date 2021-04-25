package com.hx.crm.settings.service;


import com.hx.crm.exception.LoginException;
import com.hx.crm.settings.domain.User;

import java.util.List;

public interface UserService  {
    User login(String loginAct, String loginPwd,String ip)throws LoginException;

    List<User> getUserList();
}
