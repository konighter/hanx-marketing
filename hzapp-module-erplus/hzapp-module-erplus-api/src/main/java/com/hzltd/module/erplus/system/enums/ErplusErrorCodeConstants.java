package com.hzltd.module.erplus.system.enums;

import com.hzltd.framework.common.exception.ErrorCode;

/**
 * ERP 错误码枚举类
 * <p>
 * erp 系统，使用 1-030-000-000 段
 */
public interface ErplusErrorCodeConstants {

    ErrorCode OPT_NOT_SUPPORT = new ErrorCode(1_030_000_000, "不支持该操作");

    // ========== ERP 供应商（1-030-100-000） ==========
    ErrorCode SUPPLIER_NOT_EXISTS = new ErrorCode(1_030_100_000, "供应商不存在");
    ErrorCode SUPPLIER_NOT_ENABLE = new ErrorCode(1_030_100_000, "供应商({})未启用");

    // ========== ERP 采购订单（1-030-101-000） ==========
    ErrorCode PURCHASE_ORDER_NOT_EXISTS = new ErrorCode(1_030_101_000, "采购订单不存在");
    ErrorCode PURCHASE_ORDER_DELETE_FAIL_APPROVE = new ErrorCode(1_030_101_001, "采购订单({})已审核，无法删除");
    ErrorCode PURCHASE_ORDER_PROCESS_FAIL = new ErrorCode(1_030_101_002, "反审核失败，只有已审核的采购订单才能反审核");
    ErrorCode PURCHASE_ORDER_APPROVE_FAIL = new ErrorCode(1_030_101_003, "审核失败，只有未审核的采购订单才能审核");
    ErrorCode PURCHASE_ORDER_NO_EXISTS = new ErrorCode(1_030_101_004, "生成采购单号失败，请重新提交");
    ErrorCode PURCHASE_ORDER_UPDATE_FAIL_APPROVE = new ErrorCode(1_030_101_005, "采购订单({})已审核，无法修改");
    ErrorCode PURCHASE_ORDER_NOT_APPROVE = new ErrorCode(1_030_101_006, "采购订单未审核，无法操作");
    ErrorCode PURCHASE_ORDER_ITEM_IN_FAIL_PRODUCT_EXCEED = new ErrorCode(1_030_101_007, "采购订单项({})超过最大允许入库数量({})");
    ErrorCode PURCHASE_ORDER_PROCESS_FAIL_EXISTS_IN = new ErrorCode(1_030_101_008, "反审核失败，已存在对应的采购入库单");
    ErrorCode PURCHASE_ORDER_ITEM_RETURN_FAIL_IN_EXCEED = new ErrorCode(1_030_101_009, "采购订单项({})超过最大允许退货数量({})");
    ErrorCode PURCHASE_ORDER_PROCESS_FAIL_EXISTS_RETURN = new ErrorCode(1_030_101_010, "反审核失败，已存在对应的采购退货单");

    // ========== ERP 采购入库（1-030-102-000） ==========
    ErrorCode PURCHASE_IN_NOT_EXISTS = new ErrorCode(1_030_102_000, "采购入库单不存在");
    ErrorCode PURCHASE_IN_DELETE_FAIL_APPROVE = new ErrorCode(1_030_102_001, "采购入库单({})已审核，无法删除");
    ErrorCode PURCHASE_IN_PROCESS_FAIL = new ErrorCode(1_030_102_002, "反审核失败，只有已审核的入库单才能反审核");
    ErrorCode PURCHASE_IN_APPROVE_FAIL = new ErrorCode(1_030_102_003, "审核失败，只有未审核的入库单才能审核");
    ErrorCode PURCHASE_IN_NO_EXISTS = new ErrorCode(1_030_102_004, "生成入库单失败，请重新提交");
    ErrorCode PURCHASE_IN_UPDATE_FAIL_APPROVE = new ErrorCode(1_030_102_005, "采购入库单({})已审核，无法修改");
    ErrorCode PURCHASE_IN_NOT_APPROVE = new ErrorCode(1_030_102_006, "采购入库单未审核，无法操作");
    ErrorCode PURCHASE_IN_FAIL_PAYMENT_PRICE_EXCEED = new ErrorCode(1_030_102_007, "付款金额({})超过采购入库单总金额({})");
    ErrorCode PURCHASE_IN_PROCESS_FAIL_EXISTS_PAYMENT = new ErrorCode(1_030_102_008, "反审核失败，已存在对应的付款单");

