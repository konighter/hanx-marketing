package com.hzltd.module.erplus.service.sellzone;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZonePageReqVO;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZoneReqVO;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZoneSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.sellzone.SellZoneDO;
import com.hzltd.module.erplus.dal.mysql.sellzone.SellZoneMapper;
import com.hzltd.module.erplus.spapi.enums.RedisKeyConstants;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.SELL_ZONE_NOT_EXISTS;

/**
 * 销售区域 Service 实现类
 *
 * @author hzadd
 */
@Service
@Validated
public class SellZoneServiceImpl implements SellZoneService {

    @Resource
    private SellZoneMapper sellZoneMapper;

    @Override
    public Integer createSellZone(SellZoneSaveReqVO createReqVO) {
        // 插入
        SellZoneDO sellZone = BeanUtils.toBean(createReqVO, SellZoneDO.class);
        sellZoneMapper.insert(sellZone);
        // 返回
        return sellZone.getId();
    }

    @Override
    public void updateSellZone(SellZoneSaveReqVO updateReqVO) {
        // 校验存在
        validateSellZoneExists(updateReqVO.getId());
        // 更新
        SellZoneDO updateObj = BeanUtils.toBean(updateReqVO, SellZoneDO.class);
        sellZoneMapper.updateById(updateObj);
    }

    @Override
    public void deleteSellZone(Integer id) {
        // 校验存在
        validateSellZoneExists(id);
        // 删除
        sellZoneMapper.deleteById(id);
    }

    private void validateSellZoneExists(Integer id) {
        if (sellZoneMapper.selectById(id) == null) {
            throw exception(SELL_ZONE_NOT_EXISTS);
        }
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.KEY_SELL_ZONE, key = "#id")
    public SellZoneDO getSellZone(Integer id) {
        return sellZoneMapper.selectById(id);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.KEY_SELL_ZONE, key = "#id")
    public SellZoneDO getSellZone(String code) {
        return sellZoneMapper.selectOne(new LambdaQueryWrapperX<SellZoneDO>().eq(SellZoneDO::getZoneCode, code));
    }

    @Override
    public PageResult<SellZoneDO> getSellZonePage(SellZonePageReqVO pageReqVO) {
        return sellZoneMapper.selectPage(pageReqVO);
    }

    @Override
    public List<SellZoneDO> getSellZoneList(SellZoneReqVO reqVO) {
        return sellZoneMapper.selectList(reqVO);
    }
}