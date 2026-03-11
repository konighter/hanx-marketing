package com.hzltd.module.erplus.ai.masv0.framework.event;

import com.hzltd.module.erplus.ai.masv0.framework.agent.MasRole;
import lombok.Getter;
import java.util.List;

/**
 * 管理者初始化完成，触发规划
 */
@Getter
public class MasInitialEvent extends MasEvent.BaseEvent {
    
    private final String goal;
    private final List<MasRole> selectedRoles;
    private final String workflow;

    public MasInitialEvent(String sessionId, String goal, List<MasRole> selectedRoles, String workflow) {
        super(sessionId);
        this.goal = goal;
        this.selectedRoles = selectedRoles;
        this.workflow = workflow;
    }
}
