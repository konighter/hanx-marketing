-- 平台授权表 erplus_platform_auth
CREATE TABLE `erplus_platform_auth` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `platform` VARCHAR(20) NOT NULL COMMENT '平台类型: AMAZON, TIKTOK',
    `user_id` BIGINT COMMENT '员工ID',
    `auth_type` VARCHAR(20) NOT NULL COMMENT '授权类型: AMAZON_SP, AMAZON_ADV, TTS_SHOP',
    `auth_scope` VARCHAR(20) NOT NULL COMMENT '授权范围',
    `region` VARCHAR(10) NOT NULL COMMENT '区域代码: NA(北美), EU(欧洲), FE(远东), GLOBAL(TTS全球)',
    `seller_id` VARCHAR(50) NOT NULL COMMENT '卖家后台的唯一身份标识',
    `refresh_token` TEXT NOT NULL COMMENT '用于刷新access_token的长效令牌',
    `access_token` TEXT COMMENT '当前有效的访问令牌',
    `expiry_time` DATETIME COMMENT 'access_token的过期时间',
    `app_id` BIGINT NOT NULL COMMENT '平台应用ID',
    `is_default` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否默认授权',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台授权表';

-- Shop表变更
ALTER TABLE `erplus_platform_shop`
    ADD COLUMN `marketplace_id` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '站点ID: 如ATVPDKIKX0DER(US)',
    ADD COLUMN `country_code` CHAR(2) NOT NULL DEFAULT '' COMMENT '国家代码: US, GB, JP, MY',
    ADD COLUMN `currency` CHAR(3) NOT NULL DEFAULT '' COMMENT '币种',
    ADD COLUMN `seller_id` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '卖家后台的唯一身份标识',
    ADD COLUMN `auth_info` JSON COMMENT '授权信息',
    ADD COLUMN `auth_start_time` DATETIME COMMENT '授权开始时间',
    ADD COLUMN `auth_exp_time` DATETIME COMMENT '授权失效时间',
    ADD COLUMN `account_id` BIGINT COMMENT '平台账号ID',
    ADD COLUMN `status` TINYINT NOT NULL DEFAULT '1' COMMENT '状态';

-- 增加 erplus_shop_auth 表: 关联 platform_shop 和 platform_auth 表
CREATE TABLE `erplus_shop_auth` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `shop_id` INT NOT NULL COMMENT '店铺ID',
    `auth_id` BIGINT NOT NULL COMMENT '授权ID',
    `is_default` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否默认授权',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_shop_id` (`shop_id`),
    KEY `idx_auth_id` (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺授权关联表';

-- 平台应用表 erplus_platform_app
CREATE TABLE `erplus_platform_app` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '应用名称',
    `platform` VARCHAR(20) NOT NULL COMMENT '平台类型: AMAZON, TIKTOK',
    `app_key` VARCHAR(100) NOT NULL COMMENT '应用Key',
    `app_secret` VARCHAR(100) NOT NULL COMMENT '应用密钥',
    `callback_url` VARCHAR(1000) COMMENT '回调地址',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台应用表';

-- 平台账号表 erplus_platform_account
CREATE TABLE `erplus_platform_account` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '账号名称',
    `platform` VARCHAR(20) NOT NULL COMMENT '平台类型: AMAZON, TIKTOK',
    `business_type` VARCHAR(50) COMMENT '业务类型',
    `registration_number` VARCHAR(100) COMMENT '注册号/统一社会信用代码',
    `contact_name` VARCHAR(50) COMMENT '联系人姓名',
    `contact_phone` VARCHAR(20) COMMENT '联系人电话',
    `contact_email` VARCHAR(100) COMMENT '联系人邮箱',
    `address` VARCHAR(1500) COMMENT '地址',
    `remark` VARCHAR(500) COMMENT '备注',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台账号表';


