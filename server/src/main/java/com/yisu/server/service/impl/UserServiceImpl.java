package com.yisu.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yisu.server.common.JwtUtils;
import com.yisu.server.common.LoginResult;
import com.yisu.server.entity.User;
import com.yisu.server.mapper.UserMapper;
import com.yisu.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginResult login(User user) {
        User one = getOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (one != null && one.getPassword().equals(user.getPassword())) {
            String token = jwtUtils.generateToken(one.getUsername(), one.getRole(), one.getId());
            one.setPassword(null); // Hide password
            return new LoginResult(token, one);
        }
        throw new RuntimeException("用户名或密码错误");
    }

    @Override
    public void register(User user) {
        if(StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())){
            throw new RuntimeException("用户名或密码不能为空");
        }
        User one = getOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if(one != null){
            throw new RuntimeException("用户名已存在");
        }
        if(StrUtil.isBlank(user.getRole())){
            user.setRole("MERCHANT"); // Default to merchant if not specified or handle in controller
        }
        save(user);
    }

    @Override
    public User getCurrentUser(Integer userId) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(null); // Hide password
        return user;
    }

    @Override
    public void updateProfile(User user) {
        if (user.getId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        User existing = getById(user.getId());
        if (existing == null) {
            throw new RuntimeException("用户不存在");
        }
        // 只更新允许修改的字段：phone, email, avatar
        if (StrUtil.isNotBlank(user.getPhone())) {
            existing.setPhone(user.getPhone());
        }
        if (StrUtil.isNotBlank(user.getEmail())) {
            existing.setEmail(user.getEmail());
        }
        if (StrUtil.isNotBlank(user.getAvatar())) {
            existing.setAvatar(user.getAvatar());
        }
        updateById(existing);
    }

    @Override
    public void updatePassword(Integer userId, String oldPassword, String newPassword) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (StrUtil.isBlank(oldPassword) || StrUtil.isBlank(newPassword)) {
            throw new RuntimeException("原密码和新密码不能为空");
        }
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(newPassword);
        updateById(user);
    }
}
