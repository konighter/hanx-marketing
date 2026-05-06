package com.hzltd.module.erplus.adv.automation.domain;

import com.hzltd.module.erplus.adv.enums.automation.AdsActionTypeEnum;
import lombok.Data;
import java.util.Map;

/**
 * 执行动作定义
 */
@Data
public class AdsAction {
    private AdsActionTypeEnum type;
    private Map<String, Object> params;
}
