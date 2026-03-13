package com.hzltd.module.erplus.adv.controller.admin.mas;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.ActivateReqVO;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.MasSkillListVO;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasSkillDefDO;
import com.hzltd.module.erplus.adv.service.mas.MasSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Tag(name = "MAS 技能大厅 API")
@RestController
@RequestMapping("/adv/mas-skills")
public class MasSkillController {

    @Autowired
    private MasSkillService masSkillService;

    @GetMapping("/list")
    @Operation(summary = "获取技能大厅列表")
    public CommonResult<List<MasSkillListVO>> listSkills() {
        return CommonResult.success(masSkillService.getAllSkills());
    }

    @GetMapping("/detail")
    @Operation(summary = "获取单个技能详情 (包含参数 Schema)")
    public CommonResult<MasSkillDefDO> getSkillDetail(@RequestParam("code") String code) {
        return CommonResult.success(masSkillService.getSkillByCode(code));
    }

    @PostMapping("/activate")
    @Operation(summary = "为 ASIN 激活某策略技能")
    public CommonResult<Long> activateSkill(@RequestBody ActivateReqVO req) {
        Long taskId = masSkillService.activateSkillForAsin(req.getSkillCode(), req.getTargetBizId(), req.getConfigParams());
        return CommonResult.success(taskId);
    }

}
