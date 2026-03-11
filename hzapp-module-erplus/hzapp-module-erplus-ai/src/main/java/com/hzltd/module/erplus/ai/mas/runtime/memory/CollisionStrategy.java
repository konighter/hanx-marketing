package com.hzltd.module.erplus.ai.mas.runtime.memory;

/**
 * Strategy for handling context key collisions in GlobalSessionMemory.
 */
public enum CollisionStrategy {
    /**
     * Last write wins. Overwrites existing value.
     */
    OVERWRITE,

    /**
     * Throw an exception if the key already exists.
     */
    STRICT,

    /**
     * If existing and new values are both Maps, merge them. Otherwise, overwrite.
     */
    MERGE
}
