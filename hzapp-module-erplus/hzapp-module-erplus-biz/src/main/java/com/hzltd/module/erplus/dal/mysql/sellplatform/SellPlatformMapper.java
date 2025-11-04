package com.hzltd.module.erplus.dal.mysql.sellplatform;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformPageReqVO;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformReqVO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 销售平台 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface SellPlatformMapper extends BaseMapperX<SellPlatformDO> {

    default PageResult<SellPlatformDO> selectPage(SellPlatformPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SellPlatformDO>()
                .likeIfPresent(SellPlatformDO::getName, reqVO.getName())
                .eqIfPresent(SellPlatformDO::getCode, reqVO.getCode())
                .betweenIfPresent(SellPlatformDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SellPlatformDO::getId));
    }

    default List<SellPlatformDO> selectList(SellPlatformReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<SellPlatformDO>()
                .eqIfPresent(SellPlatformDO::getId, reqVO.getId())
                .likeIfPresent(SellPlatformDO::getName, reqVO.getName())
                .likeIfPresent(SellPlatformDO::getCode, reqVO.getCode())
                .betweenIfPresent(SellPlatformDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SellPlatformDO::getId));
    }

}