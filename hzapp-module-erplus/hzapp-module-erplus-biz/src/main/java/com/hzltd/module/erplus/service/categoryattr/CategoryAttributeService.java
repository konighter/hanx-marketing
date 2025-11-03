package com.hzltd.module.erplus.service.categoryattr;

import com.hzltd.module.erplus.dal.dataobject.categoryattr.CategoryAttributeDO;

import java.util.List;

/**
 * 品类属性 Service 接口
 *
 * @author 翰展科技
 */
public interface CategoryAttributeService {



    /**
     * 删除品类属性
     *
     * @param id 编号
     */
    void deleteCategoryAttribute(Integer id);

    /**
     * 获得品类属性
     *
     * @param id 编号
     * @return 品类属性
     */
    CategoryAttributeDO getCategoryAttribute(Integer id);

    /**
     * 查询品类属性
     * @param attr
     * @return
     */
    List<CategoryAttributeDO> getCategoryAttributes(CategoryAttributeDO attr);
}