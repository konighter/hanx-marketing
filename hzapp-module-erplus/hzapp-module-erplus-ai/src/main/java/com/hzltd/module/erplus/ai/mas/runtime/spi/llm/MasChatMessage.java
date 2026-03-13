package com.hzltd.module.erplus.ai.mas.runtime.spi.llm;

public interface MasChatMessage {
    enum Role {
        SYSTEM, USER, AI, TOOL
    }

    Role getRole();

    String getContent();
}
