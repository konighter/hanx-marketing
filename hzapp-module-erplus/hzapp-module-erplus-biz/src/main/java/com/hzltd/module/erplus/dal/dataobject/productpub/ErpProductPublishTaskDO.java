package com.hzltd.module.erplus.dal.dataobject.productpub;

import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 商品发布任务 DO (刊登流水表)
 *
 * @author 翰展科技
 */
@TableName("erplus_product_listing_task") // 修正为数据库中的实际表名
@KeySequence("erplus_product_listing_task_seq")
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
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联刊登状态表 ID
     */
    private Long listingId;

    /**
     * 预约发布时间
     */
    private LocalDateTime scheduleTime;

    /**
     * 版本号
     */
    private Long version;
    
    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 操作类型
     * @link {OperationModeEnum}
     */
    private Integer operation;
    
    /**
     * 平台原始反馈JSON
     */
    private String rawFeedback;
    
    /**
     * 错误简述/状态信息
     */
    @TableField("status_info")
    private String statusInfo;
    
    /**
     * 刊登原始数据 JSON (暂存)
     */
    private String listingData;

    /**
     * 简要错误预览 (用于列表展示)
     */
    private String brief;
    
    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 发布开始时间
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