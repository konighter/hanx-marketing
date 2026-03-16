package com.hzltd.module.erplus.ai.mas.communication;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasEventLogDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasEventLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Service to capture and persist real-time execution events for visualization.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasEventLogService {

    private final MasEventLogMapper eventLogMapper;


    /**
     * Record a memory state snapshot for visual playback.
     */
    public void logStateSnapshot(String sessionId, String loopId, Map<String, Object> state) {
        try {
            String json = JsonUtils.toJsonString(state);
            logEvent(sessionId, loopId, "STATE_SNAPSHOT", "Memory snapshot captured", json);
        } catch (Exception e) {
            log.error("[EventLog] Failed to serialize state snapshot", e);
        }
    }

    /**
     * Record a new execution event.
     */
    public void logEvent(String sessionId, String loopId, String eventType, String description) {
        logEvent(sessionId, loopId, eventType, description, null);
    }

    public void logEvent(String sessionId, String loopId, String eventType, String description, String payload) {
        MasEventLogDO event = MasEventLogDO.builder()
                .sessionId(sessionId)
                .loopId(loopId)
                .eventType(eventType)
                .description(description)
                .payload(payload)
                .build();
        
        eventLogMapper.insert(event);
        log.debug("[EventLog] {} | Session: {} | Loop: {} | {}", eventType, sessionId, loopId, description);
    }
}
