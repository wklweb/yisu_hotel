package com.yisu.server.controller;

import com.yisu.server.common.JwtUtils;
import com.yisu.server.common.LoginResult;
import com.yisu.server.common.Result;
import com.yisu.server.entity.User;
import com.yisu.server.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

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
    
    // Get user info from token
    @GetMapping("/info")
    public Result<User> info(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.error("未登录");
            }
            String token = authHeader.substring(7);
            Claims claims = jwtUtils.getClaimsByToken(token);
            if (claims == null) {
                return Result.error("Token无效");
            }
            Integer userId = (Integer) claims.get("userId");
            User user = userService.getCurrentUser(userId);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // Update user profile (avatar, email, phone)
    @PutMapping("/profile")
    public Result<Object> updateProfile(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                        @RequestBody User user) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.error("未登录");
            }
            String token = authHeader.substring(7);
            Claims claims = jwtUtils.getClaimsByToken(token);
            if (claims == null) {
                return Result.error("Token无效");
            }
            Integer userId = (Integer) claims.get("userId");
            user.setId(userId);
            userService.updateProfile(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // Update password
    @PutMapping("/password")
    public Result<Object> updatePassword(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                          @RequestBody Map<String, String> params) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.error("未登录");
            }
            String token = authHeader.substring(7);
            Claims claims = jwtUtils.getClaimsByToken(token);
            if (claims == null) {
                return Result.error("Token无效");
            }
            Integer userId = (Integer) claims.get("userId");
            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");
            userService.updatePassword(userId, oldPassword, newPassword);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
