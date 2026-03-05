package com.hzltd.module.erplus.adv.metadata.service.keyword;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsKeywordDO;
import com.hzltd.module.erplus.adv.metadata.vo.keyword.AdsKeywordPageReqVO;

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
     * 更新广告关键词状态
     *
     * @param id 编号
     * @param status 统一状态
     */
    void updateKeywordStatus(Long id, String status);

    AdsKeywordDO getKeyword(Long id);

    /**
     * 更新关键词出价
     *
     * @param id 编号
     * @param bid 出价
     */
    void updateKeywordBid(Long id, java.math.BigDecimal bid);

}
