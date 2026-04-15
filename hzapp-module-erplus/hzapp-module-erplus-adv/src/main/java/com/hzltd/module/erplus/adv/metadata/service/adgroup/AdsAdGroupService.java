package com.hzltd.module.erplus.adv.metadata.service.adgroup;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupDO;
import com.hzltd.module.erplus.adv.metadata.vo.adgroup.AdsAdGroupPageReqVO;
import com.hzltd.module.erplus.adv.model.AdsAdGroupModel;

import java.util.Map;

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
     * 获取广告组扩展属性
     * @param id 广告组ID
     * @return 属性 Map
     */
    Map<String, Object> getAdGroupAttributes(Long id);

    /**
     * 保存广告组（用于同步）
     * @param shopId 店铺ID
     * @param vo 广告组数据
     * @return 本地广告组ID
     */
    Long saveAdGroup(Long shopId, AdsAdGroupModel vo);

    /**
     * 根据计划ID和外部ID查询广告组
     * @param campaignId 计划ID
     * @param externalId 外部ID
     * @return 广告组
     */
    AdsAdGroupDO getAdGroupByCampaignAndExternalId(Long campaignId, String externalId);

    /**
     * 根据店铺ID和外部ID查询广告组
     * @param shopId 店铺ID
     * @param externalId 外部ID
     * @return 广告组
     */
    AdsAdGroupDO getAdGroupByShopAndExternalId(Long shopId, String externalId);

}
