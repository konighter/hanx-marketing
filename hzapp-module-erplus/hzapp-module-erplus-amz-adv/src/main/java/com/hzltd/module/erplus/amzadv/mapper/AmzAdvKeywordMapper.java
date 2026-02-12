package com.hzltd.module.erplus.amzadv.mapper;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.amzadv.dataobject.AmzAdvKeywordDO;
import com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvKeywordPageReqVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 亚马逊广告关键词 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface AmzAdvKeywordMapper extends BaseMapperX<AmzAdvKeywordDO> {

    /**
     * 分页查询关键词
     *
     * @param pageReqVO 分页请求参数
     * @return 关键词分页结果
     */
    PageResult<AmzAdvKeywordDO> selectPage(AmzAdvKeywordPageReqVO pageReqVO);

    /**
     * 根据店铺ID和关键词ID查询
     *
     * @param shopId      店铺ID
     * @param keywordId   关键词ID
     * @return 关键词
     */
    AmzAdvKeywordDO selectByShopIdAndKeywordId(String shopId, String keywordId);
}