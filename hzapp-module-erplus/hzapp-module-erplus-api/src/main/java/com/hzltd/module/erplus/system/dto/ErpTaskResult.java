package com.hzltd.module.erplus.system.dto;

import com.hzltd.module.erplus.system.enums.ErpTaskResultStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 任务执行结果
 *
 * @author antigravity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.Accessors(chain = true)
public class ErpTaskResult {

    /** 下一个目标状态 */
    private String status;
    
    /** 远程平台/第三方ID (可选) */
    private String platformJobId;
    /**
     * 下次调度时间
     */
    private Long nextScheduleTime;
    
    /** 需更新的上下文 (可选) */
    private Map<String, Object> context;
    
    /** 错误信息 (失败时提供) */
    private String errorMessage;

    // 静态工厂方法
    public static ErpTaskResult success() {
        return ErpTaskResult.builder().status(ErpTaskResultStatusEnum.SUCCESS.getStatus()).build();
    }

    public static ErpTaskResult success(Map<String, Object> context) {
        return ErpTaskResult.builder().status(ErpTaskResultStatusEnum.SUCCESS.getStatus()).context(context).build();
    }

    public static ErpTaskResult failed(String errorMessage) {
        return ErpTaskResult.builder().status(ErpTaskResultStatusEnum.FAILED.getStatus()).errorMessage(errorMessage).build();
    }

    public static ErpTaskResult submitted(String platformJobId) {
        return ErpTaskResult.builder().status(ErpTaskResultStatusEnum.SUBMITTED.getStatus()).platformJobId(platformJobId).build();
    }

    public static ErpTaskResult processing() {
        return ErpTaskResult.builder().status(ErpTaskResultStatusEnum.PROCESSING.getStatus()).build();
    }

    public static ErpTaskResult skipped() {
        return ErpTaskResult.builder().status(ErpTaskResultStatusEnum.SKIPPED.getStatus()).build();
    }
}
