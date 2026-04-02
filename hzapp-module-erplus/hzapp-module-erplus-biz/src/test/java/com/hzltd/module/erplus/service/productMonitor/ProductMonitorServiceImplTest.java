package com.hzltd.module.erplus.service.productMonitor;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.test.core.ut.BaseDbUnitTest;
import com.hzltd.module.erplus.controller.admin.productMonitor.vo.ProductMonitorPageReqVO;
import com.hzltd.module.erplus.controller.admin.productMonitor.vo.ProductMonitorSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.productMonitor.ProductMonitorDO;
import com.hzltd.module.erplus.dal.mysql.productMonitor.ProductMonitorMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.hzltd.framework.common.util.date.LocalDateTimeUtils.buildBetweenTime;
import static com.hzltd.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static com.hzltd.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.hzltd.framework.test.core.util.AssertUtils.assertServiceException;
import static com.hzltd.framework.test.core.util.RandomUtils.randomPojo;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.PRODUCT_MONITOR_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ProductMonitorServiceImpl} 的单元测试类
 *
 * @author 翰展科技
 */
@Import(ProductMonitorServiceImpl.class)
public class ProductMonitorServiceImplTest extends BaseDbUnitTest {

    @Resource
    private ProductMonitorServiceImpl productMonitorService;

    @Resource
    private ProductMonitorMapper productMonitorMapper;

    @Test
    public void testCreateProductMonitor_success() {
        // 准备参数
        ProductMonitorSaveReqVO createReqVO = randomPojo(ProductMonitorSaveReqVO.class).setId(null);

        // 调用
        Integer productMonitorId = productMonitorService.createProductMonitor(createReqVO);
        // 断言
        assertNotNull(productMonitorId);
        // 校验记录的属性是否正确
        ProductMonitorDO productMonitor = productMonitorMapper.selectById(productMonitorId);
        assertPojoEquals(createReqVO, productMonitor, "id");
    }

    @Test
    public void testUpdateProductMonitor_success() {
        // mock 数据
        ProductMonitorDO dbProductMonitor = randomPojo(ProductMonitorDO.class);
        productMonitorMapper.insert(dbProductMonitor);// @Sql: 先插入出一条存在的数据
        // 准备参数
        ProductMonitorSaveReqVO updateReqVO = randomPojo(ProductMonitorSaveReqVO.class, o -> {
            o.setId(dbProductMonitor.getId()); // 设置更新的 ID
        });

        // 调用
        productMonitorService.updateProductMonitor(updateReqVO);
        // 校验是否更新正确
        ProductMonitorDO productMonitor = productMonitorMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, productMonitor);
    }

    @Test
    public void testUpdateProductMonitor_notExists() {
        // 准备参数
        ProductMonitorSaveReqVO updateReqVO = randomPojo(ProductMonitorSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> productMonitorService.updateProductMonitor(updateReqVO), PRODUCT_MONITOR_NOT_EXISTS);
    }

    @Test
    public void testDeleteProductMonitor_success() {
        // mock 数据
        ProductMonitorDO dbProductMonitor = randomPojo(ProductMonitorDO.class);
        productMonitorMapper.insert(dbProductMonitor);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = dbProductMonitor.getId();

        // 调用
        productMonitorService.deleteProductMonitor(id);
       // 校验数据不存在了
       assertNull(productMonitorMapper.selectById(id));
    }

    @Test
    public void testDeleteProductMonitor_notExists() {
        // 准备参数
        Integer id = 0;

        // 调用, 并断言异常
        assertServiceException(() -> productMonitorService.deleteProductMonitor(id), PRODUCT_MONITOR_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetProductMonitorPage() {
       // mock 数据
       ProductMonitorDO dbProductMonitor = randomPojo(ProductMonitorDO.class, o -> { // 等会查询到
           o.setProductId(null);
           o.setPlatformId(null);
           o.setStatus(null);
           o.setCreateTime(null);
       });
       productMonitorMapper.insert(dbProductMonitor);
       // 测试 productId 不匹配
       productMonitorMapper.insert(cloneIgnoreId(dbProductMonitor, o -> o.setProductId(null)));
       // 测试 platformId 不匹配
       productMonitorMapper.insert(cloneIgnoreId(dbProductMonitor, o -> o.setPlatformId(null)));
       // 测试 status 不匹配
       productMonitorMapper.insert(cloneIgnoreId(dbProductMonitor, o -> o.setStatus(null)));
       // 测试 createTime 不匹配
       productMonitorMapper.insert(cloneIgnoreId(dbProductMonitor, o -> o.setCreateTime(null)));
       // 准备参数
       ProductMonitorPageReqVO reqVO = new ProductMonitorPageReqVO();
       reqVO.setProductId(null);
       reqVO.setPlatformId(null);
       reqVO.setStatus(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<ProductMonitorDO> pageResult = productMonitorService.getProductMonitorPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbProductMonitor, pageResult.getList().get(0));
    }

}