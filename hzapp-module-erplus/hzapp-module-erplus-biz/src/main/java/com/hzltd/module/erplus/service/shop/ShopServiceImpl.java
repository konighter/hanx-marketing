package com.hzltd.module.erplus.service.shop;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.collection.MapUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformReqVO;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZoneReqVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopPageReqVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopReqVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopRespVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.dataobject.sellzone.SellZoneDO;
import com.hzltd.module.erplus.dal.dataobject.shop.ShopDO;
import com.hzltd.module.erplus.dal.mysql.shop.ShopMapper;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import com.hzltd.module.erplus.service.sellzone.SellZoneService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.SHOP_NOT_EXISTS;

/**
 * 店铺信息 Service 实现类
 *
 * @author hzadd
 */
@Service
@Validated
public class ShopServiceImpl implements ShopService {

    @Resource
    private ShopMapper shopMapper;

    @Resource
    private SellPlatformService sellPlatformService;

    @Resource
    private SellZoneService sellZoneService;

    @Override
    public Integer createShop(ShopSaveReqVO createReqVO) {
        // 插入
        ShopDO shop = BeanUtils.toBean(createReqVO, ShopDO.class);
        shopMapper.insert(shop);
        // 返回
        return shop.getId();
    }

    @Override
    public void updateShop(ShopSaveReqVO updateReqVO) {
        // 校验存在
        validateShopExists(updateReqVO.getId());
        // 更新
        ShopDO updateObj = BeanUtils.toBean(updateReqVO, ShopDO.class);
        shopMapper.updateById(updateObj);
    }

    @Override
    public void deleteShop(Integer id) {
        // 校验存在
        validateShopExists(id);
        // 删除
        shopMapper.deleteById(id);
    }

    private void validateShopExists(Integer id) {
        if (shopMapper.selectById(id) == null) {
            throw exception(SHOP_NOT_EXISTS);
        }
    }

    @Override
    public ShopDO getShop(Integer id) {
        return shopMapper.selectById(id);
    }



    @Override
    public PageResult<ShopRespVO> getShopPage(ShopPageReqVO pageReqVO) {
        PageResult<ShopDO> shops = shopMapper.selectPage(pageReqVO);
        return new PageResult<>(buildShopRespVOList(shops.getList()), shops.getTotal());
    }

    @Override
    public ShopRespVO getShopResp(Integer id) {
        return buildShopRespVO(getShop(id));
    }

    @Override
    public List<ShopRespVO> getShopList(ShopReqVO reqVO) {
        return buildShopRespVOList(shopMapper.selectList(reqVO));
    }

    private List<ShopRespVO> buildShopRespVOList(List<ShopDO> shopDOS) {
        if (CollectionUtils.isEmpty(shopDOS)) {
            return Collections.emptyList();
        }

        List<SellPlatformDO> sellPlatforms = sellPlatformService.getSellPlatformList(new SellPlatformReqVO());
        Map<Integer, SellPlatformDO> sellPlatformMap = sellPlatforms.stream().collect(Collectors.toMap(SellPlatformDO::getId, Function.identity()));

        List<SellZoneDO> sellZones = sellZoneService.getSellZoneList(new SellZoneReqVO());
        Map<Integer, SellZoneDO> sellZoneMap = sellZones.stream().collect(Collectors.toMap(SellZoneDO::getId, Function.identity()));



        return BeanUtils.toBean(shopDOS, ShopRespVO.class, s -> {
            MapUtils.findAndThen(sellPlatformMap, s.getPlatform(), p -> s.setPlatformName(p.getName()));
            MapUtils.findAndThen(sellZoneMap, s.getRegion(), p -> s.setRegionName(p.getZoneName()));
        });
    }

    private ShopRespVO buildShopRespVO(ShopDO shop) {
        if (shop == null) {
            return null;
        }

        List<SellPlatformDO> sellPlatforms = sellPlatformService.getSellPlatformList(new SellPlatformReqVO());
        Map<Integer, SellPlatformDO> sellPlatformMap = sellPlatforms.stream().collect(Collectors.toMap(SellPlatformDO::getId, Function.identity()));

        List<SellZoneDO> sellZones = sellZoneService.getSellZoneList(new SellZoneReqVO());
        Map<Integer, SellZoneDO> sellZoneMap = sellZones.stream().collect(Collectors.toMap(SellZoneDO::getId, Function.identity()));

        return BeanUtils.toBean(shop, ShopRespVO.class, s -> {
            MapUtils.findAndThen(sellPlatformMap, s.getPlatform(), p -> s.setPlatformName(p.getName()));
            MapUtils.findAndThen(sellZoneMap, s.getRegion(), p -> s.setRegionName(p.getZoneName()));
        });
    }
}