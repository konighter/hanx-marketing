package com.hzltd.module.erplus.service.productMonitor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hzltd.framework.common.enums.CommonStatusEnum;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.productMonitor.vo.*;
import com.hzltd.module.erplus.dal.dataobject.productMonitor.ProductMetricsDO;
import com.hzltd.module.erplus.dal.dataobject.productMonitor.ProductMonitorDO;
import com.hzltd.module.erplus.dal.mysql.productMonitor.ProductMetricsMapper;
import com.hzltd.module.erplus.dal.mysql.productMonitor.ProductMonitorMapper;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.PRODUCT_MONITOR_NOT_EXISTS;

/**
 * 产品监控 Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ProductMonitorServiceImpl implements ProductMonitorService {

    @Resource
    private ProductMonitorMapper productMonitorMapper;

    @Resource
    private ProductMetricsMapper productMetricsMapper;

    @Override
    public Integer createProductMonitor(ProductMonitorSaveReqVO createReqVO) {
        // 插入
        ProductMonitorDO productMonitor = BeanUtils.toBean(createReqVO, ProductMonitorDO.class);

        this.setNextCrawlTime(productMonitor);


        productMonitorMapper.insert(productMonitor);
        // 返回
        return productMonitor.getId();
    }

    /**
     * 设置下一次采集时间
     *
     * @param productMonitor
     */
    private void setNextCrawlTime(ProductMonitorDO productMonitor) {
        if (StringUtils.isEmpty(productMonitor.getCroneType())) {
            productMonitor.setCroneType("Day");
        }
        if (productMonitor.getCroneType().equals("Day")) {
            productMonitor.setNextCrawlTime(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime() + RandomUtils.nextInt(0, 24 * 60 * 60 * 1000));
        }
    }

    @Override
    public void updateProductMonitor(ProductMonitorSaveReqVO updateReqVO) {
        // 校验存在
        validateProductMonitorExists(updateReqVO.getId());
        // 更新
        ProductMonitorDO updateObj = BeanUtils.toBean(updateReqVO, ProductMonitorDO.class);
        productMonitorMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductMonitor(Integer id) {
        // 校验存在
        validateProductMonitorExists(id);
        // 删除
        productMonitorMapper.deleteById(id);
    }

    private void validateProductMonitorExists(Integer id) {
        if (productMonitorMapper.selectById(id) == null) {
            throw exception(PRODUCT_MONITOR_NOT_EXISTS);
        }
    }

    @Override
    public ProductMonitorDO getProductMonitor(Integer id) {
        return productMonitorMapper.selectById(id);
    }

    @Override
    public PageResult<ProductMonitorDO> getProductMonitorPage(ProductMonitorPageReqVO pageReqVO) {
        return productMonitorMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ProductMonitorDO> getProductMonitorToRun(Integer platformId) {
        return productMonitorMapper.selectList(new LambdaQueryWrapperX<ProductMonitorDO>()
                .eq(ProductMonitorDO::getStatus, CommonStatusEnum.ENABLE)
                .eq(ProductMonitorDO::getPlatformId, platformId)
                .and(wrapper -> {
                    wrapper.isNull(ProductMonitorDO::getLastCrawlTime)
                            .or()
                            .le(ProductMonitorDO::getNextCrawlTime, System.currentTimeMillis());
                })
        );
    }

    @Override
    public PageResult<ProductMetricsDataRespVO> getProductMetricsData(ProductMetricsDataPageReqVO reqVO) {

        PageResult<ProductMetricsDO> productMetricsPage = productMetricsMapper.selectPage(reqVO);

        if (CollectionUtils.isEmpty(productMetricsPage.getList())) {
            return new PageResult<>(Collections.emptyList(), productMetricsPage.getTotal());
        }

        List<ProductMetricsDataRespVO> productMetricsDataRespVOS = productMetricsPage.getList().stream()
                .map(item -> BeanUtils.toBean(item, ProductMetricsDataRespVO.class, (resp) -> {
                    resp.setMetricsData(JsonUtils.parseObject(item.getMetircs(), new TypeReference<Map<String, BigDecimal>>() {
                    }));
                }))
                .collect(Collectors.toList());


        return new PageResult<>(productMetricsDataRespVOS, productMetricsPage.getTotal());
    }

    @Override
    public List<ProductMetricsRespVO> getProductMetrics(List<String> metircs) {
        return METRICS_LIST.stream().filter(item -> CollectionUtils.isEmpty(metircs) || metircs.contains(item.getField())).collect(Collectors.toList());
    }


    private static final UnmodifiableList<ProductMetricsRespVO> METRICS_LIST;

    static {
        // ["sales", "visitors", "conversionRate", "stock", "price"]
        List<ProductMetricsRespVO> list = new ArrayList<>();
        list.add(new ProductMetricsRespVO("销量", "sales"));
        list.add(new ProductMetricsRespVO("转化率", "conversionRate"));
        list.add(new ProductMetricsRespVO("访问量", "visitors"));
        list.add(new ProductMetricsRespVO("价格", "price"));
        list.add(new ProductMetricsRespVO("库存", "stock"));

        METRICS_LIST = new UnmodifiableList<>(list);
    }


}