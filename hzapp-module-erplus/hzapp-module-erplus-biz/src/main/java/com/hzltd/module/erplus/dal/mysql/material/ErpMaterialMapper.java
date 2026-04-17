package com.hzltd.module.erplus.dal.mysql.material;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.material.vo.material.ErpMaterialPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * ERP 耗材定义 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpMaterialMapper extends BaseMapperX<ErpMaterialDO> {

    default PageResult<ErpMaterialDO> selectPage(ErpMaterialPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ErpMaterialDO>()
                .likeIfPresent(ErpMaterialDO::getName, reqVO.getName())
                .eqIfPresent(ErpMaterialDO::getCode, reqVO.getCode())
                .eqIfPresent(ErpMaterialDO::getCategory, reqVO.getCategory())
                .eqIfPresent(ErpMaterialDO::getUnit, reqVO.getUnit())
                .orderByDesc(ErpMaterialDO::getId));
    }

}
