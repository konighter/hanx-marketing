package com.hzltd.module.erplus.dal.mysql.shop;

import java.util.*;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.shop.ShopDO;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopPageReqVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopReqVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 店铺信息 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface ShopMapper extends BaseMapperX<ShopDO> {

    default PageResult<ShopDO> selectPage(ShopPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ShopDO>()
                .likeIfPresent(ShopDO::getName, reqVO.getName())
                .eqIfPresent(ShopDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(ShopDO::getRegion, reqVO.getRegion())
                .eqIfPresent(ShopDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ShopDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ShopDO::getId));
    }

    default List<ShopDO> selectList(ShopReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ShopDO>()
                .likeIfPresent(ShopDO::getName, reqVO.getName())
                .eqIfPresent(ShopDO::getId, reqVO.getId())
                .eqIfPresent(ShopDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(ShopDO::getRegion, reqVO.getRegion())
                .eqIfPresent(ShopDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ShopDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ShopDO::getId));
    }

}