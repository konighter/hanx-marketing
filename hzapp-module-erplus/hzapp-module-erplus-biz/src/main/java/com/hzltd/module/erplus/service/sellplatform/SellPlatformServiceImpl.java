package com.hzltd.module.erplus.service.sellplatform;

import com.hzltd.module.erplus.enums.RedisKeyConstants;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformPageReqVO;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformReqVO;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.mysql.sellplatform.SellPlatformMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;


import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.*;

/**
 * 销售平台 Service 实现类
 *
 * @author hzadd
 */
@Service
@Validated
public class SellPlatformServiceImpl implements SellPlatformService {

    @Resource
    private SellPlatformMapper sellPlatformMapper;

    @Override
    public Integer createSellPlatform(SellPlatformSaveReqVO createReqVO) {
        // 插入
        SellPlatformDO sellPlatform = BeanUtils.toBean(createReqVO, SellPlatformDO.class);
        sellPlatformMapper.insert(sellPlatform);
        // 返回
        return sellPlatform.getId();
    }

    @Override
    public void updateSellPlatform(SellPlatformSaveReqVO updateReqVO) {
        // 校验存在
        validateSellPlatformExists(updateReqVO.getId());
        // 更新
        SellPlatformDO updateObj = BeanUtils.toBean(updateReqVO, SellPlatformDO.class);
        sellPlatformMapper.updateById(updateObj);
    }

    @Override
    public void deleteSellPlatform(Integer id) {
        // 校验存在
        validateSellPlatformExists(id);
        // 删除
        sellPlatformMapper.deleteById(id);
    }

    private void validateSellPlatformExists(Integer id) {
        if (sellPlatformMapper.selectById(id) == null) {
            throw exception(SELL_PLATFORM_NOT_EXISTS);
        }
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.KEY_SELL_PLATFORM, key = "#id")
    public SellPlatformDO getSellPlatform(Integer id) {
        return sellPlatformMapper.selectById(id);
    }

    @Override
    public PageResult<SellPlatformDO> getSellPlatformPage(SellPlatformPageReqVO pageReqVO) {
        return sellPlatformMapper.selectPage(pageReqVO);
    }

    @Override
    public List<SellPlatformDO> getSellPlatformList(SellPlatformReqVO reqVO) {
        return sellPlatformMapper.selectList(reqVO);
    }
}