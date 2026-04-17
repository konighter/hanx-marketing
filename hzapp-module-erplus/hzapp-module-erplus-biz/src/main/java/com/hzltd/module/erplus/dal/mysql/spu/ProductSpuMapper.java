package com.hzltd.module.erplus.dal.mysql.spu;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSpuPageReqVO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuDO;
import com.hzltd.module.erplus.spapi.enums.ProductConstants;
import com.hzltd.module.erplus.spapi.enums.ProductSpuStatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSpuMapper extends BaseMapperX<ProductSpuDO> {

    @Select("SELECT * FROM erplus_product_spu WHERE id = #{id}")
    ProductSpuDO selectByIdIncludeDeleted(@Param("id") Long id);

    /**
     * 获取商品 SPU 分页列表数据
     *
     * @param reqVO 分页请求参数
     * @return 商品 SPU 分页列表数据
     */
    default PageResult<ProductSpuDO> selectPage(ProductSpuPageReqVO reqVO) {
        return selectPage(reqVO, buildQuery(reqVO).orderByDesc(ProductSpuDO::getId));
    }

    default List<ProductSpuDO> selectList(ProductSpuPageReqVO reqVO) {
        return selectList(buildQuery(reqVO).orderByDesc(ProductSpuDO::getId));
    }

    private LambdaQueryWrapperX<ProductSpuDO> buildQuery(ProductSpuPageReqVO reqVO) {
        LambdaQueryWrapperX<ProductSpuDO> queryWrapper = new LambdaQueryWrapperX<ProductSpuDO>()
                .likeIfPresent(ProductSpuDO::getName, reqVO.getName())
                .eqIfPresent(ProductSpuDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(ProductSpuDO::getBrandId, reqVO.getBrandId())
                .eqIfPresent(ProductSpuDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ProductSpuDO::getCreateTime, reqVO.getCreateTime());

        // 处理搜索逻辑
        if ("SKU".equals(reqVO.getSearchType()) && ObjectUtil.isNotEmpty(reqVO.getSearchValue())) {
            // 通过 SKU 条码或名称搜索，关联到 SPU
            queryWrapper.inSql(ProductSpuDO::getId, "SELECT spu_id FROM erplus_product_sku WHERE deleted = 0 " +
                    "AND (bar_code LIKE '%" + reqVO.getSearchValue() + "%' OR name LIKE '%" + reqVO.getSearchValue() + "%')");
        } else if ("NAME".equals(reqVO.getSearchType()) && ObjectUtil.isNotEmpty(reqVO.getSearchValue())) {
            queryWrapper.like(ProductSpuDO::getName, reqVO.getSearchValue());
        } else if (ObjectUtil.isNotEmpty(reqVO.getSearchValue())) {
            // 兼容旧的搜索框
            queryWrapper.like(ProductSpuDO::getName, reqVO.getSearchValue());
        }

        appendTabQuery(reqVO, queryWrapper);
        return queryWrapper;
    }

    /**
     * 查询触发警戒库存的 SPU 数量
     *
     * @return 触发警戒库存的 SPU 数量
     */
    default Long selectCount() {
        LambdaQueryWrapperX<ProductSpuDO> queryWrapper = new LambdaQueryWrapperX<>();
        // 库存小于等于警戒库存
        queryWrapper.le(ProductSpuDO::getStock, ProductConstants.ALERT_STOCK)
                // 如果库存触发警戒库存且状态为回收站的话则不计入触发警戒库存的个数
                .notIn(ProductSpuDO::getStatus, ProductSpuStatusEnum.ARCHIVED.getStatus());
        return selectCount(queryWrapper);
    }

//    /**
//     * 获得商品 SPU 分页，提供给用户 App 使用
//     */
//    default PageResult<ProductSpuDO> selectPage(AppProductSpuPageReqVO pageReqVO, Set<Long> categoryIds) {
//        LambdaQueryWrapperX<ProductSpuDO> query = new LambdaQueryWrapperX<ProductSpuDO>()
//                // 关键字匹配，目前只匹配商品名
//                .likeIfPresent(ProductSpuDO::getName, pageReqVO.getKeyword())
//                // 分类
//                .inIfPresent(ProductSpuDO::getCategoryId, categoryIds);
//        // 上架状态 且有库存
//        query.eq(ProductSpuDO::getStatus, ProductSpuStatusEnum.ENABLE.getStatus());
//
//        // 排序逻辑
//        if (Objects.equals(pageReqVO.getSortField(), AppProductSpuPageReqVO.SORT_FIELD_SALES_COUNT)) {
//            query.last(String.format(" ORDER BY (sales_count + virtual_sales_count) %s, sort DESC, id DESC",
//                    pageReqVO.getSortAsc() ? "ASC" : "DESC"));
//        } else if (Objects.equals(pageReqVO.getSortField(), AppProductSpuPageReqVO.SORT_FIELD_PRICE)) {
//            query.orderBy(true, pageReqVO.getSortAsc(), ProductSpuDO::getPrice)
//                    .orderByDesc(ProductSpuDO::getSort).orderByDesc(ProductSpuDO::getId);
//        } else if (Objects.equals(pageReqVO.getSortField(), AppProductSpuPageReqVO.SORT_FIELD_CREATE_TIME)) {
//            query.orderBy(true, pageReqVO.getSortAsc(), ProductSpuDO::getCreateTime)
//                    .orderByDesc(ProductSpuDO::getSort).orderByDesc(ProductSpuDO::getId);
//        } else {
//            query.orderByDesc(ProductSpuDO::getSort).orderByDesc(ProductSpuDO::getId);
//        }
//        return selectPage(pageReqVO, query);
//    }

    /**
     * 更新商品 SPU 库存
     *
     * @param id        商品 SPU 编号
     * @param incrCount 增加的库存数量
     */
    default void updateStock(Long id, Integer incrCount) {
        LambdaUpdateWrapper<ProductSpuDO> updateWrapper = new LambdaUpdateWrapper<ProductSpuDO>()
                // 负数，所以使用 + 号
                .setSql(" stock = stock +" + incrCount)
                .eq(ProductSpuDO::getId, id);
        update(null, updateWrapper);
    }

    /**
     * 添加后台 Tab 选项的查询条件
     *
     * @param tabType 标签类型
     * @param query   查询条件
     */
    static void appendTabQuery(ProductSpuPageReqVO reqVO, LambdaQueryWrapperX<ProductSpuDO> query) {
        Integer tabType = reqVO.getTabType();
        String viewMode = reqVO.getViewMode();

        // 基础 Tab 类型 (与原有逻辑兼容)
        if (ObjectUtil.equals(ProductSpuPageReqVO.DRAFT, tabType)) {
            query.eqIfPresent(ProductSpuDO::getStatus, ProductSpuStatusEnum.DRAFT.getStatus());
        } else if (ObjectUtil.equals(ProductSpuPageReqVO.FOR_SALE, tabType)) {
            query.eqIfPresent(ProductSpuDO::getStatus, ProductSpuStatusEnum.FOR_SALE.getStatus());
        } else if (ObjectUtil.equals(ProductSpuPageReqVO.OFF_SALE, tabType)) {
            query.eqIfPresent(ProductSpuDO::getStatus, ProductSpuStatusEnum.OFF_SALE.getStatus());
        } else if (ObjectUtil.equals(ProductSpuPageReqVO.SOLD_OUT, tabType)) {
            query.eqIfPresent(ProductSpuDO::getStock, 0);
        } else if (ObjectUtil.equals(ProductSpuPageReqVO.ALERT_STOCK, tabType)) {
            query.le(ProductSpuDO::getStock, ProductConstants.ALERT_STOCK)
                    .notIn(ProductSpuDO::getStatus, ProductSpuStatusEnum.ARCHIVED.getStatus());
        } else if (ObjectUtil.equals(ProductSpuPageReqVO.ARCHIVED, tabType)) {
            query.eqIfPresent(ProductSpuDO::getStatus, ProductSpuStatusEnum.ARCHIVED.getStatus());
        }

        // 处理 SPU/SKU 特定视图下的产品类型筛选
        if ("SPU".equals(viewMode)) {
            if (ObjectUtil.equals(ProductSpuPageReqVO.SINGLE_SPEC, tabType)) {
                query.eq(ProductSpuDO::getSpecType, 1); // SINGLE
            } else if (ObjectUtil.equals(ProductSpuPageReqVO.MULTI_SPEC, tabType)) {
                query.eq(ProductSpuDO::getSpecType, 2); // MULTI
            }
        } else if ("SKU".equals(viewMode)) {
            if (ObjectUtil.equals(ProductSpuPageReqVO.ORDINARY_SKU, tabType)) {
                query.inSql(ProductSpuDO::getId, "SELECT spu_id FROM erplus_product_sku WHERE deleted = 0 AND type = 1");
            } else if (ObjectUtil.equals(ProductSpuPageReqVO.COMBO_SKU, tabType)) {
                query.inSql(ProductSpuDO::getId, "SELECT spu_id FROM erplus_product_sku WHERE deleted = 0 AND type = 2");
            }
        }
    }

    /**
     * 更新商品 SPU 浏览量
     *
     * @param id        商品 SPU 编号
     * @param incrCount 增加的数量
     */
    default void updateBrowseCount(Long id, int incrCount) {
        LambdaUpdateWrapper<ProductSpuDO> updateWrapper = new LambdaUpdateWrapper<ProductSpuDO>()
                .setSql(" browse_count = browse_count +" + incrCount)
                .eq(ProductSpuDO::getId, id);
        update(null, updateWrapper);
    }

}
