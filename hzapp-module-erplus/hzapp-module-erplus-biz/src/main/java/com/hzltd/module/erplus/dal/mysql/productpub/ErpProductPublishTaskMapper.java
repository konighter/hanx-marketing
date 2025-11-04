package com.hzltd.module.erplus.dal.mysql.productpub;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.productpub.ErpProductPublishTaskDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品发布任务 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpProductPublishTaskMapper extends BaseMapperX<ErpProductPublishTaskDO> {

    default ErpProductPublishTaskDO selectByProductId(Long productId, Long version) {
        return selectOne(new LambdaQueryWrapperX<ErpProductPublishTaskDO>()
                .eq(ErpProductPublishTaskDO::getProductId, productId)
                .eq(ErpProductPublishTaskDO::getVersion, version));
    }

}