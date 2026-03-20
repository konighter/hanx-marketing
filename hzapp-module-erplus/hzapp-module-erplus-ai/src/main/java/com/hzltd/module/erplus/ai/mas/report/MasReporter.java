package com.hzltd.module.erplus.ai.mas.report;

/**
 * MAS 执行事件实时推送接口.
 * <p>
 * 实现类将事件推送给关注该 Session 的客户端（如 WebSocket、钉钉机器人等）。
 * <p>
 * <b>实现约定：</b>
 * <ul>
 *   <li>推送必须是<b>非阻塞的</b>（异步或快速失败）</li>
 *   <li>推送失败只记录日志，<b>不抛出异常</b>，不影响 Orchestrator 执行流程</li>
 * </ul>
 */
public interface MasReporter {

    /**
     * 将执行事件推送给关注该 session 的客户端.
     *
     * @param event MAS 执行事件
     */
    void push(MasEvent event);
}
