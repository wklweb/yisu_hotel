package com.yisu.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hotel_favorite")
public class HotelFavorite {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer hotelId;
    private Integer userId;
    private LocalDateTime createTime;
}
