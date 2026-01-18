package com.hzltd.module.erplus.service.stock.v2;

import com.hzltd.module.erplus.dal.dataobject.product.ErpProductDO;
import com.hzltd.module.erplus.dal.mysql.product.ErpProductMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * SKU 解析 Service 实现类
 *
 * @author 翰展科技
 */
@Service
public class ErpSkuServiceImpl implements ErpSkuService {

    @Resource
    private ErpProductMapper productMapper;

    @Override
    public String getSkuByProductId(Long productId) {
        ErpProductDO product = productMapper.selectById(productId);
        // 假设在当前系统中，产品的 barCode 或者是其唯一的映射即为 sellerSku
        // 如果有专门的 SKU 表，此逻辑应调整为查询该表
        return product != null ? product.getBarCode() : null;
    }

}
