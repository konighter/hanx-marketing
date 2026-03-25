package com.hzltd.module.erplus.service.productpotential;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.productpotential.enums.ProductpotentialStatusEnum;
import com.hzltd.module.erplus.controller.admin.productpotential.vo.ProductPotentialPageReqVO;
import com.hzltd.module.erplus.controller.admin.productpotential.vo.ProductPotentialRespVO;
import com.hzltd.module.erplus.controller.admin.productpotential.vo.ProductPotentialSaveReqVO;
import com.hzltd.module.erplus.controller.admin.productpotential.vo.ProductPotentialSimpleReqVO;
import com.hzltd.module.erplus.dal.dataobject.productpotential.ProductPotentialDO;
import com.hzltd.module.erplus.dal.mysql.productpotential.ProductPotentialMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.PRODUCT_POTENTIAL_NOT_EXISTS;

/**
 * 选品提案 Service 实现类
 *
 * @author 翰展科技
 */
@Service
@Validated
public class ProductPotentialServiceImpl implements ProductPotentialService {

    @Resource
    private ProductPotentialMapper productPotentialMapper;

    @Override
    public Integer createProductPotential(ProductPotentialSaveReqVO createReqVO) {
        // 插入
        ProductPotentialDO productPotential = convertProductPotentialDO(createReqVO);
        productPotential.setStatus(ProductpotentialStatusEnum.CREATED.getCode());
        productPotentialMapper.insert(productPotential);
        // 返回
        return productPotential.getId();
    }

    @Override
    public void updateProductPotential(ProductPotentialSaveReqVO updateReqVO) {
        // 校验存在
        validateProductPotentialExists(updateReqVO.getId());
        // 更新
        ProductPotentialDO updateObj = convertProductPotentialDO(updateReqVO);
        updateObj.setStatus(ProductpotentialStatusEnum.CREATED.getCode());
        productPotentialMapper.updateById(updateObj);
    }

    @Override
    public void updateProductPotential(ProductPotentialSimpleReqVO simpleReqVO) {
        ProductPotentialDO productPotentialDO = getProductPotentialSimple(simpleReqVO.getId());
        if (productPotentialDO == null) {
            throw exception(PRODUCT_POTENTIAL_NOT_EXISTS);
        }
        productPotentialDO.setStatus(simpleReqVO.getStatus());
        if (productPotentialDO.getStatus() != null && !productPotentialDO.getStatus().equals(simpleReqVO.getStatus())) {
            productPotentialDO.setMarks(new StringBuilder(productPotentialDO.getMarks())
                    .append("\n")
                    .append("status:").append(ProductpotentialStatusEnum.getByCode(simpleReqVO.getStatus()))
                    .append("\t").append(simpleReqVO.getMarks()).toString());
        }


        productPotentialMapper.updateById(productPotentialDO);
    }

    private ProductPotentialDO convertProductPotentialDO(ProductPotentialSaveReqVO saveReqVO) {
        return BeanUtils.toBean(saveReqVO, ProductPotentialDO.class, p -> {
            ProductPotentialRespVO respVO = BeanUtils.toBean(saveReqVO, ProductPotentialRespVO.class);
            p.setDetail(JsonUtils.toJsonString(respVO));
        });
    }

    @Override
    public void deleteProductPotential(Integer id) {
        // 校验存在
        validateProductPotentialExists(id);
        // 删除
        productPotentialMapper.deleteById(id);
    }

    private void validateProductPotentialExists(Integer id) {
        if (getProductPotentialSimple(id) == null) {
            throw exception(PRODUCT_POTENTIAL_NOT_EXISTS);
        }
    }

    @Override
    public ProductPotentialDO getProductPotentialSimple(Integer id) {
        return productPotentialMapper.selectByIdSimple(id);
    }

    @Override
    public ProductPotentialRespVO getProductPotential(Integer id) {
        ProductPotentialDO productPotential = productPotentialMapper.selectById(id);
        if (productPotential == null) {
            throw exception(PRODUCT_POTENTIAL_NOT_EXISTS);
        }
        return convertProductPotentialSaveReqVO(productPotential);
    }

    private ProductPotentialRespVO convertProductPotentialSaveReqVO(ProductPotentialDO productPotential) {
        ProductPotentialRespVO respVO = JsonUtils.parseObject(productPotential.getDetail(), ProductPotentialRespVO.class);
        respVO.setId(productPotential.getId());
        respVO.setPlatformId(productPotential.getPlatformId());
        respVO.setStatus(productPotential.getStatus());
        respVO.setCreator(productPotential.getCreator());
        respVO.setCreateTime(productPotential.getCreateTime());
        return respVO;
    }

    @Override
    public PageResult<ProductPotentialDO> getProductPotentialPage(ProductPotentialPageReqVO pageReqVO) {
        return productPotentialMapper.selectPage(pageReqVO);
    }

}