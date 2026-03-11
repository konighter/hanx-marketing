package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.google.adk.tools.BaseTool;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnifiedToolRegistryTest {

    @Component
    static class SampleToolBean {
        @MasTool("Calculate the sum of two numbers")
        public int add(int a, int b) {
            return a + b;
        }

        public void notATool() {}
    }

    @Test
    void testResolveTools_Success() {
        // Arrange
        ApplicationContext mockContext = mock(ApplicationContext.class);
        SampleToolBean bean = new SampleToolBean();
        when(mockContext.getBean("sampleBean")).thenReturn(bean);
        
        UnifiedToolRegistry registry = new UnifiedToolRegistry(mockContext);

        // Act
        List<BaseTool> tools = registry.resolveTools(Collections.singletonList("sampleBean"));

        // Assert
        assertNotNull(tools);
        assertEquals(1, tools.size());
        BaseTool tool = tools.get(0);
        assertEquals("add", tool.name());
        // Note: ADK internal reflection-based description might vary, 
        // but we mainly care that it's correctly wrapped.
    }
}
