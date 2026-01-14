package com.hzltd.module.erplus.service.stock;

import cn.hutool.core.collection.CollUtil;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.number.MoneyUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.stock.vo.in.ErpStockInPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.in.ErpStockInSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpStockInDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpStockInItemDO;
import com.hzltd.module.erplus.dal.mysql.stock.ErpStockInItemMapper;
import com.hzltd.module.erplus.dal.mysql.stock.ErpStockInMapper;
import com.hzltd.module.erplus.dal.redis.no.ErpNoRedisDAO;
import com.hzltd.module.erplus.enums.ErpAuditStatus;
import com.hzltd.module.erplus.enums.stock.ErpStockRecordBizTypeEnum;
import com.hzltd.module.erplus.service.cross.backup.ProductService;
import com.hzltd.module.erplus.service.purchase.ErpSupplierService;
import com.hzltd.module.erplus.service.stock.bo.ErpStockRecordCreateReqBO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.framework.common.util.collection.CollectionUtils.*;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.*;

// TODO 芋艿：记录操作日志
/**
 * ERP 其它入库单 Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ErpStockInServiceImpl implements ErpStockInService {

    @Resource
    private ErpStockInMapper stockInMapper;
    @Resource
    private ErpStockInItemMapper stockInItemMapper;

    @Resource
    private ErpNoRedisDAO noRedisDAO;

    @Resource
    private ProductService productService;
    @Resource
    private ErpWarehouseService warehouseService;
    @Resource
    private ErpSupplierService supplierService;
    @Resource
    private ErpStockRecordService stockRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStockIn(ErpStockInSaveReqVO createReqVO) {
        // 1.1 校验入库项的有效性
        List<ErpStockInItemDO> stockInItems = validateStockInItems(createReqVO.getItems());
        // 1.2 校验供应商
        supplierService.validateSupplier(createReqVO.getSupplierId());
        // 1.3 生成入库单号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.STOCK_IN_NO_PREFIX);
        if (stockInMapper.selectByNo(no) != null) {
            throw exception(STOCK_IN_NO_EXISTS);
        }

        // 2.1 插入入库单
        ErpStockInDO stockIn = BeanUtils.toBean(createReqVO, ErpStockInDO.class, in -> in
                .setNo(no).setStatus(ErpAuditStatus.PROCESS.getStatus())
                .setTotalCount(getSumValue(stockInItems, ErpStockInItemDO::getCount, BigDecimal::add))
                .setTotalPrice(getSumValue(stockInItems, ErpStockInItemDO::getTotalPrice, BigDecimal::add, BigDecimal.ZERO)));
        stockInMapper.insert(stockIn);
        // 2.2 插入入库单项
        stockInItems.forEach(o -> o.setInId(stockIn.getId()));
        stockInItemMapper.insertBatch(stockInItems);
        return stockIn.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockIn(ErpStockInSaveReqVO updateReqVO) {
        // 1.1 校验存在
        ErpStockInDO stockIn = validateStockInExists(updateReqVO.getId());
        if (ErpAuditStatus.APPROVE.getStatus().equals(stockIn.getStatus())) {
            throw exception(STOCK_IN_UPDATE_FAIL_APPROVE, stockIn.getNo());
        }
        // 1.2 校验供应商
        supplierService.validateSupplier(updateReqVO.getSupplierId());
        // 1.3 校验入库项的有效性
        List<ErpStockInItemDO> stockInItems = validateStockInItems(updateReqVO.getItems());

        // 2.1 更新入库单
        ErpStockInDO updateObj = BeanUtils.toBean(updateReqVO, ErpStockInDO.class, in -> in
                .setTotalCount(getSumValue(stockInItems, ErpStockInItemDO::getCount, BigDecimal::add))
                .setTotalPrice(getSumValue(stockInItems, ErpStockInItemDO::getTotalPrice, BigDecimal::add)));
        stockInMapper.updateById(updateObj);
        // 2.2 更新入库单项
        updateStockInItemList(updateReqVO.getId(), stockInItems);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockInStatus(Long id, Integer status) {
        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        // 1.1 校验存在
        ErpStockInDO stockIn = validateStockInExists(id);
        // 1.2 校验状态
        if (stockIn.getStatus().equals(status)) {
            throw exception(approve ? STOCK_IN_APPROVE_FAIL : STOCK_IN_PROCESS_FAIL);
        }

        // 2. 更新状态
        int updateCount = stockInMapper.updateByIdAndStatus(id, stockIn.getStatus(),
                new ErpStockInDO().setStatus(status));
        if (updateCount == 0) {
            throw exception(approve ? STOCK_IN_APPROVE_FAIL : STOCK_IN_PROCESS_FAIL);
        }

        // 3. 变更库存
        List<ErpStockInItemDO> stockInItems = stockInItemMapper.selectListByInId(id);
        Integer bizType = approve ? ErpStockRecordBizTypeEnum.OTHER_IN.getType()
                : ErpStockRecordBizTypeEnum.OTHER_IN_CANCEL.getType();
        stockInItems.forEach(stockInItem -> {
            BigDecimal count = approve ? stockInItem.getCount() : stockInItem.getCount().negate();
            stockRecordService.createStockRecord(new ErpStockRecordCreateReqBO(
                    stockInItem.getProductId(), stockInItem.getWarehouseId(), count,
                    bizType, stockInItem.getInId(), stockInItem.getId(), stockIn.getNo()));
        });
    }

    private List<ErpStockInItemDO> validateStockInItems(List<ErpStockInSaveReqVO.Item> list) {
        // 1.1 校验产品存在
        List<ProductSpuDO> productList = productService.validProductList(
                convertSet(list, ErpStockInSaveReqVO.Item::getProductId));
        Map<Long, ProductSpuDO> productMap = convertMap(productList, ProductSpuDO::getId);
        // 1.2 校验仓库存在
        warehouseService.validWarehouseList(convertSet(
                list, ErpStockInSaveReqVO.Item::getWarehouseId));
        // 2. 转化为 ErpStockInItemDO 列表
        return convertList(list, o -> BeanUtils.toBean(o, ErpStockInItemDO.class, item -> item
                .setProductUnitId(productMap.get(item.getProductId()).getUnitId())
                .setTotalPrice(MoneyUtils.priceMultiply(item.getProductPrice(), item.getCount()))));
    }

    private void updateStockInItemList(Long id, List<ErpStockInItemDO> newList) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<ErpStockInItemDO> oldList = stockInItemMapper.selectListByInId(id);
        List<List<ErpStockInItemDO>> diffList = diffList(oldList, newList, // id 不同，就认为是不同的记录
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setInId(id));
            stockInItemMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            stockInItemMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            stockInItemMapper.deleteBatchIds(convertList(diffList.get(2), ErpStockInItemDO::getId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStockIn(List<Long> ids) {
        // 1. 校验不处于已审批
        List<ErpStockInDO> stockIns = stockInMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(stockIns)) {
            return;
        }
        stockIns.forEach(stockIn -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(stockIn.getStatus())) {
                throw exception(STOCK_IN_DELETE_FAIL_APPROVE, stockIn.getNo());
            }
        });

        // 2. 遍历删除，并记录操作日志
        stockIns.forEach(stockIn -> {
            // 2.1 删除入库单
            stockInMapper.deleteById(stockIn.getId());
            // 2.2 删除入库单项
            stockInItemMapper.deleteByInId(stockIn.getId());
        });
    }

    private ErpStockInDO validateStockInExists(Long id) {
        ErpStockInDO stockIn = stockInMapper.selectById(id);
        if (stockIn == null) {
            throw exception(STOCK_IN_NOT_EXISTS);
        }
        return stockIn;
    }

    @Override
    public ErpStockInDO getStockIn(Long id) {
        return stockInMapper.selectById(id);
    }

    @Override
    public PageResult<ErpStockInDO> getStockInPage(ErpStockInPageReqVO pageReqVO) {
        return stockInMapper.selectPage(pageReqVO);
    }

    // ==================== 入库项 ====================

    @Override
    public List<ErpStockInItemDO> getStockInItemListByInId(Long inId) {
        return stockInItemMapper.selectListByInId(inId);
    }

    @Override
    public List<ErpStockInItemDO> getStockInItemListByInIds(Collection<Long> inIds) {
        if (CollUtil.isEmpty(inIds)) {
            return Collections.emptyList();
        }
        return stockInItemMapper.selectListByInIds(inIds);
    }

}