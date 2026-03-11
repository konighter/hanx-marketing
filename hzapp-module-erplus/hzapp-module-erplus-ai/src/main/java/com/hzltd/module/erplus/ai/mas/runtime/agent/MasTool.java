package com.hzltd.module.erplus.ai.mas.runtime.agent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a Spring Bean method as a MAS Tool accessible by ADK Agents.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MasTool {
    /**
     * Description of the tool, used by the LLM to understand when to invoke it.
     */
    String value();
}
