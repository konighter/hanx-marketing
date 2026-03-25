package com.hzltd.module.amz.api.adv.model;

import com.hzltd.module.amz.api.enums.AmazonRegionEnum;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvRequest<T> {

    private Map<String, String> headers;

    private AdsAccountCredentialDO credential;

    private String accountId;

    private String profileId;

    private AmazonRegionEnum region;


    private T data;

}
