package com.hzltd.module.erplus.dal.dataobject.stock;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * ERP 仓库 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_warehouse")
@KeySequence("erplus_warehouse_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpWarehouseDO extends BaseDO {

    /**
     * 仓库编号
     */
    @TableId
    private Long id;
    /**
     * 仓库名称
     */
    private String name;
    /**
     * 类型: 平台仓/海外仓/家庭仓/活动仓
     */
    private Integer type;
    /**
     * 店铺
     */
    private Integer shopId;
    /**
     * 平台ID
     */
    private Integer platformId;
    /**
     * 市场
     */
    private String marketId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 负责人
     */
    private String principal;
    /**
     * 开启状态
     */
    private Integer status;
    /**
     * 是否默认
     */
    private Boolean defaultStatus;
}