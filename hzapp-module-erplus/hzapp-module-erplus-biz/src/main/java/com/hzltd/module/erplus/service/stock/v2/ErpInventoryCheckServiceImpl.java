package com.hzltd.module.erplus.service.stock.v2;

import cn.hutool.core.collection.CollUtil;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryCheckPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryCheckSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryCheckDO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryCheckItemDO;
import com.hzltd.module.erplus.dal.mysql.stock.v2.ErpInventoryCheckItemMapper;
import com.hzltd.module.erplus.dal.mysql.stock.v2.ErpInventoryCheckMapper;
import com.hzltd.module.erplus.dal.redis.no.ErpNoRedisDAO;
import com.hzltd.module.erplus.service.event.ErpEventBus;
import com.hzltd.module.erplus.service.stock.v2.event.ErpStockUpdateEvent;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.STOCK_CHECK_NOT_EXISTS;

/**
 * ERP 库存盘点 (V2) Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ErpInventoryCheckServiceImpl implements ErpInventoryCheckService {

    @Resource
    private ErpInventoryCheckMapper inventoryCheckMapper;
    @Resource
    private ErpInventoryCheckItemMapper inventoryCheckItemMapper;
    @Resource
    private ErpEventBus erpEventBus;
    @Resource
    private ErpNoRedisDAO noRedisDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInventoryCheck(ErpInventoryCheckSaveReqVO createReqVO) {
        String checkCode = noRedisDAO.generate("CHK");

        ErpInventoryCheckDO check = BeanUtils.toBean(createReqVO, ErpInventoryCheckDO.class)
                .setCheckCode(checkCode)
                .setStatus(1); // 1: 进行中
        inventoryCheckMapper.insert(check);

        List<ErpInventoryCheckItemDO> items = BeanUtils.toBean(createReqVO.getItems(), ErpInventoryCheckItemDO.class);
        items.forEach(item -> item.setCheckId(check.getId()));
        inventoryCheckItemMapper.insertBatch(items);

        return check.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInventoryCheck(ErpInventoryCheckSaveReqVO updateReqVO) {
        ErpInventoryCheckDO check = validateInventoryCheckExists(updateReqVO.getId());
        if (check.getStatus() != 1) {
            throw exception(STOCK_CHECK_NOT_EXISTS);
        }

        BeanUtils.copyProperties(updateReqVO, check);
        inventoryCheckMapper.updateById(check);

        inventoryCheckItemMapper
                .delete(new com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX<ErpInventoryCheckItemDO>()
                        .eq(ErpInventoryCheckItemDO::getCheckId, check.getId()));
        List<ErpInventoryCheckItemDO> items = BeanUtils.toBean(updateReqVO.getItems(), ErpInventoryCheckItemDO.class);
        items.forEach(item -> item.setCheckId(check.getId()));
        inventoryCheckItemMapper.insertBatch(items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveInventoryCheck(Long id) {
        ErpInventoryCheckDO check = validateInventoryCheckExists(id);
        if (check.getStatus() != 1) {
            return;
        }

        List<ErpInventoryCheckItemDO> items = inventoryCheckItemMapper.selectListByCheckId(id);

        // 过滤出有差异的项
        List<ErpInventoryCheckItemDO> diffItems = items.stream()
                .filter(item -> item.getCheckQty() != null && !item.getCheckQty().equals(item.getBookQty()))
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(diffItems)) {
            // 发布库存变动事件 (EDA 模式)
            ErpStockUpdateEvent event = ErpStockUpdateEvent.builder()
                    .type(40) // 40:调整
                    .fromType("WH")
                    .fromId(check.getWarehouseId())
                    .toType("WH")
                    .toId(check.getWarehouseId())
                    .refType("CHECK")
                    .refCode(check.getCheckCode())
                    .remark("盘点自动调整")
                    .items(diffItems.stream()
                            .map(i -> new ErpStockUpdateEvent.Item(i.getSellerSku(), i.getCheckQty() - i.getBookQty()))
                            .collect(Collectors.toList()))
                    .build();

            erpEventBus.post("STOCK", event);

            // 注意：由于事件监听器通常是同步的且在同一个事务内，
            // 我们可以预期对应的 Inventory Bill 已经创建，但这里无法直接拿到产生的 billId 除非事件机制做特殊处理。
            // 简单起见，如果需要回填 ID，可以考虑在监听器中回填，或者保持解耦。
        }

        check.setStatus(20); // 20: 已过账
        inventoryCheckMapper.updateById(check);
    }

    private ErpInventoryCheckDO validateInventoryCheckExists(Long id) {
        ErpInventoryCheckDO check = inventoryCheckMapper.selectById(id);
        if (check == null) {
            throw exception(STOCK_CHECK_NOT_EXISTS);
        }
        return check;
    }

    @Override
    public ErpInventoryCheckDO getInventoryCheck(Long id) {
        return inventoryCheckMapper.selectById(id);
    }

    @Override
    public PageResult<ErpInventoryCheckDO> getInventoryCheckPage(ErpInventoryCheckPageReqVO pageReqVO) {
        return inventoryCheckMapper.selectPage(pageReqVO,
                new com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX<ErpInventoryCheckDO>()
                        .likeIfPresent(ErpInventoryCheckDO::getCheckCode, pageReqVO.getCheckCode())
                        .eqIfPresent(ErpInventoryCheckDO::getWarehouseId, pageReqVO.getWarehouseId())
                        .eqIfPresent(ErpInventoryCheckDO::getStatus, pageReqVO.getStatus())
                        .betweenIfPresent(ErpInventoryCheckDO::getCreateTime, pageReqVO.getCreateTime())
                        .orderByDesc(ErpInventoryCheckDO::getId));
    }

    @Override
    public List<ErpInventoryCheckItemDO> getInventoryCheckItemListByCheckId(Long checkId) {
        return inventoryCheckItemMapper.selectListByCheckId(checkId);
    }

}
