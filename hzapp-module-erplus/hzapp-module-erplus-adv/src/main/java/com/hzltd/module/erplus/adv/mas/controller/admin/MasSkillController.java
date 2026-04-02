package com.hzltd.module.erplus.adv.mas.controller.admin;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.mas.controller.admin.vo.ActivateReqVO;
import com.hzltd.module.erplus.adv.mas.controller.admin.vo.MasSkillInstanceMessageVO;
import com.hzltd.module.erplus.adv.mas.controller.admin.vo.MasSkillInstanceVO;
import com.hzltd.module.erplus.adv.mas.controller.admin.vo.MasSkillListVO;
import com.hzltd.module.erplus.adv.mas.dal.dataobject.MasSkillDefDO;
import com.hzltd.module.erplus.adv.mas.service.MasSkillService;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSkillInstanceLogDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 策略技能")
@RestController
@RequestMapping("/erplus/adv/mas/skill")
@Validated
public class MasSkillController {

    @Resource
    private MasSkillService skillService;

    @GetMapping("/list")
    @Operation(summary = "获得策略技能列表")
    public CommonResult<List<MasSkillListVO>> getSkillList() {
        return success(skillService.getAllSkills());
    }

    @GetMapping("/detail")
    @Operation(summary = "获取单个技能详情")
    public CommonResult<MasSkillDefDO> getSkillDetail(@RequestParam("code") String code) {
        return success(skillService.getSkillByCode(code));
    }

    @PostMapping("/activate")
    @Operation(summary = "为 ASIN 激活某策略技能")
    public CommonResult<Long> activateSkill(@RequestBody ActivateReqVO req) {
        return success(skillService.activateSkillForAsin(req.getSkillCode(), req.getTargetBizId(), req.getConfigParams()));
    }

    @GetMapping("/instance/page")
    @Operation(summary = "获得策略技能实例分页")
    public CommonResult<PageResult<MasSkillInstanceVO>> getSkillInstancePage(
            @RequestParam(value = "skillCode", required = false) String skillCode,
            @RequestParam(value = "targetBizId", required = false) String targetBizId,
            @Valid PageParam pageParam) {
        return success(skillService.getSkillInstancePage(skillCode, targetBizId, pageParam));
    }

    @GetMapping("/instance/logs")
    @Operation(summary = "获得策略技能实例日志")
    public CommonResult<List<MasSkillInstanceLogDO>> getSkillInstanceLogs(@RequestParam("id") Long id) {
        return success(skillService.getSkillInstanceLogs(id));
    }

    @GetMapping("/instance/messages")
    @Operation(summary = "获得策略技能实例消息")
    public CommonResult<List<MasSkillInstanceMessageVO>> getSkillInstanceMessages(@RequestParam("processInstanceId") String processInstanceId) {
        return success(skillService.getSkillInstanceMessages(processInstanceId));
    }

}
