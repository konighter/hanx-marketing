package com.hzltd.module.erplus.ai.core.tool;

import lombok.Getter;

import java.util.function.Function;

/**
 * AI Tool 基础实现类
 */
public abstract class BaseAiTool<I, O> implements AiTool<I, O>, Function<I, O> {

    @Getter
    protected final String name;

    @Getter
    protected final String description;

    protected BaseAiTool(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public O apply(I input) {
        return execute(input);
    }

}
