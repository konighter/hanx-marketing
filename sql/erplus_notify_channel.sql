-- 通知渠道配置表
CREATE TABLE `erplus_notify_channel` (
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `name`         VARCHAR(64)  NOT NULL COMMENT '渠道名称',
    `channel_type` TINYINT      NOT NULL COMMENT '渠道类型 1-飞书 2-钉钉 3-企微',
    `webhook_url`  VARCHAR(512) NOT NULL COMMENT 'Webhook 地址',
    `config`       VARCHAR(1024)         COMMENT '扩展配置 JSON (如签名密钥等)',
    `status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态 0-启用 1-禁用',
    `creator`      VARCHAR(64)           DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      VARCHAR(64)           DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`    BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知渠道配置';
