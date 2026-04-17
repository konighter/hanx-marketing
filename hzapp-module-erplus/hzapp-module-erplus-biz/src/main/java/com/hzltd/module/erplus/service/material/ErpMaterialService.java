package com.hzltd.module.erplus.service.material;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.material.vo.material.ErpMaterialPageReqVO;
import com.hzltd.module.erplus.controller.admin.material.vo.material.ErpMaterialSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialDO;

import java.util.List;
import java.util.Map;

/**
 * ERP 耗材 Service 接口
 *
 * @author 翰展科技
 */
public interface ErpMaterialService {

    /**
     * 创建耗材
     *
     * @param reqVO 创建信息
     * @return 编号
     */
    Long createMaterial(ErpMaterialSaveReqVO reqVO);

    /**
     * 更新耗材
     *
     * @param reqVO 更新信息
     */
    void updateMaterial(ErpMaterialSaveReqVO reqVO);

    /**
     * 删除耗材
     *
     * @param id 编号
     */
    void deleteMaterial(Long id);

    /**
     * 获得耗材
     *
     * @param id 编号
     * @return 耗材
     */
    ErpMaterialDO getMaterial(Long id);

    /**
     * 获得耗材列表
     *
     * @param ids 编号列表
     * @return 耗材列表
     */
    List<ErpMaterialDO> getMaterialList(List<Long> ids);

    /**
     * 获得耗材分页
     *
     * @param pageReqVO 分页查询
     * @return 耗材分页
     */
    PageResult<ErpMaterialDO> getMaterialPage(ErpMaterialPageReqVO pageReqVO);

    /**
     * 获得耗材映射
     *
     * @param ids 编号列表
     * @return 耗材映射
     */
    Map<Long, ErpMaterialDO> getMaterialMap(List<Long> ids);

    /**
     * 获得耗材库存数量映射
     *
     * @param materialIds 耗材编号列表
     * @return 耗材库存数量映射
     */
    Map<Long, java.math.BigDecimal> getMaterialStockCountMap(java.util.Collection<Long> materialIds);

    /**
     * 获得耗材库存数量
     *
     * @param materialId 耗材编号
     * @return 耗材库存数量
     */
    java.math.BigDecimal getMaterialStockCount(Long materialId);

}