    // ========== ERP 采购退货（1-030-103-000） ==========
    ErrorCode PURCHASE_RETURN_NOT_EXISTS = new ErrorCode(1_030_103_000, "采购退货单不存在");
    ErrorCode PURCHASE_RETURN_DELETE_FAIL_APPROVE = new ErrorCode(1_030_103_001, "采购退货单({})已审核，无法删除");
    ErrorCode PURCHASE_RETURN_PROCESS_FAIL = new ErrorCode(1_030_103_002, "反审核失败，只有已审核的退货单才能反审核");
    ErrorCode PURCHASE_RETURN_APPROVE_FAIL = new ErrorCode(1_030_103_003, "审核失败，只有未审核的退货单才能审核");
    ErrorCode PURCHASE_RETURN_NO_EXISTS = new ErrorCode(1_030_103_004, "生成退货单失败，请重新提交");
    ErrorCode PURCHASE_RETURN_UPDATE_FAIL_APPROVE = new ErrorCode(1_030_103_005, "采购退货单({})已审核，无法修改");
    ErrorCode PURCHASE_RETURN_NOT_APPROVE = new ErrorCode(1_030_103_006, "采购退货单未审核，无法操作");
    ErrorCode PURCHASE_RETURN_FAIL_REFUND_PRICE_EXCEED = new ErrorCode(1_030_103_007, "退款金额({})超过采购退货单总金额({})");
    ErrorCode PURCHASE_RETURN_PROCESS_FAIL_EXISTS_REFUND = new ErrorCode(1_030_103_008, "反审核失败，已存在对应的退款单");

    // ========== ERP 客户（1-030-200-000）==========
    ErrorCode CUSTOMER_NOT_EXISTS = new ErrorCode(1_020_200_000, "客户不存在");
    ErrorCode CUSTOMER_NOT_ENABLE = new ErrorCode(1_020_200_001, "客户({})未启用");

    // ========== ERP 销售订单（1-030-201-000） ==========
    ErrorCode SALE_ORDER_NOT_EXISTS = new ErrorCode(1_020_201_000, "销售订单不存在");
    ErrorCode SALE_ORDER_DELETE_FAIL_APPROVE = new ErrorCode(1_020_201_001, "销售订单({})已审核，无法删除");
    ErrorCode SALE_ORDER_PROCESS_FAIL = new ErrorCode(1_020_201_002, "反审核失败，只有已审核的销售订单才能反审核");
    ErrorCode SALE_ORDER_APPROVE_FAIL = new ErrorCode(1_020_201_003, "审核失败，只有未审核的销售订单才能审核");
    ErrorCode SALE_ORDER_NO_EXISTS = new ErrorCode(1_020_201_004, "生成销售单号失败，请重新提交");
    ErrorCode SALE_ORDER_UPDATE_FAIL_APPROVE = new ErrorCode(1_020_201_005, "销售订单({})已审核，无法修改");
    ErrorCode SALE_ORDER_NOT_APPROVE = new ErrorCode(1_020_201_006, "销售订单未审核，无法操作");
    ErrorCode SALE_ORDER_ITEM_OUT_FAIL_PRODUCT_EXCEED = new ErrorCode(1_020_201_007, "销售订单项({})超过最大允许出库数量({})");
    ErrorCode SALE_ORDER_PROCESS_FAIL_EXISTS_OUT = new ErrorCode(1_020_201_008, "反审核失败，已存在对应的销售出库单");
    ErrorCode SALE_ORDER_ITEM_RETURN_FAIL_OUT_EXCEED = new ErrorCode(1_020_201_009, "销售订单项({})超过最大允许退货数量({})");
    ErrorCode SALE_ORDER_PROCESS_FAIL_EXISTS_RETURN = new ErrorCode(1_020_201_010, "反审核失败，已存在对应的销售退货单");

