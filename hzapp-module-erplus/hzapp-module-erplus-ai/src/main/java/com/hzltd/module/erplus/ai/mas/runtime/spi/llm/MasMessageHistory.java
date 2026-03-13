package com.hzltd.module.erplus.ai.mas.runtime.spi.llm;

import java.util.List;

public interface MasMessageHistory {
    
    void addMessage(String sessionId, MasChatMessage message);
    
    List<MasChatMessage> getMessages(String sessionId);
    
    void clear(String sessionId);
}
