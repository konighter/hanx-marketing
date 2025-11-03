package com.hzltd.module.erplus.service.productpotentialcompetitive;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.annotation.Resource;

import com.hzltd.framework.test.core.ut.BaseDbUnitTest;

import com.hzltd.module.erplus.controller.admin.productpotentialcompetitive.vo.*;
import com.hzltd.module.erplus.dal.dataobject.productpotentialcompetitive.ProductPotentialCompetitiveDO;
import com.hzltd.module.erplus.dal.mysql.productpotentialcompetitive.ProductPotentialCompetitiveMapper;
import com.hzltd.framework.common.pojo.PageResult;

import javax.annotation.Resource;
import org.springframework.context.annotation.Import;
import java.util.*;
import java.time.LocalDateTime;

import static cn.hutool.core.util.RandomUtil.*;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.*;
import static com.hzltd.framework.test.core.util.AssertUtils.*;
import static com.hzltd.framework.test.core.util.RandomUtils.*;
import static com.hzltd.framework.common.util.date.LocalDateTimeUtils.*;
import static com.hzltd.framework.common.util.object.ObjectUtils.*;
import static com.hzltd.framework.common.util.date.DateUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link ProductPotentialCompetitiveServiceImpl} 的单元测试类
 *
 * @author 翰展科技
 */
@Import(ProductPotentialCompetitiveServiceImpl.class)
public class ProductPotentialCompetitiveServiceImplTest extends BaseDbUnitTest {

    @Resource
    private ProductPotentialCompetitiveServiceImpl productPotentialCompetitiveService;

    @Resource
    private ProductPotentialCompetitiveMapper productPotentialCompetitiveMapper;

    @Test
    public void testCreateProductPotentialCompetitive_success() {
        // 准备参数
        ProductPotentialCompetitiveSaveReqVO createReqVO = randomPojo(ProductPotentialCompetitiveSaveReqVO.class).setId(null);

        // 调用
        Integer productPotentialCompetitiveId = productPotentialCompetitiveService.createProductPotentialCompetitive(createReqVO);
        // 断言
        assertNotNull(productPotentialCompetitiveId);
        // 校验记录的属性是否正确
        ProductPotentialCompetitiveDO productPotentialCompetitive = productPotentialCompetitiveMapper.selectById(productPotentialCompetitiveId);
        assertPojoEquals(createReqVO, productPotentialCompetitive, "id");
    }

    @Test
    public void testUpdateProductPotentialCompetitive_success() {
        // mock 数据
        ProductPotentialCompetitiveDO dbProductPotentialCompetitive = randomPojo(ProductPotentialCompetitiveDO.class);
        productPotentialCompetitiveMapper.insert(dbProductPotentialCompetitive);// @Sql: 先插入出一条存在的数据
        // 准备参数
        ProductPotentialCompetitiveSaveReqVO updateReqVO = randomPojo(ProductPotentialCompetitiveSaveReqVO.class, o -> {
            o.setId(dbProductPotentialCompetitive.getId()); // 设置更新的 ID
        });

        // 调用
        productPotentialCompetitiveService.updateProductPotentialCompetitive(updateReqVO);
        // 校验是否更新正确
        ProductPotentialCompetitiveDO productPotentialCompetitive = productPotentialCompetitiveMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, productPotentialCompetitive);
    }

    @Test
    public void testUpdateProductPotentialCompetitive_notExists() {
        // 准备参数
        ProductPotentialCompetitiveSaveReqVO updateReqVO = randomPojo(ProductPotentialCompetitiveSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> productPotentialCompetitiveService.updateProductPotentialCompetitive(updateReqVO), PRODUCT_POTENTIAL_COMPETITIVE_NOT_EXISTS);
    }

    @Test
    public void testDeleteProductPotentialCompetitive_success() {
        // mock 数据
        ProductPotentialCompetitiveDO dbProductPotentialCompetitive = randomPojo(ProductPotentialCompetitiveDO.class);
        productPotentialCompetitiveMapper.insert(dbProductPotentialCompetitive);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = dbProductPotentialCompetitive.getId();

        // 调用
        productPotentialCompetitiveService.deleteProductPotentialCompetitive(id);
       // 校验数据不存在了
       assertNull(productPotentialCompetitiveMapper.selectById(id));
    }

    @Test
    public void testDeleteProductPotentialCompetitive_notExists() {
        // 准备参数
        Integer id = 0;

        // 调用, 并断言异常
        assertServiceException(() -> productPotentialCompetitiveService.deleteProductPotentialCompetitive(id), PRODUCT_POTENTIAL_COMPETITIVE_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetProductPotentialCompetitivePage() {
       // mock 数据
       ProductPotentialCompetitiveDO dbProductPotentialCompetitive = randomPojo(ProductPotentialCompetitiveDO.class, o -> { // 等会查询到
           o.setPotenialProductId(null);
           o.setPlatformId(null);
           o.setProductId(null);
           o.setProductLink(null);
           o.setMainImageUrl(null);
           o.setMetrics(null);
           o.setCreateTime(null);
       });
       productPotentialCompetitiveMapper.insert(dbProductPotentialCompetitive);
       // 测试 potenialProductId 不匹配
       productPotentialCompetitiveMapper.insert(cloneIgnoreId(dbProductPotentialCompetitive, o -> o.setPotenialProductId(null)));
       // 测试 platformId 不匹配
       productPotentialCompetitiveMapper.insert(cloneIgnoreId(dbProductPotentialCompetitive, o -> o.setPlatformId(null)));
       // 测试 productId 不匹配
       productPotentialCompetitiveMapper.insert(cloneIgnoreId(dbProductPotentialCompetitive, o -> o.setProductId(null)));
       // 测试 productLink 不匹配
       productPotentialCompetitiveMapper.insert(cloneIgnoreId(dbProductPotentialCompetitive, o -> o.setProductLink(null)));
       // 测试 mainImageUrl 不匹配
       productPotentialCompetitiveMapper.insert(cloneIgnoreId(dbProductPotentialCompetitive, o -> o.setMainImageUrl(null)));
       // 测试 metrics 不匹配
       productPotentialCompetitiveMapper.insert(cloneIgnoreId(dbProductPotentialCompetitive, o -> o.setMetrics(null)));
       // 测试 createTime 不匹配
       productPotentialCompetitiveMapper.insert(cloneIgnoreId(dbProductPotentialCompetitive, o -> o.setCreateTime(null)));
       // 准备参数
       ProductPotentialCompetitivePageReqVO reqVO = new ProductPotentialCompetitivePageReqVO();
       reqVO.setPotenialProductId(null);
       reqVO.setPlatformId(null);
       reqVO.setProductId(null);
       reqVO.setProductLink(null);
       reqVO.setMainImageUrl(null);
       reqVO.setMetrics(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<ProductPotentialCompetitiveDO> pageResult = productPotentialCompetitiveService.getProductPotentialCompetitivePage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbProductPotentialCompetitive, pageResult.getList().get(0));
    }

}