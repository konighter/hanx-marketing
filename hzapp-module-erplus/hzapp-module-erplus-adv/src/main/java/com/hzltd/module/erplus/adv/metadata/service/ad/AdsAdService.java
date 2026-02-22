package com.hzltd.module.erplus.adv.metadata.service.ad;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdDO;
import com.hzltd.module.erplus.adv.metadata.vo.ad.AdsAdPageReqVO;

/**
 * 广告 Service 接口
 */
public interface AdsAdService {

    /**
     * 获得广告分页
     *
     * @param pageReqVO 分页查询
     * @return 广告分页
     */
    PageResult<AdsAdDO> getAdPage(AdsAdPageReqVO pageReqVO);

    /**
     * 更新广告状态
     *
     * @param id 编号
     * @param status 统一状态
     */
    void updateAdStatus(Long id, String status);

    /**
     * 获得广告
     *
     * @param id 编号
     * @return 广告
     */
    AdsAdDO getAd(Long id);

}
