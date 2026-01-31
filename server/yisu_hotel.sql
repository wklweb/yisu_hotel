/*
 Navicat Premium Data Transfer

 Source Server         : Localhost
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : localhost:3306
 Source Schema         : yisu_hotel

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 30/01/2026 20:23:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Create Database
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `yisu_hotel` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `yisu_hotel`;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'MERCHANT' COMMENT '角色: ADMIN-管理员, MERCHANT-商户',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '123456', 'ADMIN', '13800138000', NULL, '2026-01-30 20:00:00');
INSERT INTO `sys_user` VALUES (2, 'merchant', '123456', 'MERCHANT', '13900139000', NULL, '2026-01-30 20:00:00');

-- ----------------------------
-- Table structure for hotel
-- ----------------------------
DROP TABLE IF EXISTS `hotel`;
CREATE TABLE `hotel`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '酒店名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '具体地址',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '城市',
  `star_rating` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '星级',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `facilities` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设施(逗号分隔)',
  `open_date` date NULL DEFAULT NULL COMMENT '开业日期',
  `status` int NULL DEFAULT 0 COMMENT '0:审核中, 1:已发布, 2:已下线, 3:审核不通过',
  `merchant_id` int NOT NULL COMMENT '商户ID',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图片JSON',
  `min_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '最低价格',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `deleted` int NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel
-- ----------------------------
INSERT INTO `hotel` VALUES (1, '易宿北京国际大酒店', '北京市朝阳区建国门外大街1号', '北京', '5星', '位于北京CBD核心地带，交通便利，设施豪华。', 'Wifi,停车场,健身房,游泳池,餐厅,会议室', '2020-01-01', 1, 2, NULL, 899.00, NULL, 0, '2026-01-30 20:00:00');
INSERT INTO `hotel` VALUES (2, '易宿上海度假村', '上海市浦东新区迪士尼大道', '上海', '4星', '毗邻迪士尼乐园，亲子度假首选。', 'Wifi,停车场,游泳池,接送机', '2021-05-01', 1, 2, NULL, 599.00, NULL, 0, '2026-01-30 20:00:00');
INSERT INTO `hotel` VALUES (3, '待审核测试酒店', '测试地址888号', '深圳', '3星', '这是一个测试酒店描述', 'Wifi', '2023-01-01', 0, 2, NULL, 299.00, NULL, 0, '2026-01-30 20:00:00');

-- ----------------------------
-- Table structure for room_type
-- ----------------------------
DROP TABLE IF EXISTS `room_type`;
CREATE TABLE `room_type`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `hotel_id` int NOT NULL COMMENT '所属酒店ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房型名称',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片链接',
  `stock` int NULL DEFAULT 0 COMMENT '库存',
  `deleted` int NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room_type
-- ----------------------------
INSERT INTO `room_type` VALUES (1, 1, '豪华大床房', 899.00, '50平米，超大落地窗', NULL, 10, 0);
INSERT INTO `room_type` VALUES (2, 1, '行政套房', 1599.00, '80平米，包含行政酒廊待遇', NULL, 5, 0);
INSERT INTO `room_type` VALUES (3, 2, '亲子主题房', 699.00, '卡通主题装饰', NULL, 20, 0);
INSERT INTO `room_type` VALUES (4, 2, '标准双床房', 599.00, '舒适双床，性价比高', NULL, 30, 0);

SET FOREIGN_KEY_CHECKS = 1;
