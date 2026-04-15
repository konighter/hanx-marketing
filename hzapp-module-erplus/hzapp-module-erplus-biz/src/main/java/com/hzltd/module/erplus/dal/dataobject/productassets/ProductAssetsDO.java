package com.hzltd.module.erplus.dal.dataobject.productassets;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

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
     * 来源
     */
    private Integer source;
    /**
     * 素材链接
     */
    private String assetLink;

    /**
     * 素材信息哈希值
     */
    private String assetInfoHash;
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