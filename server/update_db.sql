-- 数据库增量升级脚本（按顺序执行；若字段已存在可跳过对应语句）

-- 1) 图片字段
ALTER TABLE `hotel` ADD COLUMN `cover_image` varchar(255) NULL COMMENT '封面图' AFTER `images`;

-- 2) 多语言与展示信息字段
ALTER TABLE `hotel` ADD COLUMN `name_en` varchar(100) NULL COMMENT '英文名称' AFTER `name`;
ALTER TABLE `hotel` ADD COLUMN `surroundings` text NULL COMMENT '周边环境' AFTER `description`;
ALTER TABLE `hotel` ADD COLUMN `promotion_info` text NULL COMMENT '优惠信息' AFTER `surroundings`;

-- 3) 快捷标签（用于前台筛选）
ALTER TABLE `hotel` ADD COLUMN `tags` varchar(255) NULL COMMENT '快捷标签(逗号分隔)' AFTER `facilities`;