    // ========== ERP 销售出库（1-030-202-000） ==========
    ErrorCode SALE_OUT_NOT_EXISTS = new ErrorCode(1_020_202_000, "销售出库单不存在");
    ErrorCode SALE_OUT_DELETE_FAIL_APPROVE = new ErrorCode(1_020_202_001, "销售出库单({})已审核，无法删除");
    ErrorCode SALE_OUT_PROCESS_FAIL = new ErrorCode(1_020_202_002, "反审核失败，只有已审核的出库单才能反审核");
    ErrorCode SALE_OUT_APPROVE_FAIL = new ErrorCode(1_020_202_003, "审核失败，只有未审核的出库单才能审核");
    ErrorCode SALE_OUT_NO_EXISTS = new ErrorCode(1_020_202_004, "生成出库单失败，请重新提交");
    ErrorCode SALE_OUT_UPDATE_FAIL_APPROVE = new ErrorCode(1_020_202_005, "销售出库单({})已审核，无法修改");
    ErrorCode SALE_OUT_NOT_APPROVE = new ErrorCode(1_020_202_006, "销售出库单未审核，无法操作");
    ErrorCode SALE_OUT_FAIL_RECEIPT_PRICE_EXCEED = new ErrorCode(1_020_202_007, "收款金额({})超过销售出库单总金额({})");
    ErrorCode SALE_OUT_PROCESS_FAIL_EXISTS_RECEIPT = new ErrorCode(1_020_202_008, "反审核失败，已存在对应的收款单");

    // ========== ERP 销售退货（1-030-203-000） ==========
    ErrorCode SALE_RETURN_NOT_EXISTS = new ErrorCode(1_020_203_000, "销售退货单不存在");
    ErrorCode SALE_RETURN_DELETE_FAIL_APPROVE = new ErrorCode(1_020_203_001, "销售退货单({})已审核，无法删除");
    ErrorCode SALE_RETURN_PROCESS_FAIL = new ErrorCode(1_020_203_002, "反审核失败，只有已审核的退货单才能反审核");
    ErrorCode SALE_RETURN_APPROVE_FAIL = new ErrorCode(1_020_203_003, "审核失败，只有未审核的退货单才能审核");
    ErrorCode SALE_RETURN_NO_EXISTS = new ErrorCode(1_020_203_004, "生成退货单失败，请重新提交");
    ErrorCode SALE_RETURN_UPDATE_FAIL_APPROVE = new ErrorCode(1_020_203_005, "销售退货单({})已审核，无法修改");
    ErrorCode SALE_RETURN_NOT_APPROVE = new ErrorCode(1_020_203_006, "销售退货单未审核，无法操作");
    ErrorCode SALE_RETURN_FAIL_REFUND_PRICE_EXCEED = new ErrorCode(1_020_203_007, "退款金额({})超过销售退货单总金额({})");
    ErrorCode SALE_RETURN_PROCESS_FAIL_EXISTS_REFUND = new ErrorCode(1_020_203_008, "反审核失败，已存在对应的退款单");

    // ========== ERP 仓库 1-030-400-000 ==========
    ErrorCode WAREHOUSE_NOT_EXISTS = new ErrorCode(1_030_400_000, "仓库不存在");
    ErrorCode WAREHOUSE_NOT_ENABLE = new ErrorCode(1_030_400_001, "仓库({})未启用");
    ErrorCode WAREHOUSE_SHOP_PLATFORM_EXISTS = new ErrorCode(1_030_400_002, "该店铺已绑定平台仓，请勿重复绑定");

    // ========== ERP 其它入库单 1-030-401-000 ==========
    ErrorCode STOCK_IN_NOT_EXISTS = new ErrorCode(1_030_401_000, "其它入库单不存在");
    ErrorCode STOCK_IN_DELETE_FAIL_APPROVE = new ErrorCode(1_030_401_001, "其它入库单({})已审核，无法删除");
    ErrorCode STOCK_IN_PROCESS_FAIL = new ErrorCode(1_030_401_002, "反审核失败，只有已审核的入库单才能反审核");
    ErrorCode STOCK_IN_APPROVE_FAIL = new ErrorCode(1_030_401_003, "审核失败，只有未审核的入库单才能审核");
    ErrorCode STOCK_IN_NO_EXISTS = new ErrorCode(1_030_401_004, "生成入库单失败，请重新提交");
    ErrorCode STOCK_IN_UPDATE_FAIL_APPROVE = new ErrorCode(1_030_401_005, "其它入库单({})已审核，无法修改");

