package com.yisu.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hotel_review")
public class HotelReview {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer hotelId;
    private Integer userId;
    private Integer rating; // 1-5分
    private String content; // 点评内容
    private String images; // JSON数组，点评图片
    private LocalDateTime createTime;
}
