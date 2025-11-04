package com.hzltd.module.erplus.service.property;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.property.vo.value.ProductPropertyValuePageReqVO;
import com.hzltd.module.erplus.controller.admin.property.vo.value.ProductPropertyValueSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductPropertyValueDO;
import com.hzltd.module.erplus.dal.mysql.spu.ErpProductPropertyValueMapper;
import com.hzltd.module.erplus.service.spu.ProductSkuService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.PROPERTY_VALUE_EXISTS;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.PROPERTY_VALUE_NOT_EXISTS;
/**
 * 商品属性值 Service 实现类
 *
 * @author LuoWenFeng
 */
@Service
@Validated
public class ProductPropertyValueServiceImpl implements ProductPropertyValueService {

    @Resource
    private ErpProductPropertyValueMapper erpProductPropertyValueMapper;

    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private ProductSkuService productSkuService;

    @Override
    public Long createPropertyValue(ProductPropertyValueSaveReqVO createReqVO) {
        // 如果已经添加过该属性值，直接返回
        ProductPropertyValueDO dbValue = erpProductPropertyValueMapper.selectByName(
                createReqVO.getPropertyId(), createReqVO.getName());
        if (dbValue != null) {
            return dbValue.getId();
        }

        // 新增
        ProductPropertyValueDO value = BeanUtils.toBean(createReqVO, ProductPropertyValueDO.class);
        erpProductPropertyValueMapper.insert(value);
        return value.getId();
    }

    @Override
    public void updatePropertyValue(ProductPropertyValueSaveReqVO updateReqVO) {
        validatePropertyValueExists(updateReqVO.getId());
        // 校验名字唯一
        ProductPropertyValueDO value = erpProductPropertyValueMapper.selectByName
                (updateReqVO.getPropertyId(), updateReqVO.getName());
        if (value != null && !value.getId().equals(updateReqVO.getId())) {
            throw exception(PROPERTY_VALUE_EXISTS);
        }

        // 更新
        ProductPropertyValueDO updateObj = BeanUtils.toBean(updateReqVO, ProductPropertyValueDO.class);
        erpProductPropertyValueMapper.updateById(updateObj);
        // 更新 sku 相关属性
        productSkuService.updateSkuPropertyValue(updateObj.getId(), updateObj.getName());
    }

    @Override
    public void deletePropertyValue(Long id) {
        validatePropertyValueExists(id);
        erpProductPropertyValueMapper.deleteById(id);
    }

    private void validatePropertyValueExists(Long id) {
        if (erpProductPropertyValueMapper.selectById(id) == null) {
            throw exception(PROPERTY_VALUE_NOT_EXISTS);
        }
    }

    @Override
    public ProductPropertyValueDO getPropertyValue(Long id) {
        return erpProductPropertyValueMapper.selectById(id);
    }

    @Override
    public List<ProductPropertyValueDO> getPropertyValueListByPropertyId(Collection<Long> propertyIds) {
        return erpProductPropertyValueMapper.selectListByPropertyId(propertyIds);
    }

    @Override
    public Integer getPropertyValueCountByPropertyId(Long propertyId) {
        return erpProductPropertyValueMapper.selectCountByPropertyId(propertyId);
    }

    @Override
    public PageResult<ProductPropertyValueDO> getPropertyValuePage(ProductPropertyValuePageReqVO pageReqVO) {
        return erpProductPropertyValueMapper.selectPage(pageReqVO);
    }

    @Override
    public void deletePropertyValueByPropertyId(Long propertyId) {
        erpProductPropertyValueMapper.deleteByPropertyId(propertyId);
    }

}