    // ========== ERP 其它出库单 1-030-402-000 ==========
    ErrorCode STOCK_OUT_NOT_EXISTS = new ErrorCode(1_030_402_000, "其它出库单不存在");
    ErrorCode STOCK_OUT_DELETE_FAIL_APPROVE = new ErrorCode(1_030_402_001, "其它出库单({})已审核，无法删除");
    ErrorCode STOCK_OUT_PROCESS_FAIL = new ErrorCode(1_030_402_002, "反审核失败，只有已审核的出库单才能反审核");
    ErrorCode STOCK_OUT_APPROVE_FAIL = new ErrorCode(1_030_402_003, "审核失败，只有未审核的出库单才能审核");
    ErrorCode STOCK_OUT_NO_EXISTS = new ErrorCode(1_030_402_004, "生成出库单失败，请重新提交");
    ErrorCode STOCK_OUT_UPDATE_FAIL_APPROVE = new ErrorCode(1_030_402_005, "其它出库单({})已审核，无法修改");

    // ========== ERP 库存调拨单 1-030-403-000 ==========
    ErrorCode STOCK_MOVE_NOT_EXISTS = new ErrorCode(1_030_402_000, "库存调拨单不存在");
    ErrorCode STOCK_MOVE_DELETE_FAIL_APPROVE = new ErrorCode(1_030_402_001, "库存调拨单({})已审核，无法删除");
    ErrorCode STOCK_MOVE_PROCESS_FAIL = new ErrorCode(1_030_402_002, "反审核失败，只有已审核的调拨单才能反审核");
    ErrorCode STOCK_MOVE_APPROVE_FAIL = new ErrorCode(1_030_402_003, "审核失败，只有未审核的调拨单才能审核");
    ErrorCode STOCK_MOVE_NO_EXISTS = new ErrorCode(1_030_402_004, "生成调拨号失败，请重新提交");
    ErrorCode STOCK_MOVE_UPDATE_FAIL_APPROVE = new ErrorCode(1_030_402_005, "库存调拨单({})已审核，无法修改");

    // ========== ERP 库存盘点单 1-030-403-000 ==========
    ErrorCode STOCK_CHECK_NOT_EXISTS = new ErrorCode(1_030_403_000, "库存盘点单不存在");
    ErrorCode STOCK_CHECK_DELETE_FAIL_APPROVE = new ErrorCode(1_030_403_001, "库存盘点单({})已审核，无法删除");
    ErrorCode STOCK_CHECK_PROCESS_FAIL = new ErrorCode(1_030_403_002, "反审核失败，只有已审核的盘点单才能反审核");
    ErrorCode STOCK_CHECK_APPROVE_FAIL = new ErrorCode(1_030_403_003, "审核失败，只有未审核的盘点单才能审核");
    ErrorCode STOCK_CHECK_NO_EXISTS = new ErrorCode(1_030_403_004, "生成盘点号失败，请重新提交");
    ErrorCode STOCK_CHECK_UPDATE_FAIL_APPROVE = new ErrorCode(1_030_403_005, "库存盘点单({})已审核，无法修改");

    // ========== ERP 产品库存 1-030-404-000 ==========
    ErrorCode STOCK_COUNT_NEGATIVE = new ErrorCode(1_030_404_000, "操作失败，产品({})所在仓库({})的库存：{}，小于变更数量：{}");
    ErrorCode STOCK_COUNT_NEGATIVE2 = new ErrorCode(1_030_404_001, "操作失败，产品({})所在仓库({})的库存不足");

    // ========== ERP 产品 1-030-500-000 ==========
    ErrorCode PRODUCT_NOT_EXISTS = new ErrorCode(1_030_500_000, "产品不存在");
    ErrorCode PRODUCT_NOT_ENABLE = new ErrorCode(1_030_500_001, "产品({})未启用");

