package com.hzltd.module.erplus.service.categoryattr;

import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.categoryattr.CategoryAttributeDO;
import com.hzltd.module.erplus.dal.mysql.categoryattr.CategoryAttributeMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import java.util.Collections;
import java.util.List;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.CATEGORY_ATTRIBUTE_NOT_EXISTS;

/**
 * 品类属性 Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class CategoryAttributeServiceImpl implements CategoryAttributeService {

    @Resource
    private CategoryAttributeMapper categoryAttributeMapper;


    @Override
    public void deleteCategoryAttribute(Integer id) {
        // 校验存在
        validateCategoryAttributeExists(id);
        // 删除
        categoryAttributeMapper.deleteById(id);
    }

    private void validateCategoryAttributeExists(Integer id) {
        if (categoryAttributeMapper.selectById(id) == null) {
            throw exception(CATEGORY_ATTRIBUTE_NOT_EXISTS);
        }
    }

    @Override
    public CategoryAttributeDO getCategoryAttribute(Integer id) {
        return categoryAttributeMapper.selectById(id);
    }

    @Override
    public List<CategoryAttributeDO> getCategoryAttributes(CategoryAttributeDO attr) {
        if (attr == null) {
            attr = new CategoryAttributeDO();
        }
        return categoryAttributeMapper.selectList(new LambdaQueryWrapperX<CategoryAttributeDO>()
                .eqIfPresent(CategoryAttributeDO::getCategoryId, attr.getCategoryId())
                .likeIfPresent(CategoryAttributeDO::getAttrName, attr.getAttrName())
                .eqIfPresent(CategoryAttributeDO::getAttrId, attr.getAttrId()));
    }
}