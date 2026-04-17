package com.hzltd.module.erplus.service.spu;

/**
 * 商品编码生成 Service 接口
 */
public interface ProductCodeService {

    /**
     * 生成 SPU 编码
     * 规则：分类编码 + 年月日 + 流水号
     *
     * @param categoryId 分类编号
     * @return SPU 编码
     */
    String generateSpuCode(Long categoryId);

    /**
     * 生成 SKU 编码
     * 规则：分类编码 + 年月日 + 流水号
     *
     * @param categoryId 分类编号
     * @return SKU 编码
     */
    String generateSkuCode(Long categoryId);

    /**
     * 生成耗材编码
     * 规则：拼音首字母 + 年月日 + 流水号
     *
     * @param name 耗材名称
     * @return 耗材编码
     */
    String generateMaterialCode(String name);

}
