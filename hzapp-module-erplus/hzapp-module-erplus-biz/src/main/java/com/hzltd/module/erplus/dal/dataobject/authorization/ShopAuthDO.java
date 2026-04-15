package com.hzltd.module.erplus.dal.dataobject.authorization;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 店铺授权关联 DO
 *
 * @author hzadd
 */
@TableName("erplus_shop_auth")
@KeySequence("erplus_shop_auth_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopAuthDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 店铺ID
     */
    private Integer shopId;
    /**
     * 授权ID
     */
    private Long authId;
    /**
     * 是否默认授权
     */
    private Boolean isDefault;

}
