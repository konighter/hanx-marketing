package com.hzltd.module.erplus.amzadv.service;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.amzadv.dataobject.AmzAdvKeywordDO;
import com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvKeywordPageReqVO;
import com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvKeywordSaveReqVO;

/**
 * 亚马逊广告关键词 Service 接口
 *
 * @author 翰展科技
 */
public interface AmzAdvKeywordService {

    /**
     * 创建关键词
     *
     * @param createReqVO 创建请求参数
     * @return 关键词ID
     */
    Long createAmzAdvKeyword(AmzAdvKeywordSaveReqVO createReqVO);

    /**
     * 更新关键词
     *
     * @param updateReqVO 更新请求参数
     */
    void updateAmzAdvKeyword(AmzAdvKeywordSaveReqVO updateReqVO);

    /**
     * 删除关键词
     *
     * @param id 关键词ID
     */
    void deleteAmzAdvKeyword(Long id);

    /**
     * 获取关键词
     *
     * @param id 关键词ID
     * @return 关键词
     */
    AmzAdvKeywordDO getAmzAdvKeyword(Long id);

    /**
     * 分页查询关键词
     *
     * @param pageReqVO 分页请求参数
     * @return 关键词分页结果
     */
    PageResult<AmzAdvKeywordDO> getAmzAdvKeywordPage(AmzAdvKeywordPageReqVO pageReqVO);

    /**
     * 根据店铺ID和关键词ID查询
     *
     * @param shopId      店铺ID
     * @param keywordId   关键词ID
     * @return 关键词
     */
    AmzAdvKeywordDO getByShopIdAndKeywordId(String shopId, String keywordId);
}