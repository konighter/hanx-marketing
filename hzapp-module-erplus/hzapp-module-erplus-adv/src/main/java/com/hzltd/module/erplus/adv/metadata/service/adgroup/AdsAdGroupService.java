package com.hzltd.module.erplus.adv.metadata.service.adgroup;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupDO;
import com.hzltd.module.erplus.adv.metadata.vo.adgroup.AdsAdGroupPageReqVO;

/**
 * 广告组 Service 接口
 */
public interface AdsAdGroupService {

    /**
     * 获得广告组分页
     *
     * @param pageReqVO 分页查询
     * @return 广告组分页
     */
    PageResult<AdsAdGroupDO> getAdGroupPage(AdsAdGroupPageReqVO pageReqVO);

    /**
     * 更新广告组状态
     *
     * @param id 编号
     * @param status 统一状态
     */
    void updateAdGroupStatus(Long id, String status);

    /**
     * 获得广告组
     *
     * @param id 编号
     * @return 广告组
     */
    AdsAdGroupDO getAdGroup(Long id);

    /**
     * 根据账户 ID 获取平台信息
     * @param accountId 账户ID
     * @return 平台
     */
    String getPlatformByAccountId(Long accountId);

}
