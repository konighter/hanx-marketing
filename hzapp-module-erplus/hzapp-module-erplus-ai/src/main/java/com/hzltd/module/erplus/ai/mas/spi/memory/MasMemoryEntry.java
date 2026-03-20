package com.hzltd.module.erplus.ai.mas.spi.memory;

import lombok.Builder;
import lombok.Data;

/**
 * Agnostic model for a memory search result.
 */
@Data
@Builder
public class MasMemoryEntry {
    private String content;
    private String author;
    private String timestamp;
    private Double score;
}
