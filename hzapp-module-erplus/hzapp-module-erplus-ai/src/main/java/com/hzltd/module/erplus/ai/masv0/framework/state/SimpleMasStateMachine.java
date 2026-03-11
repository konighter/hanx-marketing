package com.hzltd.module.erplus.ai.masv0.framework.state;

import com.hzltd.module.erplus.ai.masv0.framework.event.MasEventBus;
import com.hzltd.module.erplus.ai.masv0.framework.event.StateChangedEvent;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 基于内存的轻量级状态机实现
 */
public class SimpleMasStateMachine implements MasStateMachine {

    private final String sessionId;
    private final AtomicReference<MasState> state = new AtomicReference<>(MasState.INIT);
    private final List<StateChangeListener> listeners = new ArrayList<>();
    private final MasEventBus eventBus;

    public SimpleMasStateMachine(String sessionId, MasEventBus eventBus) {
        this.sessionId = sessionId;
        this.eventBus = eventBus;
    }

    @Override
    public MasState getCurrentState() {
        return state.get();
    }

    @Override
    public Mono<MasState> transitTo(MasState nextState) {
        return Mono.fromCallable(() -> {
            MasState from = state.get();
            if (isValidTransition(from, nextState)) {
                if (state.compareAndSet(from, nextState)) {
                    notifyListeners(from, nextState);
                    eventBus.publish(new StateChangedEvent(sessionId, from, nextState));
                }
            }
            return state.get();
        });
    }

    /**
     * 校验状态转换合法性 (按需扩展规则)
     */
    private boolean isValidTransition(MasState from, MasState to) {
        if (from == to) return false;
        // 允许任意状态切换至失败
        if (to == MasState.FAILED) return true;
        
        return switch (from) {
            case INIT -> to == MasState.PLANNING;
            case PLANNING -> to == MasState.EXECUTING || to == MasState.WAITING_FOR_USER;
            case EXECUTING -> to == MasState.REVIEWING || to == MasState.WAITING_FOR_USER;
            case REVIEWING -> to == MasState.COMPLETED || to == MasState.EXECUTING || to == MasState.PLANNING;
            case WAITING_FOR_USER -> to == MasState.PLANNING || to == MasState.EXECUTING;
            case COMPLETED, FAILED -> false;
        };
    }

    @Override
    public void onStateChanged(StateChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    private void notifyListeners(MasState from, MasState to) {
        synchronized (listeners) {
            listeners.forEach(l -> l.onChanged(from, to));
        }
    }
}