    // ========== ERP 产品分类 1-030-501-000 ==========
    ErrorCode PRODUCT_CATEGORY_NOT_EXISTS = new ErrorCode(1_030_501_000, "产品分类不存在");
    ErrorCode PRODUCT_CATEGORY_EXITS_CHILDREN = new ErrorCode(1_030_501_001, "存在存在子产品分类，无法删除");
    ErrorCode PRODUCT_CATEGORY_PARENT_NOT_EXITS = new ErrorCode(1_030_501_002, "父级产品分类不存在");
    ErrorCode PRODUCT_CATEGORY_PARENT_ERROR = new ErrorCode(1_030_501_003, "不能设置自己为父产品分类");
    ErrorCode PRODUCT_CATEGORY_NAME_DUPLICATE = new ErrorCode(1_030_501_004, "已经存在该分类名称的产品分类");
    ErrorCode PRODUCT_CATEGORY_PARENT_IS_CHILD = new ErrorCode(1_030_501_005, "不能设置自己的子分类为父分类");
    ErrorCode PRODUCT_CATEGORY_EXITS_PRODUCT = new ErrorCode(1_030_502_002, "存在产品使用该分类，无法删除");

    // ========== ERP 产品单位 1-030-502-000 ==========
    ErrorCode PRODUCT_UNIT_NOT_EXISTS = new ErrorCode(1_030_502_000, "产品单位不存在");
    ErrorCode PRODUCT_UNIT_NAME_DUPLICATE = new ErrorCode(1_030_502_001, "已存在该名字的产品单位");
    ErrorCode PRODUCT_UNIT_EXITS_PRODUCT = new ErrorCode(1_030_502_002, "存在产品使用该单位，无法删除");

    // ========== ERP 结算账户 1-030-600-000 ==========
    ErrorCode ACCOUNT_NOT_EXISTS = new ErrorCode(1_030_600_000, "结算账户不存在");
    ErrorCode ACCOUNT_NOT_ENABLE = new ErrorCode(1_030_600_001, "结算账户({})未启用");

    // ========== ERP 付款单 1-030-601-000 ==========
    ErrorCode FINANCE_PAYMENT_NOT_EXISTS = new ErrorCode(1_030_601_000, "付款单不存在");
    ErrorCode FINANCE_PAYMENT_DELETE_FAIL_APPROVE = new ErrorCode(1_030_601_001, "付款单({})已审核，无法删除");
    ErrorCode FINANCE_PAYMENT_PROCESS_FAIL = new ErrorCode(1_030_601_002, "反审核失败，只有已审核的付款单才能反审核");
    ErrorCode FINANCE_PAYMENT_APPROVE_FAIL = new ErrorCode(1_030_601_003, "审核失败，只有未审核的付款单才能审核");
    ErrorCode FINANCE_PAYMENT_NO_EXISTS = new ErrorCode(1_030_601_004, "生成付款单号失败，请重新提交");
    ErrorCode FINANCE_PAYMENT_UPDATE_FAIL_APPROVE = new ErrorCode(1_030_601_005, "付款单({})已审核，无法修改");

    // ========== ERP 收款单 1-030-602-000 ==========
    ErrorCode FINANCE_RECEIPT_NOT_EXISTS = new ErrorCode(1_030_602_000, "收款单不存在");
    ErrorCode FINANCE_RECEIPT_DELETE_FAIL_APPROVE = new ErrorCode(1_030_602_001, "收款单({})已审核，无法删除");
    ErrorCode FINANCE_RECEIPT_PROCESS_FAIL = new ErrorCode(1_030_602_002, "反审核失败，只有已审核的收款单才能反审核");
    ErrorCode FINANCE_RECEIPT_APPROVE_FAIL = new ErrorCode(1_030_602_003, "审核失败，只有未审核的收款单才能审核");
    ErrorCode FINANCE_RECEIPT_NO_EXISTS = new ErrorCode(1_030_602_004, "生成收款单号失败，请重新提交");
    ErrorCode FINANCE_RECEIPT_UPDATE_FAIL_APPROVE = new ErrorCode(1_030_602_005, "收款单({})已审核，无法修改");

    // ========== 商品属性项 1-008-003-000 ==========
    ErrorCode PROPERTY_NOT_EXISTS = new ErrorCode(1_031_003_000, "属性项不存在");
    ErrorCode PROPERTY_EXISTS = new ErrorCode(1_031_003_001, "属性项的名称已存在");
    ErrorCode PROPERTY_DELETE_FAIL_VALUE_EXISTS = new ErrorCode(1_031_003_002, "属性项下存在属性值，无法删除");

