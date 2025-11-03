package com.hzltd.module.erplus.dal.dataobject.app;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * 应用注册信息 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_app")
@KeySequence("erplus_app_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 名称
     */
    private String name;
    /**
     * 平台ID
     */
    private Long platformId;
    /**
     * AppId
     */
    private String appId;
    /**
     * 应用Key
     */
    private String appKey;
    /**
     * 应用密钥
     */
    private String appSecret;

    /**
     * 状态
     */
    private Integer status;

}