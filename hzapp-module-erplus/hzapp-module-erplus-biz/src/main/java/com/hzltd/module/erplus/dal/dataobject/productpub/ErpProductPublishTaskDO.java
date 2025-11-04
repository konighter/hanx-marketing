package com.hzltd.module.erplus.dal.dataobject.productpub;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;
import org.joda.time.DateTime;

import java.time.LocalDateTime;

/**
 * 商品发布任务 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_cross_product_publish_task")
@KeySequence("erplus_cross_product_publish_task_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpProductPublishTaskDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 平台ID
     */
    private Integer platformId;

    /**
     * 版本号
     */
    private Long version;
    /**
     * 调度时间(秒)
     */
    private DateTime scheduleTime;
    /**
     * 任务状态
     */
    private Integer status;
    /**
     * 状态信息
     */
    private String statusInfo;
    /**
     * 发布时间
     */
    private LocalDateTime beginTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 任务时长
     */
    private Integer duration;

}