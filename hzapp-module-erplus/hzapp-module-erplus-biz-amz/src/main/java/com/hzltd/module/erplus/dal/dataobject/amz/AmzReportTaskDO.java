package com.hzltd.module.erplus.dal.dataobject.amz;

import lombok.*;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * 亚马逊报告任务 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_amz_report_task")
@KeySequence("erplus_amz_report_task_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmzReportTaskDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 报告ID
     */
    private String reportId;

    /**
     * 报告类型
     */
    private String reportType;
    /**
     * 查询条件
     */
    private String query;
    /**
     * 查询状态
     */
    private Integer status;
    /**
     * 报告结果
     */
    private String reportResult;
    /**
     * 上次检索时间
     */
    private LocalDateTime lastCheckTime;
    /**
     * 结果是否已归档
     */
    private Integer isArchive;


    public enum StatusEnum {
        /**
         * 初始状态
         */
        INIT,
        /**
         * 处理中
         */
        PROCESSING,
        /**
         * 已完成
         */
        COMPLETED,
        /**
         * 已归档
         */
        ARCHIVED,
    }
}