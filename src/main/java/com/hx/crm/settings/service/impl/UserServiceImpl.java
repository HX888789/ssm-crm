package com.hx.crm.settings.service.impl;

import com.hx.crm.exception.LoginException;
import com.hx.crm.settings.dao.UserDao;
import com.hx.crm.settings.domain.User;
import com.hx.crm.settings.service.UserService;
import com.hx.crm.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public User login(String loginAct, String loginPwd,String ip)throws LoginException {
        Map<String ,Object> map=new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user=userDao.login(map);

        if(user==null){
            throw new LoginException("账号密码错误");
        }
//        如果程序往下执行，说明账号密码正确，需要继续判断其他三项信息
        String expireTime=user.getExpireTime();
        String currentTime= DateTimeUtil.getSysTime();
        if(expireTime.compareTo(currentTime)<0){
            throw new LoginException("账号已失效");
        }
//        锁定状态判断
        if("0".equals(user.getLockState())){
            throw new LoginException("账号已锁定");
        }
//        判断ip地址是否符合 允许ip是否包含登录ip
//        if(user.getAllowIps().contains(ip)){
//            throw new LoginException("IP地址受限");
//        }
        return user;
    }

    @Override
    public List<User> getUserList() {
        List<User> ulist=userDao.getUserList();
        return ulist;
    }
}

