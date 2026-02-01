package com.yisu.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String role; // ADMIN, MERCHANT
    private Integer status; // 0:正常, 1:禁用
    private String phone;
    private String email;
    private String avatar;
    private LocalDateTime createTime;
}
