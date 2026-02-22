package com.hzltd.module.erplus.adv.metadata.service.adgroup;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdGroupMapper;
import com.hzltd.module.erplus.adv.metadata.vo.adgroup.AdsAdGroupPageReqVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.PURCHASE_ORDER_NOT_EXISTS;

/**
 * 广告组 Service 实现类
 */
@Service
@Validated
public class AdsAdGroupServiceImpl implements AdsAdGroupService {

    @Resource
    private AdsAdGroupMapper adsAdGroupMapper;

    @Override
    public PageResult<AdsAdGroupDO> getAdGroupPage(AdsAdGroupPageReqVO pageReqVO) {
        return adsAdGroupMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateAdGroupStatus(Long id, String status) {
        // 1. 校验存在
        AdsAdGroupDO adGroup = adsAdGroupMapper.selectById(id);
        if (adGroup == null) {
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_002_001, "广告组不存在"));
        }

        // 2. 更新本地状态
        AdsAdGroupDO updateObj = new AdsAdGroupDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        adsAdGroupMapper.updateById(updateObj);
    }

    @Override
    public AdsAdGroupDO getAdGroup(Long id) {
        return adsAdGroupMapper.selectById(id);
    }

}
