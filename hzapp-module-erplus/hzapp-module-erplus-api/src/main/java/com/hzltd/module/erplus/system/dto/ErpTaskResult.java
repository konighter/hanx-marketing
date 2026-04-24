package com.hzltd.module.erplus.system.dto;

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
public class ErpTaskResult {

    /** 下一个目标状态 (SUCCESS, FAILED, SUBMITTED, PROCESSING) */
    private String status;
    
    /** 远程平台/第三方ID (可选) */
    private String platformJobId;
    
    /** 需更新的上下文 (可选) */
    private Map<String, Object> context;
    
    /** 错误信息 (失败时提供) */
    private String errorMessage;

    // 静态工厂方法
    public static ErpTaskResult success() {
        return ErpTaskResult.builder().status("SUCCESS").build();
    }

    public static ErpTaskResult success(Map<String, Object> context) {
        return ErpTaskResult.builder().status("SUCCESS").context(context).build();
    }

    public static ErpTaskResult failed(String errorMessage) {
        return ErpTaskResult.builder().status("FAILED").errorMessage(errorMessage).build();
    }

    public static ErpTaskResult submitted(String platformJobId) {
        return ErpTaskResult.builder().status("SUBMITTED").platformJobId(platformJobId).build();
    }

    public static ErpTaskResult processing() {
        return ErpTaskResult.builder().status("PROCESSING").build();
    }
}
