package com.yisu.server.controller;

import com.yisu.server.common.LoginResult;
import com.yisu.server.common.Result;
import com.yisu.server.entity.User;
import com.yisu.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginResult> login(@RequestBody User user) {
        try {
            LoginResult result = userService.login(user);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Object> register(@RequestBody User user) {
        try {
            userService.register(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // Get user info from token (simplified)
    @GetMapping("/info")
    public Result<User> info(@RequestParam String token) {
        // In real app, parse token and get user from DB
        return Result.success(null); 
    }
}
