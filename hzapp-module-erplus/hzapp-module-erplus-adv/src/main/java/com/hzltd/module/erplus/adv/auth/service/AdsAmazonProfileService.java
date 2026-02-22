package com.hzltd.module.erplus.adv.auth.service;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import java.util.List;

/**
 * 亚马逊广告 Profile Service 接口
 */
public interface AdsAmazonProfileService {

    /**
     * 获得亚马逊广告 Profile 列表
     *
     * @param accountId 账号编号
     * @return Profile 列表
     */
    List<AdsAmazonProfileDO> getAmazonProfileList(Long accountId);

}
