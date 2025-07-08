package com.hzltd.module.erplus.service.crossproduct;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import com.hzltd.framework.test.core.ut.BaseDbUnitTest;

import com.hzltd.module.erplus.controller.admin.crossproduct.vo.*;
import com.hzltd.module.erplus.dal.dataobject.product.ErpCrossProductDO;
import com.hzltd.module.erplus.dal.mysql.product.ErpCrossProductMapper;
import com.hzltd.framework.common.pojo.PageResult;

import org.springframework.context.annotation.Import;

import static com.hzltd.framework.test.core.util.AssertUtils.*;
import static com.hzltd.framework.test.core.util.RandomUtils.*;
import static com.hzltd.framework.common.util.date.LocalDateTimeUtils.*;
import static com.hzltd.framework.common.util.object.ObjectUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link CrossProductServiceImpl} 的单元测试类
 *
 * @author 翰展科技
 */
@Import(CrossProductServiceImpl.class)
public class CrossProductServiceImplTest extends BaseDbUnitTest {

    @Resource
    private CrossProductServiceImpl crossProductService;

    @Resource
    private ErpCrossProductMapper crossProductMapper;

    @Test
    public void testCreateCrossProduct_success() {
        // 准备参数
        CrossProductSaveReqVO createReqVO = randomPojo(CrossProductSaveReqVO.class).setId(null);

        // 调用
        Long crossProductId = crossProductService.createCrossProduct(createReqVO);
        // 断言
        assertNotNull(crossProductId);
        // 校验记录的属性是否正确
        ErpCrossProductDO crossProduct = crossProductMapper.selectById(crossProductId);
        assertPojoEquals(createReqVO, crossProduct, "id");
    }

    @Test
    public void testUpdateCrossProduct_success() {
        // mock 数据
        ErpCrossProductDO dbCrossProduct = randomPojo(ErpCrossProductDO.class);
        crossProductMapper.insert(dbCrossProduct);// @Sql: 先插入出一条存在的数据
        // 准备参数
        CrossProductSaveReqVO updateReqVO = randomPojo(CrossProductSaveReqVO.class, o -> {
            o.setId(dbCrossProduct.getId()); // 设置更新的 ID
        });

        // 调用
        crossProductService.updateCrossProduct(updateReqVO);
        // 校验是否更新正确
        ErpCrossProductDO crossProduct = crossProductMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, crossProduct);
    }

    @Test
    public void testUpdateCrossProduct_notExists() {
        // 准备参数
        CrossProductSaveReqVO updateReqVO = randomPojo(CrossProductSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> crossProductService.updateCrossProduct(updateReqVO), CROSS_PRODUCT_NOT_EXISTS);
    }

    @Test
    public void testDeleteCrossProduct_success() {
        // mock 数据
        ErpCrossProductDO dbCrossProduct = randomPojo(ErpCrossProductDO.class);
        crossProductMapper.insert(dbCrossProduct);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbCrossProduct.getId();

        // 调用
        crossProductService.deleteCrossProduct(id);
       // 校验数据不存在了
       assertNull(crossProductMapper.selectById(id));
    }

    @Test
    public void testDeleteCrossProduct_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> crossProductService.deleteCrossProduct(id), CROSS_PRODUCT_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetCrossProductPage() {
       // mock 数据
       ErpCrossProductDO dbCrossProduct = randomPojo(ErpCrossProductDO.class, o -> { // 等会查询到
           o.setPlatformId(null);
           o.setShopId(null);
           o.setMarketId(null);
           o.setFulfillType(null);
           o.setLanguage(null);
           o.setPlatformProductId(null);
           o.setRelateProductId(null);
           o.setTitle(null);
           o.setKeyword(null);
           o.setFeatures(null);
           o.setDescription(null);
           o.setCategoryId(null);
           o.setBrandId(null);
           o.setMainImageUrl(null);
           o.setSliderImageUrls(null);
           o.setVideoUrl(null);
           o.setSizeChart(null);
           o.setExtra(null);
           o.setSpecType(null);
           o.setPublishStatus(null);
           o.setStatus(null);
           o.setCreateTime(null);
       });
       crossProductMapper.insert(dbCrossProduct);
       // 测试 platformId 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setPlatformId(null)));
       // 测试 shopId 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setShopId(null)));
       // 测试 marketId 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setMarketId(null)));
       // 测试 fulfillType 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setFulfillType(null)));
       // 测试 language 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setLanguage(null)));
       // 测试 platformProductId 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setPlatformProductId(null)));
       // 测试 relateProductId 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setRelateProductId(null)));
       // 测试 title 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setTitle(null)));
       // 测试 keyword 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setKeyword(null)));
       // 测试 features 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setFeatures(null)));
       // 测试 description 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setDescription(null)));
       // 测试 categoryId 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setCategoryId(null)));
       // 测试 brandId 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setBrandId(null)));
       // 测试 mainImageUrl 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setMainImageUrl(null)));
       // 测试 sliderImageUrls 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setSliderImageUrls(null)));
       // 测试 videoUrl 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setVideoUrl(null)));
       // 测试 sizeChart 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setSizeChart(null)));
       // 测试 extra 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setExtra(null)));
       // 测试 specType 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setSpecType(null)));
       // 测试 publishStatus 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setPublishStatus(null)));
       // 测试 status 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setStatus(null)));
       // 测试 createTime 不匹配
       crossProductMapper.insert(cloneIgnoreId(dbCrossProduct, o -> o.setCreateTime(null)));
       // 准备参数
       CrossProductPageReqVO reqVO = new CrossProductPageReqVO();
       reqVO.setPlatformId(null);
       reqVO.setShopId(null);
       reqVO.setMarketId(null);
       reqVO.setFulfillType(null);
       reqVO.setLanguage(null);
       reqVO.setPlatformProductId(null);
       reqVO.setRelateProductId(null);
       reqVO.setTitle(null);
       reqVO.setKeyword(null);
       reqVO.setFeatures(null);
       reqVO.setDescription(null);
       reqVO.setCategoryId(null);
       reqVO.setBrandId(null);
       reqVO.setMainImageUrl(null);
       reqVO.setSliderImageUrls(null);
       reqVO.setVideoUrl(null);
       reqVO.setSizeChart(null);
       reqVO.setExtra(null);
       reqVO.setSpecType(null);
       reqVO.setPublishStatus(null);
       reqVO.setStatus(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<ErpCrossProductDO> pageResult = crossProductService.getCrossProductPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbCrossProduct, pageResult.getList().get(0));
    }

}