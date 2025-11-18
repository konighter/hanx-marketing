package com.hzltd.module.erplus.service.sellplatform;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformPageReqVO;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformReqVO;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.ServiceMode;
import com.hzltd.module.erplus.dal.mysql.sellplatform.SellPlatformMapper;
import com.hzltd.module.erplus.enums.RedisKeyConstants;
import com.hzltd.module.erplus.enums.ServiceModeEnum;
import com.hzltd.module.erplus.sys.SystemPlatformService;
import com.hzltd.module.erplus.sys.model.SellPlatformModel;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.SELL_PLATFORM_NOT_EXISTS;

/**
 * 销售平台 Service 实现类
 *
 * @author hzadd
 */
@Service
@Validated
public class SellPlatformServiceImpl implements SellPlatformService, SystemPlatformService {

    @Resource
    private SellPlatformMapper sellPlatformMapper;

    @Override
    public Integer createSellPlatform(SellPlatformSaveReqVO createReqVO) {
        // 插入
        SellPlatformDO sellPlatform = convertSellPlatformDO(createReqVO);
        sellPlatformMapper.insert(sellPlatform);
        // 返回
        return sellPlatform.getId();
    }

    @Override
    public void updateSellPlatform(SellPlatformSaveReqVO updateReqVO) {
        // 校验存在
        validateSellPlatformExists(updateReqVO.getId());
        // 更新
        SellPlatformDO updateObj = convertSellPlatformDO(updateReqVO);
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

    private SellPlatformDO convertSellPlatformDO(SellPlatformSaveReqVO sellPlatformSaveReqVO ) {
        SellPlatformDO sellPlatform = BeanUtils.toBean(sellPlatformSaveReqVO, SellPlatformDO.class, platform -> {
            platform.setServiceModes( sellPlatformSaveReqVO.getShipModes().stream().map(mode -> {
                ServiceModeEnum modeEnum = ServiceModeEnum.getByCode(mode);
                return new ServiceMode(modeEnum.getName(), modeEnum.getCode());
            }).collect(Collectors.toList()));
        });
        return sellPlatform;
    }

    @Override
    public SellPlatformDO getSellPlatform(Integer id) {
        return sellPlatformMapper.selectById(id);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.KEY_SELL_PLATFORM, key = "#id")
    public SellPlatformDO getSellPlatformCache(Integer id) {
        return getSellPlatform(id);
    }

    @Override
    public SellPlatformDO getSellPlatformByCode(String code) {
        return sellPlatformMapper.selectOne(new LambdaQueryWrapperX<SellPlatformDO>().eq(SellPlatformDO::getCode, code));
    }

    @Override
    public PageResult<SellPlatformDO> getSellPlatformPage(SellPlatformPageReqVO pageReqVO) {
        return sellPlatformMapper.selectPage(pageReqVO);
    }

    @Override
    public List<SellPlatformDO> getSellPlatformList(SellPlatformReqVO reqVO) {
        return sellPlatformMapper.selectList(reqVO);
    }

    @Override
    public SellPlatformModel getSellPlatformDetail(Integer id) {
        SellPlatformDO platformDO = sellPlatformMapper.selectById(id);
        return BeanUtils.toBean(platformDO, SellPlatformModel.class);
    }
}