package com.hzltd.module.erplus.service.spu;

import com.hzltd.module.erplus.dal.dataobject.product.ProductCategoryDO;
import com.hzltd.module.erplus.dal.redis.no.ErpNoRedisDAO;
import com.hzltd.module.erplus.service.categoryattr.ProductCategoryService;
import jakarta.annotation.Resource;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.CATEGORY_NOT_EXISTS;

/**
 * 商品编码生成 Service 实现类
 */
@Service
@Validated
public class ProductCodeServiceImpl implements ProductCodeService {

    @Resource
    private ErpNoRedisDAO erpNoRedisDAO;

    @Resource
    private ProductCategoryService categoryService;

    @Override
    public String generateSpuCode(Long categoryId) {
        String prefix = getCategoryCode(categoryId);
        return erpNoRedisDAO.generate(prefix);
    }

    @Override
    public String generateSkuCode(Long categoryId) {
        String prefix = getCategoryCode(categoryId);
        return erpNoRedisDAO.generate(prefix);
    }

    @Override
    public String generateMaterialCode(String name) {
        String prefix = getPinyinFirstLetters(name);
        return erpNoRedisDAO.generate(prefix);
    }

    private String getPinyinFirstLetters(String name) {
        if (name == null || name.isEmpty()) {
            return "HC"; // 默认前缀
        }
        StringBuilder pinyin = new StringBuilder();
        for (char c : name.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyinArray != null && pinyinArray.length > 0) {
                pinyin.append(pinyinArray[0].charAt(0));
            } else {
                // 如果不是中文，直接保留数字或字母
                if (Character.isLetterOrDigit(c)) {
                    pinyin.append(c);
                }
            }
        }
        return pinyin.toString().toUpperCase();
    }

    private String getCategoryCode(Long categoryId) {
        ProductCategoryDO category = categoryService.getProductCategory(categoryId);
        if (category == null) {
            throw exception(CATEGORY_NOT_EXISTS);
        }
        return category.getCode() != null ? category.getCode() : "";
    }

}
