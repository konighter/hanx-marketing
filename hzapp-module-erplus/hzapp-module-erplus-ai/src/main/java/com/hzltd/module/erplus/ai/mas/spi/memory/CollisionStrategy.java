package com.hzltd.module.erplus.ai.mas.spi.memory;

/**
 * Strategy for handling context key collisions in MasSessionMemory.
 */
public enum CollisionStrategy {
    /**
     * Last write wins. Overwrites existing value.
     */
    OVERWRITE,

    /**
     * If existing and new values are both Maps, merge them. Otherwise, overwrite.
     */
    MERGE,

    /**
     * Ignore the write if the key already exists.
     */
    IGNORE,

    /**
     * Throw an exception if the key already exists.
     */
    ERROR
}
