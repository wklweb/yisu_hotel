package com.yisu.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("hotel")
public class Hotel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String address;
    private String starRating; // 1-5星
    private String description;
    private String facilities; // 逗号分隔，如：Wifi,Parking
    private LocalDate openDate;
    private Integer status; // 0:审核中, 1:已发布, 2:下线, 3:审核不通过
    private Integer merchantId;
    private String images; // JSON or comma separated
    private String coverImage; // 封面图
    private BigDecimal minPrice;
    private String rejectReason; // 审核不通过原因
    private String city; // 城市，用于筛选
}
