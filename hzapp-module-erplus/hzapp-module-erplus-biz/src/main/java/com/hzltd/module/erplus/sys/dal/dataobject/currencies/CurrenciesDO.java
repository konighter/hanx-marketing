package com.hzltd.module.erplus.sys.dal.dataobject.currencies;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * [Erplus] 货币定义 DO
 *
 * @author hzadd
 */
@TableName("erplus_currencies")
@KeySequence("erplus_currencies_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrenciesDO extends BaseDO {

    /**
     * 货币ID
     */
    @TableId
    private Integer id;
    /**
     * ISO 4217 Currency code (e.g., USD, EUR, JPY)
     */
    private String code;
    /**
     * 货币符号 (e.g., $, €, ¥)
     */
    private String symbol;
    /**
     * 货币名称
     */
    private String name;
    /**
     * 相对于基准货币的汇率 (需定期更新)
     */
    private BigDecimal exchangeRate;
    /**
     * 是否启用
     */
    private Boolean isActive;

}