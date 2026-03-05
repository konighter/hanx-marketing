package com.hzltd.module.erplus.adv.adapter.amazon.model.sp;

import lombok.Data;
import java.util.List;

/** Amazon SP 否定定向查询响应 */
@Data
public class AdsSpNegativeTargetQueryResp {
    private String nextToken;
    private List<AdsSpNegativeTarget> negativeTargets;
}
