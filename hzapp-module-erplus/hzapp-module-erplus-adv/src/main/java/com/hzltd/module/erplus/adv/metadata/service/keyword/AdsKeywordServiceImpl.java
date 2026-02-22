package com.hzltd.module.erplus.adv.metadata.service.keyword;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsKeywordDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsKeywordMapper;
import com.hzltd.module.erplus.adv.metadata.vo.keyword.AdsKeywordPageReqVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 广告关键词 Service 实现类
 */
@Service
@Validated
public class AdsKeywordServiceImpl implements AdsKeywordService {

    @Resource
    private AdsKeywordMapper adsKeywordMapper;

    @Override
    public PageResult<AdsKeywordDO> getKeywordPage(AdsKeywordPageReqVO pageReqVO) {
        return adsKeywordMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateKeywordStatus(Long id, String status) {
        // 1. 校验存在
        AdsKeywordDO keyword = adsKeywordMapper.selectById(id);
        if (keyword == null) {
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_004_001, "广告关键词不存在"));
        }

        // 2. 更新本地状态
        AdsKeywordDO updateObj = new AdsKeywordDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        adsKeywordMapper.updateById(updateObj);
    }

    @Override
    public AdsKeywordDO getKeyword(Long id) {
        return adsKeywordMapper.selectById(id);
    }

}
