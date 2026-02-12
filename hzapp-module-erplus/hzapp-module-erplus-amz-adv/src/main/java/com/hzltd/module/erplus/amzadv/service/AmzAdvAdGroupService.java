package com.hzltd.module.erplus.amzadv.service;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.amzadv.dataobject.AmzAdvAdGroupDO;
import com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvAdGroupPageReqVO;
import com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvAdGroupSaveReqVO;

/**
 * 亚马逊广告组 Service 接口
 *
 * @author 翰展科技
 */
public interface AmzAdvAdGroupService {

    /**
     * 创建广告组
     *
     * @param createReqVO 创建请求参数
     * @return 广告组ID
     */
    Long createAmzAdvAdGroup(AmzAdvAdGroupSaveReqVO createReqVO);

    /**
     * 更新广告组
     *
     * @param updateReqVO 更新请求参数
     */
    void updateAmzAdvAdGroup(AmzAdvAdGroupSaveReqVO updateReqVO);

    /**
     * 删除广告组
     *
     * @param id 广告组ID
     */
    void deleteAmzAdvAdGroup(Long id);

    /**
     * 获取广告组
     *
     * @param id 广告组ID
     * @return 广告组
     */
    AmzAdvAdGroupDO getAmzAdvAdGroup(Long id);

    /**
     * 分页查询广告组
     *
     * @param pageReqVO 分页请求参数
     * @return 广告组分页结果
     */
    PageResult<AmzAdvAdGroupDO> getAmzAdvAdGroupPage(AmzAdvAdGroupPageReqVO pageReqVO);

    /**
     * 根据店铺ID和广告组ID查询
     *
     * @param shopId      店铺ID
     * @param adGroupId   广告组ID
     * @return 广告组
     */
    AmzAdvAdGroupDO getByShopIdAndAdGroupId(String shopId, String adGroupId);
}