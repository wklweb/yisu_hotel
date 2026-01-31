package com.yisu.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("room_type")
public class RoomType {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer hotelId;
    private String name;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Integer stock; // 库存

    // --- 以下字段用于“房型详情”展示（参考需求截图） ---
    private Integer status; // 0:下架 1:上架
    private String tags; // 逗号分隔：山景,智能马桶...
    private String areaRange; // 35-40㎡
    private String floorRange; // 6-8层
    private Integer wifiFree; // 0/1
    private Integer windowFlag; // 0/1 有窗
    private Integer noSmoking; // 0/1 禁烟
    private Integer bedCount; // 1
    private String bedType; // 特大床/双床...
    private String bedSize; // 1.81米
    private Integer extraBedAllowed; // 0/1 可加床
    private Integer breakfastCount; // 2
    private String breakfastType; // 自助餐
    private String breakfastDishes; // 中式,清真餐,素食
    private String breakfastTime; // 07:30-09:30
    private BigDecimal breakfastExtraPrice; // 成人加价（元/人）
    private String memberBenefits; // JSON/文本
    private String cancelPolicy; // JSON/文本
}