    // ========== 商品属性值 1-008-004-000 ==========
    ErrorCode PROPERTY_VALUE_NOT_EXISTS = new ErrorCode(1_031_004_000, "属性值不存在");
    ErrorCode PROPERTY_VALUE_EXISTS = new ErrorCode(1_031_004_001, "属性值的名称已存在");

    // ========== 商品 SPU 1-008-005-000 ==========
    ErrorCode SPU_NOT_EXISTS = new ErrorCode(1_031_005_000, "商品 SPU 不存在");
    ErrorCode SPU_SAVE_FAIL_CATEGORY_LEVEL_ERROR = new ErrorCode(1_031_005_001, "商品分类不正确，原因：必须使用第二级的商品分类及以下");
    ErrorCode SPU_SAVE_FAIL_COUPON_TEMPLATE_NOT_EXISTS = new ErrorCode(1_031_005_002, "商品 SPU 保存失败，原因：优惠卷不存在");
    ErrorCode SPU_NOT_ENABLE = new ErrorCode(1_031_005_003, "商品 SPU【{}】不处于上架状态");
    ErrorCode SPU_NOT_RECYCLE = new ErrorCode(1_031_005_004, "商品 SPU 不处于回收站状态");

    // ========== 商品 SKU 1-008-006-000 ==========
    ErrorCode SKU_NOT_EXISTS = new ErrorCode(1_031_006_000, "商品 SKU 不存在");
    ErrorCode SKU_PROPERTIES_DUPLICATED = new ErrorCode(1_031_006_001, "商品 SKU 的属性组合存在重复");
    ErrorCode SPU_ATTR_NUMBERS_MUST_BE_EQUALS = new ErrorCode(1_031_006_002, "一个 SPU 下的每个 SKU，其属性项必须一致");
    ErrorCode SPU_SKU_NOT_DUPLICATE = new ErrorCode(1_031_006_003, "一个 SPU 下的每个 SKU，必须不重复");
    ErrorCode SKU_STOCK_NOT_ENOUGH = new ErrorCode(1_031_006_004, "商品 SKU 库存不足");

    // ========== 商品品牌相关编号 1-008-002-000 ==========
    ErrorCode BRAND_NOT_EXISTS = new ErrorCode(1_031_002_000, "品牌不存在");
    ErrorCode BRAND_DISABLED = new ErrorCode(1_031_002_001, "品牌已禁用");
    ErrorCode BRAND_NAME_EXISTS = new ErrorCode(1_031_002_002, "品牌名称已存在");

    // ========== 商品认领 TODO 补充编号 ==========
    ErrorCode PRODUCT_CLAIM_NOT_EXISTS = new ErrorCode(1_031_007_000, "认领关系不存在");
    ErrorCode PRODUCT_CLAIM_SKU_EXISTS = new ErrorCode(1_031_007_001, "商品已认领");
    ErrorCode PRODUCT_CLAIM_SKU_RULE_PLT = new ErrorCode(1_031_007_002, "不符合平台认领规则:{}");

    // ========== 商品库 ==========
    ErrorCode PRODUCT_POTENTIAL_NOT_EXISTS = new ErrorCode(1_031_008_000, "选品信息不存在");
    ErrorCode PRODUCT_POTENTIAL_COMPETITIVE_NOT_EXISTS = new ErrorCode(1_031_008_001, "选品信息不存在");
    ErrorCode PRODUCT_MONITOR_NOT_EXISTS = new ErrorCode(1_031_008_002, "商品监控信息不存在");
    ErrorCode PRODUCT_ASSETS_NOT_EXISTS = new ErrorCode(1_031_008_003, "商品素材不存在");
    ErrorCode PRODUCT_ASSETS_EXISTS = new ErrorCode(1_031_008_004, "素材已经存在");

    // ========== 店铺管理 ==========
    ErrorCode SELL_PLATFORM_NOT_EXISTS = new ErrorCode(1_032_007_000, "销售平台不存在");

    ErrorCode SELL_ZONE_NOT_EXISTS = new ErrorCode(1_032_007_001, "销售区域不存在");

