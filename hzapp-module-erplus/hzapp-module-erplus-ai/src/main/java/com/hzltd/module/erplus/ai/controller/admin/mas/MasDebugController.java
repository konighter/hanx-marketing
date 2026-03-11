package com.hzltd.module.erplus.ai.controller.admin.mas;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.ai.controller.admin.mas.vo.MasEventLogRespVO;
import com.hzltd.module.erplus.ai.convert.mas.MasConvert;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasEventLogDO;
import com.hzltd.module.erplus.ai.service.mas.MasPersistenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MAS 调试与可视化")
@RestController
@RequestMapping("/erplus/ai/mas/debug")
@RequiredArgsConstructor
@Validated
public class MasDebugController {

    private final MasPersistenceService persistenceService;

    @GetMapping("/event-log/list")
    @Operation(summary = "获得执行事件日志列表")
    @Parameter(name = "sessionId", description = "会话 ID", required = true, example = "uuid-123")
    public CommonResult<List<MasEventLogRespVO>> getEventLogList(@RequestParam("sessionId") String sessionId) {
        List<MasEventLogDO> list = persistenceService.getEventLogList(sessionId);
        return success(MasConvert.INSTANCE.convertList3(list));
    }

}
