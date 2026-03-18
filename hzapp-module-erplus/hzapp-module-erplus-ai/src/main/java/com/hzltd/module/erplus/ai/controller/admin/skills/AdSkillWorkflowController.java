package com.hzltd.module.erplus.ai.controller.admin.skills;

import com.hzltd.module.erplus.ai.workflow.MasWorkflowManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 广告工作流控制器：对外提供触发接口
 */
@Slf4j
@RestController
@RequestMapping("/erplus/ai/workflow")
public class AdSkillWorkflowController {

    @Autowired
    private MasWorkflowManager workflowManager;

    /**
     * 手动部署流程（通常在调试时使用）
     */
    @PostMapping("/deploy")
    public String deploy() {
        workflowManager.deployProcess("Agent-loop.bpmn20.xml");
        return "Deployment triggered.";
    }
}
