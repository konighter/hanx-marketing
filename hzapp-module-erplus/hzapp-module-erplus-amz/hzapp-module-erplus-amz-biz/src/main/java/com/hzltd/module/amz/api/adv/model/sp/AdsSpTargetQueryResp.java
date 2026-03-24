package com.hzltd.module.amz.api.adv.model.sp;

import lombok.Data;

import java.util.List;

/** Amazon SP Target 查询响应 */
@Data
public class AdsSpTargetQueryResp {
    private String nextToken;
    private List<AdsSpTarget> targets;
}
