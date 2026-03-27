package com.hzltd.module.erplus.adv.metadata.service.keyword;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsKeywordDO;
import com.hzltd.module.adv.model.AdsTargetResponse;
import com.hzltd.module.erplus.adv.metadata.vo.keyword.AdsKeywordPageReqVO;

import java.util.Map;

/**
 * 广告关键词 Service 接口
 */
public interface AdsKeywordService {

    /**
     * 获得广告关键词分页
     *
     * @param pageReqVO 分页查询
     * @return 广告关键词分页
     */
    PageResult<AdsKeywordDO> getKeywordPage(AdsKeywordPageReqVO pageReqVO);

    /**
     * 更新关键词出价
     *
     * @param id 编号
     * @param bid 出价
     */
    void updateKeywordBid(Long id, java.math.BigDecimal bid);

    /**
     * 更新关键词状态
     *
     * @param id 编号
     * @param status 统一状态
     */
    void updateKeywordStatus(Long id, String status);

    /**
     * 获得广告关键词
     *
     * @param id 编号
     * @return 广告关键词
     */
    AdsKeywordDO getKeyword(Long id);

    /**
     * 获取关键词扩展属性
     * @param id 关键词ID
     * @return 属性 Map
     */
    Map<String, Object> getKeywordAttributes(Long id);

    /**
     * 保存关键词（用于同步）
     * @param accountId 账号ID
     * @param vo 关键词数据
     * @return 本地关键词ID
     */
    Long saveKeyword(Long accountId, AdsTargetResponse vo);

}
