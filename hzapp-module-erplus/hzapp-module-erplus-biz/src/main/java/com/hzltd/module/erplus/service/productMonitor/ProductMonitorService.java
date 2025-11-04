package com.hzltd.module.erplus.service.productMonitor;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.productMonitor.vo.*;
import com.hzltd.module.erplus.dal.dataobject.productMonitor.ProductMonitorDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 产品监控 Service 接口
 *
 * @author 翰展科技
 */
public interface ProductMonitorService {

    /**
     * 创建产品监控
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createProductMonitor(@Valid ProductMonitorSaveReqVO createReqVO);

    /**
     * 更新产品监控
     *
     * @param updateReqVO 更新信息
     */
    void updateProductMonitor(@Valid ProductMonitorSaveReqVO updateReqVO);

    /**
     * 删除产品监控
     *
     * @param id 编号
     */
    void deleteProductMonitor(Integer id);

    /**
     * 获得产品监控
     *
     * @param id 编号
     * @return 产品监控
     */
    ProductMonitorDO getProductMonitor(Integer id);

    /**
     * 获得产品监控分页
     *
     * @param pageReqVO 分页查询
     * @return 产品监控分页
     */
    PageResult<ProductMonitorDO> getProductMonitorPage(ProductMonitorPageReqVO pageReqVO);

    /**
     * 获得需要运行的产品监控
     * @return
     */
    List<ProductMonitorDO> getProductMonitorToRun(Integer platformId);


    /**
     * 获取指标值
     * @param reqVO
     * @return
     */
    PageResult<ProductMetricsDataRespVO> getProductMetricsData(ProductMetricsDataPageReqVO reqVO);

    /**
     * 获取指标定义列表
     * @return
     */
    List<ProductMetricsRespVO> getProductMetrics(List<String> metircs);

}