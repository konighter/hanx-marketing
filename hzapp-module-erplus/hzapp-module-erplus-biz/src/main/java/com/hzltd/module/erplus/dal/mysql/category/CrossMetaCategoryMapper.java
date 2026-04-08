package com.hzltd.module.erplus.dal.mysql.category;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossMetaCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 跨境平台品类 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface CrossMetaCategoryMapper extends BaseMapperX<CrossMetaCategoryDO> {

    default List<CrossMetaCategoryDO> selectBasicList(LambdaQueryWrapperX<CrossMetaCategoryDO> wrapperX) {
        return this.selectList(wrapperX.select(CrossMetaCategoryDO.class, tab -> !tab.getColumn().equalsIgnoreCase("extra")));
    }

}