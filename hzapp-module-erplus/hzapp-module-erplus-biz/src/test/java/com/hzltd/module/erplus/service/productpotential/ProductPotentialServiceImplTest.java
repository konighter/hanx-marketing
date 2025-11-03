package com.hzltd.module.erplus.service.productpotential;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import com.hzltd.framework.test.core.ut.BaseDbUnitTest;

import com.hzltd.module.erplus.controller.admin.productpotential.vo.*;
import com.hzltd.module.erplus.dal.dataobject.productpotential.ProductPotentialDO;
import com.hzltd.module.erplus.dal.mysql.productpotential.ProductPotentialMapper;
import com.hzltd.framework.common.pojo.PageResult;

import org.springframework.context.annotation.Import;

import static com.hzltd.module.erplus.enums.ErrorCodeConstants.*;
import static com.hzltd.framework.test.core.util.AssertUtils.*;
import static com.hzltd.framework.test.core.util.RandomUtils.*;
import static com.hzltd.framework.common.util.date.LocalDateTimeUtils.*;
import static com.hzltd.framework.common.util.object.ObjectUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ProductPotentialServiceImpl} 的单元测试类
 *
 * @author 翰展科技
 */
@Import(ProductPotentialServiceImpl.class)
public class ProductPotentialServiceImplTest extends BaseDbUnitTest {

    @Resource
    private ProductPotentialServiceImpl productPotentialService;

    @Resource
    private ProductPotentialMapper productPotentialMapper;

    @Test
    public void testCreateProductPotential_success() {
        // 准备参数
        ProductPotentialSaveReqVO createReqVO = randomPojo(ProductPotentialSaveReqVO.class).setId(null);

        // 调用
        Integer productPotentialId = productPotentialService.createProductPotential(createReqVO);
        // 断言
        assertNotNull(productPotentialId);
        // 校验记录的属性是否正确
        ProductPotentialDO productPotential = productPotentialMapper.selectById(productPotentialId);
        assertPojoEquals(createReqVO, productPotential, "id");
    }

    @Test
    public void testUpdateProductPotential_success() {
        // mock 数据
        ProductPotentialDO dbProductPotential = randomPojo(ProductPotentialDO.class);
        productPotentialMapper.insert(dbProductPotential);// @Sql: 先插入出一条存在的数据
        // 准备参数
        ProductPotentialSaveReqVO updateReqVO = randomPojo(ProductPotentialSaveReqVO.class, o -> {
            o.setId(dbProductPotential.getId()); // 设置更新的 ID
        });

        // 调用
        productPotentialService.updateProductPotential(updateReqVO);
        // 校验是否更新正确
        ProductPotentialDO productPotential = productPotentialMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, productPotential);
    }

    @Test
    public void testUpdateProductPotential_notExists() {
        // 准备参数
        ProductPotentialSaveReqVO updateReqVO = randomPojo(ProductPotentialSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> productPotentialService.updateProductPotential(updateReqVO), PRODUCT_POTENTIAL_NOT_EXISTS);
    }

    @Test
    public void testDeleteProductPotential_success() {
        // mock 数据
        ProductPotentialDO dbProductPotential = randomPojo(ProductPotentialDO.class);
        productPotentialMapper.insert(dbProductPotential);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Integer id = dbProductPotential.getId();

        // 调用
        productPotentialService.deleteProductPotential(id);
       // 校验数据不存在了
       assertNull(productPotentialMapper.selectById(id));
    }

    @Test
    public void testDeleteProductPotential_notExists() {
        // 准备参数
        Integer id = 0;

        // 调用, 并断言异常
        assertServiceException(() -> productPotentialService.deleteProductPotential(id), PRODUCT_POTENTIAL_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetProductPotentialPage() {
       // mock 数据
       ProductPotentialDO dbProductPotential = randomPojo(ProductPotentialDO.class, o -> { // 等会查询到
           o.setTitle(null);
           o.setPlatformId(null);
           o.setStatus(null);
           o.setCreateTime(null);
           o.setCreator(null);
       });
       productPotentialMapper.insert(dbProductPotential);
       // 测试 title 不匹配
       productPotentialMapper.insert(cloneIgnoreId(dbProductPotential, o -> o.setTitle(null)));
       // 测试 platformId 不匹配
       productPotentialMapper.insert(cloneIgnoreId(dbProductPotential, o -> o.setPlatformId(null)));
       // 测试 status 不匹配
       productPotentialMapper.insert(cloneIgnoreId(dbProductPotential, o -> o.setStatus(null)));
       // 测试 createTime 不匹配
       productPotentialMapper.insert(cloneIgnoreId(dbProductPotential, o -> o.setCreateTime(null)));
       // 测试 creator 不匹配
       productPotentialMapper.insert(cloneIgnoreId(dbProductPotential, o -> o.setCreator(null)));
       // 准备参数
       ProductPotentialPageReqVO reqVO = new ProductPotentialPageReqVO();
       reqVO.setTitle(null);
       reqVO.setPlatformId(null);
       reqVO.setStatus(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setCreator(null);

       // 调用
       PageResult<ProductPotentialDO> pageResult = productPotentialService.getProductPotentialPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbProductPotential, pageResult.getList().get(0));
    }

}