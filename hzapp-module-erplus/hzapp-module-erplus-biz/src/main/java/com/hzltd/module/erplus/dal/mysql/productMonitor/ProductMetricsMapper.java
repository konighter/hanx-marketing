package com.hzltd.module.erplus.dal.mysql.productMonitor;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.controller.admin.productMonitor.vo.ProductMetricsDataPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.productMonitor.ProductMetricsDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品监控指标 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ProductMetricsMapper extends BaseMapperX<ProductMetricsDO> {

    default PageResult<ProductMetricsDO> selectPage(ProductMetricsDataPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProductMetricsDO>()
                .eqIfPresent(ProductMetricsDO::getMonitorId, reqVO.getMonitorId())
                .eqIfPresent(ProductMetricsDO::getProductId, reqVO.getProductId())
                .betweenIfPresent(ProductMetricsDO::getDatekey, reqVO.getDatekey())
                .orderByAsc(ProductMetricsDO::getDatekey));
    }

}