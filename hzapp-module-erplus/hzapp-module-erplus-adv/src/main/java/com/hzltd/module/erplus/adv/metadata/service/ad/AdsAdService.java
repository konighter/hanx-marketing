package com.hzltd.module.erplus.adv.metadata.service.ad;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdDO;
import com.hzltd.module.erplus.adv.metadata.vo.ad.AdsAdCreateReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.ad.AdsAdPageReqVO;
import com.hzltd.module.erplus.adv.model.AdsAdModel;

import java.util.Map;

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

    /**
     * 获取广告扩展属性
     * @param id 广告ID
     * @return 属性 Map
     */
    Map<String, Object> getAdAttributes(Long id);

    /**
     * 保存广告（用于同步）
     * @param shopId 店铺ID
     * @param vo 广告数据
     * @return 本地广告ID
     */
    Long saveAd(Long shopId, AdsAdModel vo);

    /**
     * 创建广告（前端调用）
     * @param createReqVO 创建请求
     */
    void createAd(AdsAdCreateReqVO createReqVO);

}
