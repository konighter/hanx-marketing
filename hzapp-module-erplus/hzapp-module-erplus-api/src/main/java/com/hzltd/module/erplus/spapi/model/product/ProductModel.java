package com.hzltd.module.erplus.spapi.model.product;


import com.google.common.collect.Lists;
import com.hzltd.module.erplus.spapi.enums.CrossListingStatus;
import com.hzltd.module.erplus.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.erplus.spapi.model.common.Image;
import com.hzltd.module.erplus.spapi.model.common.InventoryModel;
import com.hzltd.module.erplus.spapi.model.common.PriceModel;
import com.hzltd.module.erplus.spapi.model.common.ProductAttributeModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductModel {

    /**
     * 商品SKU
     */
    private String sellerSku;

    /**
     * 库存SKU
     */
    private String fnSku;

    /**
     * 商品编码
     */
    private String productCode;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品分类,
     * AMZ 的 productType
     */
    private String category;

    /**
     * 商品品牌
     */
    private String brand;

    /**
     * 商品区域
     * AMZ 的 marketplaceId
     */
    private String marketId;

    /**
     * 商品描述
     */
    private String productDescription;

    /**
     * 商品主图
     */
    private Image mainImage;

    /**
     * 商品轮播图
     */
    private List<Image> slideImages;

    /**
     * 商品配送类型
     */
    private FulfillTypeEnum fulfillType;

    /**
     * 商品类型
     */
    private ConditionType conditionType;

    /**
     * 商品状态
     */
    private CrossListingStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 商品属性
     */
    private List<ProductAttributeModel> productAttributes;

    public ProductModel addProductAttribute(ProductAttributeModel productAttribute) {
        if (this.productAttributes == null) {
            this.productAttributes = Lists.newArrayList();
        }
        this.productAttributes.add(productAttribute);
        return this;
    }

    /**
     * 商品报价
     */
    private List<PriceModel> offers;

    public ProductModel addOffer(PriceModel priceModel) {
        if (this.offers == null) {
            this.offers = Lists.newArrayList();
        }
        this.offers.add(priceModel);
        return this;
    }

    /**
     * 商品售卖价格
     */
    private List<PriceModel> prices;

    public ProductModel addPrice(PriceModel priceModel) {
        if (this.prices == null) {
            this.prices = Lists.newArrayList();
        }
        this.prices.add(priceModel);
        return this;
    }



    /**
     * 商品库存
     */
    private List<InventoryModel> inventory;

    public ProductModel addInventory(InventoryModel inventoryModel) {
        if (this.inventory == null) {
            this.inventory = Lists.newArrayList();
        }
        this.inventory.add(inventoryModel);
        return this;
    }

    /**
     * 商品问题
     */
    private List<IssueModel> issues;

    public ProductModel addIssue(IssueModel issueModel) {
        if (this.issues == null) {
            this.issues = Lists.newArrayList();
        }
        this.issues.add(issueModel);
        return this;
    }

    /**
     * 商品关联关系
     */
    private List<RelationShipModel> relationShips;

    public ProductModel addRelationShip(RelationShipModel relationShipModel) {
        if (this.relationShips == null) {
            this.relationShips = Lists.newArrayList();
        }
        this.relationShips.add(relationShipModel);
        return this;
    }

}
