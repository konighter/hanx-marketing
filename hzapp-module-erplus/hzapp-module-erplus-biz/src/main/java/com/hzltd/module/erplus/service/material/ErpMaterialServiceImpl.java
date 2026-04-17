package com.hzltd.module.erplus.service.material;

import cn.hutool.core.collection.CollUtil;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.material.vo.material.ErpMaterialPageReqVO;
import com.hzltd.module.erplus.controller.admin.material.vo.material.ErpMaterialSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialDO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialStockDO;
import com.hzltd.module.erplus.dal.mysql.material.ErpMaterialMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;

/**
 * ERP 耗材 Service 实现类
 *
 * 管理耗材的基础档案信息。
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ErpMaterialServiceImpl implements ErpMaterialService {

    @Resource
    private ErpMaterialMapper materialMapper;
    @Resource
    @Lazy
    private ErpMaterialStockService materialStockService;

    @Override
    public BigDecimal getMaterialStockCount(Long materialId) {
        return materialStockService.getMaterialStockCount(materialId);
    }

    @Override
    public Map<Long, BigDecimal> getMaterialStockCountMap(Collection<Long> materialIds) {
        if (CollUtil.isEmpty(materialIds)) {
            return Map.of();
        }
        // 1. 批量获取库存记录
        List<ErpMaterialStockDO> stocks = materialStockService.getMaterialStockListByMaterialIds(new ArrayList<>(materialIds));
        // 2. 按耗材 ID 分组并累加库存数量
        return stocks.stream().collect(Collectors.groupingBy(
                ErpMaterialStockDO::getMaterialId,
                Collectors.reducing(BigDecimal.ZERO, 
                        ErpMaterialStockDO::getQuantity, 
                        BigDecimal::add)
        ));
    }

    @Override
    public Long createMaterial(ErpMaterialSaveReqVO reqVO) {
        // 转换并插入耗材档案
        ErpMaterialDO material = BeanUtils.toBean(reqVO, ErpMaterialDO.class);
        materialMapper.insert(material);
        return material.getId();
    }

    @Override
    public void updateMaterial(ErpMaterialSaveReqVO reqVO) {
        // 更新耗材档案信息
        ErpMaterialDO material = BeanUtils.toBean(reqVO, ErpMaterialDO.class);
        materialMapper.updateById(material);
    }

    @Override
    public void deleteMaterial(Long id) {
        // 删除耗材档案
        materialMapper.deleteById(id);
    }

    @Override
    public ErpMaterialDO getMaterial(Long id) {
        // 获取指定 ID 的耗材档案
        return materialMapper.selectById(id);
    }

    @Override
    public List<ErpMaterialDO> getMaterialList(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return List.of();
        }
        // 批量查询耗材档案
        return materialMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ErpMaterialDO> getMaterialPage(ErpMaterialPageReqVO pageReqVO) {
        // 分页查询耗材档案
        return materialMapper.selectPage(pageReqVO);
    }

    @Override
    public Map<Long, ErpMaterialDO> getMaterialMap(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Map.of();
        }
        // 查询并转换为 Map 结构，方便业务层进行数据装配
        List<ErpMaterialDO> list = getMaterialList(ids);
        return list.stream().collect(Collectors.toMap(ErpMaterialDO::getId, m -> m));
    }

}
