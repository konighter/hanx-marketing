package com.hzltd.module.erplus.ai.controller.admin.mas;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理后台 - MAS 调试与可视化")
@RestController
@RequestMapping("/erplus/ai/mas/debug")
@RequiredArgsConstructor
@Validated
public class MasDebugController {

//    private final MasPersistenceService persistenceService;
//
//    @GetMapping("/event-log/list")
//    @Operation(summary = "获得执行事件日志列表")
//    @Parameter(name = "sessionId", description = "会话 ID", required = true, example = "uuid-123")
//    public CommonResult<List<MasEventLogRespVO>> getEventLogList(@RequestParam("sessionId") String sessionId) {
//        List<MasEventLogDO> list = persistenceService.getEventLogList(sessionId);
//        return success(MasConvert.INSTANCE.convertList3(list));
//    }

}
