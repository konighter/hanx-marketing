package com.hzltd.module.erplus.ai.controller.admin.mas;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理后台 - MAS 会话")
@RestController
@RequestMapping("/erplus/ai/mas/session")
@RequiredArgsConstructor
@Validated
public class MasSessionController {

//    private final MasSessionService sessionService;
//    private final MasPersistenceService persistenceService;
//
//    @PostMapping("/create")
//    @Operation(summary = "创建并启动 MAS 会话")
//    public Mono<CommonResult<String>> createSession(@Valid @RequestBody MasSessionCreateReqVO createReqVO) {
//        return sessionService.createSession(createReqVO.getGoal())
//                .map(CommonResult::success);
//    }
//
//    @GetMapping("/page")
//    @Operation(summary = "获得会话分页")
//    public CommonResult<PageResult<MasSessionRespVO>> getSessionPage(@Valid MasSessionPageReqVO pageVO) {
//        PageResult<MasSessionDO> pageResult = persistenceService.getSessionPage(pageVO);
//        return success(MasConvert.INSTANCE.convertPage(pageResult));
//    }
//
//    @GetMapping("/get")
//    @Operation(summary = "获得会话详情")
//    @Parameter(name = "id", description = "会话 ID", required = true, example = "uuid-123")
//    public CommonResult<MasSessionRespVO> getSession(@RequestParam("id") String id) {
//        MasSessionDO session = persistenceService.getSession(id);
//        return success(MasConvert.INSTANCE.convert(session));
//    }
//
//    @GetMapping("/task-history/list")
//    @Operation(summary = "获得任务执行历史列表")
//    @Parameter(name = "sessionId", description = "会话 ID", required = true, example = "uuid-123")
//    public CommonResult<List<MasTaskHistoryRespVO>> getTaskHistoryList(@RequestParam("sessionId") String sessionId) {
//        List<MasTaskHistoryDO> list = persistenceService.getTaskHistoryList(sessionId);
//        return success(MasConvert.INSTANCE.convertList2(list));
//    }
//
//    @PostMapping("/feedback")
//    @Operation(summary = "发送反馈（模拟）")
//    public CommonResult<Boolean> sendFeedback(@Valid @RequestBody MasFeedbackReqVO feedbackReqVO) {
//        // 目前仅作接口占位，后续可接入 Agent 交互
//        return success(true);
//    }
//
//    @PostMapping("/stop")
//    @Operation(summary = "手动终止 MAS 会话")
//    @Parameter(name = "id", description = "会话 ID", required = true, example = "uuid-123")
//    public Mono<CommonResult<Boolean>> stopSession(@RequestParam("id") String id) {
//        return sessionService.stopSession(id)
//                .then(Mono.just(success(true)));
//    }

}
