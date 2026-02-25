package com.hzltd.module.erplus.ai.core.tool;

/**
 * AI Tool 接口定义
 * 基于 Spring AI 的 Function Calling 机制
 */
public interface AiTool<I, O> {

    /**
     * 获取工具名称
     */
    String getName();

    /**
     * 获取工具描述
     */
    String getDescription();

    /**
     * 执行工具逻辑
     *
     * @param input 输入参数
     * @return 输出结果
     */
    O execute(I input);

}
