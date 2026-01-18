package com.hzltd.module.erplus.dal.dataobject.stock.v2;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * ERP 库存盘点表 (Header) DO
 *
 * @author 翰展科技
 */
@TableName("erplus_inventory_check")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpInventoryCheckDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 盘点单号
     */
    private String checkCode;
    /**
     * 盘点仓库 ID
     */
    private Long warehouseId;
    /**
     * 状态 (1: 进行中, 10: 待审核, 20: 已过账, 90: 已取消)
     */
    private Integer status;
    /**
     * 创建者 ID
     */
    private Long operatorId;
    /**
     * 盘点执行人 ID
     */
    private Long checkUserId;
    /**
     * 备注
     */
    private String remark;

}
