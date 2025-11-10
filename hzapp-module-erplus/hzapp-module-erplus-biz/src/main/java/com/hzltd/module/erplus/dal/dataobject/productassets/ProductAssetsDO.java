package com.hzltd.module.erplus.dal.dataobject.productassets;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * 商品素材 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_product_assets")
@KeySequence("erplus_product_assets_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAssetsDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 产品ID
     */
    private Integer productId;
    /**
     * 产品名称
     */
    private Integer productName;
    /**
     * 标签
     */
    private String tags;
    /**
     * 素材类型
     */
    private Integer type;
    /**
     * 素材链接
     */
    private String assetLink;
    /**
     * 素材信息
     */
    private String assetInfo;
    /**
     * 状态
     *
     * 枚举 {@link TODO common_status 对应的类}
     */
    private Integer status;


}