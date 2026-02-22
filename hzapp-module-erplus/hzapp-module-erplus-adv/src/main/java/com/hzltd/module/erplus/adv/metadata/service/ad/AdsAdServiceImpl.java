package com.hzltd.module.erplus.adv.metadata.service.ad;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdMapper;
import com.hzltd.module.erplus.adv.metadata.vo.ad.AdsAdPageReqVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 广告 Service 实现类
 */
@Service
@Validated
public class AdsAdServiceImpl implements AdsAdService {

    @Resource
    private AdsAdMapper adsAdMapper;

    @Override
    public PageResult<AdsAdDO> getAdPage(AdsAdPageReqVO pageReqVO) {
        return adsAdMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateAdStatus(Long id, String status) {
        // 1. 校验存在
        AdsAdDO ad = adsAdMapper.selectById(id);
        if (ad == null) {
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_003_001, "广告不存在"));
        }

        // 2. 更新本地状态
        AdsAdDO updateObj = new AdsAdDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        adsAdMapper.updateById(updateObj);
    }

    @Override
    public AdsAdDO getAd(Long id) {
        return adsAdMapper.selectById(id);
    }

}
