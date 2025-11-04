package com.hzltd.module.erplus.dal.mysql.productMonitor;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.productMonitor.vo.ProductMonitorPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.productMonitor.ProductMonitorDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品监控 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ProductMonitorMapper extends BaseMapperX<ProductMonitorDO> {

    default PageResult<ProductMonitorDO> selectPage(ProductMonitorPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProductMonitorDO>()
                .eqIfPresent(ProductMonitorDO::getProductId, reqVO.getProductId())
                .eqIfPresent(ProductMonitorDO::getPlatformId, reqVO.getPlatformId())
                .eqIfPresent(ProductMonitorDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ProductMonitorDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProductMonitorDO::getId));
    }

}