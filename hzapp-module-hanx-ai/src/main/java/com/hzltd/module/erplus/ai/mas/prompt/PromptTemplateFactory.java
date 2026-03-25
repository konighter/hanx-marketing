package com.hzltd.module.erplus.ai.mas.prompt;

import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Map;
import java.util.Properties;

/**
 * A factory for rendering dynamic prompt templates using a placeholder syntax (e.g., ${key}).
 */
@Component
public class PromptTemplateFactory {
    
    private final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");

    /**
     * Renders a given template string by replacing all ${key} occurrences with corresponding
     * values from the variables map.
     * 
     * @param template  The original string template.
     * @param variables Map of variables to insert into the template.
     * @return Rendered string.
     */
    public String render(String template, Map<String, String> variables) {
        if (template == null || variables == null || variables.isEmpty()) {
            return template;
        }
        
        Properties props = new Properties();
        props.putAll(variables);
        return helper.replacePlaceholders(template, props);
    }
}
