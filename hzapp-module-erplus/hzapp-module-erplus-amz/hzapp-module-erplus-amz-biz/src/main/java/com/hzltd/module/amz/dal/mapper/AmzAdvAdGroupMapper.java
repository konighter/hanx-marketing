package com.hzltd.module.amz.dal.mapper;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.amz.adv.controller.admin.vo.AmzAdvAdGroupPageReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvAdGroupDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 亚马逊广告组 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface AmzAdvAdGroupMapper extends BaseMapperX<AmzAdvAdGroupDO> {

    /**
     * 分页查询广告组
     *
     * @param pageReqVO 分页请求参数
     * @return 广告组分页结果
     */
    PageResult<AmzAdvAdGroupDO> selectPage(AmzAdvAdGroupPageReqVO pageReqVO);

    /**
     * 根据店铺ID和广告组ID查询
     *
     * @param shopId      店铺ID
     * @param adGroupId   广告组ID
     * @return 广告组
     */
    AmzAdvAdGroupDO selectByShopIdAndAdGroupId(String shopId, String adGroupId);
}