    ErrorCode SHOP_NOT_EXISTS = new ErrorCode(1_032_007_002, "店铺信息不存在");

    ErrorCode PLUGIN_NOT_EXISTS = new ErrorCode(1_032_007_003, "插件不存在");

    // ============ 系统数据 ==========
    ErrorCode HS_CODES_NOT_EXISTS = new ErrorCode(1_099_000_001, "海关编码(HS Code)不存在");
    ErrorCode COUNTRIES_NOT_EXISTS = new ErrorCode(1_099_000_002, "国家/地区定义不存在");
    ErrorCode CURRENCIES_NOT_EXISTS = new ErrorCode(1_099_000_003, "货币定义不存在");
    ErrorCode LANGUAGES_NOT_EXISTS = new ErrorCode(1_099_000_004, "语言定义不存在");
    ErrorCode APP_NOT_EXISTS = new ErrorCode(1_099_000_005, "应用信息不存在");
    ErrorCode APP_ALREADY_EXISTS = new ErrorCode(1_099_000_006, "同平台下只能有一个有效应用信息");

    // ============ 跨境平台商品相关 ==========
    ErrorCode PRODUCT_NOT_VALID = new ErrorCode(2_100_000_001, "商品完整性校验失败");

    ErrorCode CATEGORY_ATTRIBUTE_NOT_EXISTS = new ErrorCode(2_100_100_001, "品类属性不存在");

    ErrorCode CATEGORY_NOT_EMPTY = new ErrorCode(2_100_100_001, "品类不能为空");

    ErrorCode CATEGORY_NOT_EXISTS = new ErrorCode(2_100_100_002, "品类不存在");

    ErrorCode LISTING_NOT_VALID = new ErrorCode(2_100_100_003, "刊登数据校验失败");

    ErrorCode CROSS_API_ERROR = new ErrorCode(2_100_200_001, "平台接口调用失败");

    ErrorCode SHIPMENT_STATUS_INVALID = new ErrorCode(2_100_300_002, "货件计划状态无效");
    ErrorCode SHIPMENT_NOT_EXISTS = new ErrorCode(2_100_300_003, "货件计划不存在");

    // ========== 广告模块 1_033_000_000 ==========

    ErrorCode ADS_ACCOUNT_NOT_EXISTS = new ErrorCode(1_033_000_001, "广告账号不存在");
    ErrorCode ADS_PLATFORM_NOT_SUPPORTED = new ErrorCode(1_033_000_002, "不支持该广告平台");
    ErrorCode ADS_AUTH_CALLBACK_ERROR = new ErrorCode(1_033_000_003, "广告授权回调失败：{}");
    ErrorCode ADS_ACCOUNT_Credential_NOT_EXISTS = new ErrorCode(1_033_000_004, "广告账号未授权");

    // ========== 广告计划 1_033_001_000 ==========
    ErrorCode ADS_CAMPAIGN_NOT_EXISTS = new ErrorCode(1_033_001_001, "广告计划不存在");
    ErrorCode ADS_CAMPAIGN_STATUS_TRANSITION_INVALID = new ErrorCode(1_033_001_002, "广告计划状态流转无效：无法从 {} 变更为 {}");

    // ========== 广告组 1_033_002_000 ==========
    ErrorCode ADS_AD_GROUP_NOT_EXISTS = new ErrorCode(1_033_002_001, "广告组不存在");

    // ========== 广告 1_033_003_000 ==========
    ErrorCode ADS_AD_NOT_EXISTS = new ErrorCode(1_033_003_001, "广告不存在");

    // ========== 关键词 1_033_004_000 ==========
    ErrorCode ADS_KEYWORD_NOT_EXISTS = new ErrorCode(1_033_004_001, "广告关键词不存在");

    // ========== 策略 1_033_005_000 ==========
    ErrorCode ADV_SKILL_NOT_EXISTS = new ErrorCode(1_033_005_001, "策略不存在");


    ErrorCode CROSS_SERVICE_ERROR = new ErrorCode(2_100_000_001, "跨境平台服务异常");

    ErrorCode PLATFORM_APP_NOT_EXISTS = new ErrorCode(2_100_000_002, "平台应用不存在");


}
