package com.hzltd.module.erplus.ai.masv0.framework.state;

import reactor.core.publisher.Mono;

/**
 * MAS 状态机接口
 */
public interface MasStateMachine {

    /**
     * 获取当前状态
     */
    MasState getCurrentState();

    /**
     * 尝试转换状态
     * @param nextState 目标状态
     * @return 转换后的状态，如果转换不合法可能保持原状或返回错误
     */
    Mono<MasState> transitTo(MasState nextState);

    /**
     * 监听状态变更
     */
    void onStateChanged(StateChangeListener listener);

    interface StateChangeListener {
        void onChanged(MasState from, MasState to);
    }
}
