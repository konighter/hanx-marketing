package com.hzltd.module.erplus.dal.mysql.finance;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.dal.dataobject.finance.ErpFinancePaymentItemDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ERP 付款单项 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface ErpFinancePaymentItemMapper extends BaseMapperX<ErpFinancePaymentItemDO> {

    default List<ErpFinancePaymentItemDO> selectListByPaymentId(Long paymentId) {
        return selectList(ErpFinancePaymentItemDO::getPaymentId, paymentId);
    }

    default List<ErpFinancePaymentItemDO> selectListByPaymentIds(Collection<Long> paymentIds) {
        return selectList(ErpFinancePaymentItemDO::getPaymentId, paymentIds);
    }

    default BigDecimal selectPaymentPriceSumByBizIdAndBizType(Long bizId, Integer bizType) {
        // SQL sum 查询
        List<Map<String, Object>> result = selectMaps(new QueryWrapper<ErpFinancePaymentItemDO>()
                .select("SUM(payment_price) AS paymentPriceSum")
                .eq("biz_id", bizId)
                .eq("biz_type", bizType));
        // 获得数量
        if (CollUtil.isEmpty(result)) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(MapUtil.getDouble(result.get(0), "paymentPriceSum", 0D));
    }

}