package com.yisu.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yisu.server.common.LoginResult;
import com.yisu.server.entity.User;

public interface UserService extends IService<User> {
    LoginResult login(User user);
    void register(User user);
}
