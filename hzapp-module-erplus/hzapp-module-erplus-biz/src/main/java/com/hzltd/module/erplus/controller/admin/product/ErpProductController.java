package com.hzltd.module.erplus.controller.admin.product;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSpuPageReqVO;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSpuRespVO;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSpuSimpleRespVO;
import com.hzltd.module.erplus.convert.spu.ProductSpuConvert;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSkuDO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuDO;
import com.hzltd.module.erplus.enums.ProductSpuStatusEnum;
import com.hzltd.module.erplus.service.spu.ProductSkuService;
import com.hzltd.module.erplus.service.spu.ProductSpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - ERP 产品")
@RestController
@RequestMapping("/erplus/product")
@Validated
public class ErpProductController {

    @Resource
    private ProductSpuService productSpuService;
    @Resource
    private ProductSkuService productSkuService;

    @GetMapping("/get")
    @Operation(summary = "获得产品")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:product:query')")
    public CommonResult<ProductSpuRespVO> getProduct(@RequestParam("id") Long id) {
        // 获得商品 SPU
        ProductSpuDO spu = productSpuService.getSpu(id);
        if (spu == null) {
            return success(null);
        }
        // 查询商品 SKU
        List<ProductSkuDO> skus = productSkuService.getSkuListBySpuId(spu.getId());
        return success(ProductSpuConvert.INSTANCE.convert(spu, skus));
    }

    @GetMapping("/page")
    @Operation(summary = "获得产品分页")
    @PreAuthorize("@ss.hasPermission('erp:product:query')")
    public CommonResult<PageResult<ProductSpuRespVO>> getProductPage(@Valid ProductSpuPageReqVO pageReqVO) {
        PageResult<ProductSpuDO> pageResult = productSpuService.getSpuPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProductSpuRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得产品精简列表", description = "只包含被开启的产品，主要用于前端的下拉选项")
    public CommonResult<List<ProductSpuSimpleRespVO>> getProductSimpleList() {
        List<ProductSpuDO> list = productSpuService.getSpuListByStatus(ProductSpuStatusEnum.CLAIMED.getStatus());
        // 降序排序后，返回给前端
        list.sort(Comparator.comparing(ProductSpuDO::getSort).reversed());
        return success(BeanUtils.toBean(list, ProductSpuSimpleRespVO.class));
    }


}