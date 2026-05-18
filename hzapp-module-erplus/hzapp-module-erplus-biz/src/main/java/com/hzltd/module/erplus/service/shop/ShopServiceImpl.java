package com.hzltd.module.erplus.service.shop;

import com.hzltd.framework.common.enums.CommonStatusEnum;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.collection.MapUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import com.hzltd.module.erplus.api.service.AuthorizationApiFactory;
import com.hzltd.module.erplus.api.service.NotificationSubscriptionApiFactory;
import com.hzltd.module.erplus.controller.admin.authorization.vo.PlatformAuthRespVO;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformReqVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.*;
import com.hzltd.module.erplus.dal.dataobject.authorization.PlatformAuthDO;
import com.hzltd.module.erplus.dal.dataobject.authorization.ShopAuthDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.dataobject.shop.ShopDO;
import com.hzltd.module.erplus.dal.mysql.authorization.PlatformAuthMapper;
import com.hzltd.module.erplus.dal.mysql.authorization.ShopAuthMapper;
import com.hzltd.module.erplus.dal.mysql.shop.ShopMapper;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import com.hzltd.module.erplus.service.sellzone.SellZoneService;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModelV0;
import com.hzltd.module.erplus.spapi.service.notification.NotificationSubscriptionApi;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.SELL_PLATFORM_NOT_EXISTS;
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.SHOP_NOT_EXISTS;

/**
 * 店铺信息 Service 实现类
 *
 * @author hzadd
 */
@Slf4j
@Service
@Validated
public class ShopServiceImpl implements ShopService , SystemShopService {

    @Resource
    private ShopMapper shopMapper;

    @Lazy
    @Resource
    private SellPlatformService sellPlatformService;

    @Resource
    private SellZoneService sellZoneService;

    @Resource
    private AuthorizationApiFactory authorizationApiFactory;

    @Resource
    private NotificationSubscriptionApiFactory notificationSubscriptionApiFactory;

    @Resource
    private ShopAuthMapper shopAuthMapper;

    @Resource
    private PlatformAuthMapper platformAuthMapper;

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

    @Override
    public List<ShopDO> getShopsByIds(List<Integer> ids) {
        return shopMapper.selectByIds(ids);
    }

    private List<ShopRespVO> buildShopRespVOList(List<ShopDO> shopDOS) {
        if (CollectionUtils.isEmpty(shopDOS)) {
            return Collections.emptyList();
        }

        List<SellPlatformDO> sellPlatforms = sellPlatformService.getSellPlatformList(new SellPlatformReqVO());
        Map<Integer, SellPlatformDO> sellPlatformMap = sellPlatforms.stream().collect(Collectors.toMap(SellPlatformDO::getId, Function.identity()));

//        List<SellZoneDO> sellZones = sellZoneService.getSellZoneList(new SellZoneReqVO());
//        Map<String, SellZoneDO> sellZoneMap = sellZones.stream().collect(Collectors.toMap(SellZoneDO::getRegion, Function.identity()));

        // 1. 获取店铺授权关联
        List<Integer> shopIds = shopDOS.stream().map(ShopDO::getId).collect(Collectors.toList());
        List<ShopAuthDO> shopAuths = shopAuthMapper.selectList(new LambdaQueryWrapperX<ShopAuthDO>()
                .in(ShopAuthDO::getShopId, shopIds));
        
        Map<Integer, List<Long>> shopAuthIdMap = shopAuths.stream()
                .collect(Collectors.groupingBy(ShopAuthDO::getShopId, 
                        Collectors.mapping(ShopAuthDO::getAuthId, Collectors.toList())));

        // 2. 获取授权详情
        List<Long> authIds = shopAuths.stream().map(ShopAuthDO::getAuthId).distinct().collect(Collectors.toList());
        Map<Long, PlatformAuthDO> authMap = Collections.emptyMap();
        if (!authIds.isEmpty()) {
            List<PlatformAuthDO> authList = platformAuthMapper.selectList(new LambdaQueryWrapperX<PlatformAuthDO>()
                    .in(PlatformAuthDO::getId, authIds));
            authMap = authList.stream().collect(Collectors.toMap(PlatformAuthDO::getId, Function.identity()));
        }

        Map<Long, PlatformAuthDO> finalAuthMap = authMap;
        return BeanUtils.toBean(shopDOS, ShopRespVO.class, s -> {
            MapUtils.findAndThen(sellPlatformMap, s.getPlatform(), p -> s.setPlatformName(p.getName()));
//            MapUtils.findAndThen(sellZoneMap, s.getRegionCode(), p -> s.setRegionName(p.getZoneName()));
            
            // 填充授权信息
            List<Long> curAuthIds = shopAuthIdMap.get(s.getId());
            if (curAuthIds != null) {
                List<PlatformAuthRespVO> authVOs = curAuthIds.stream()
                        .map(finalAuthMap::get)
                        .filter(java.util.Objects::nonNull)
                        .map(authDO -> {
                            PlatformAuthRespVO vo = BeanUtils.toBean(authDO, PlatformAuthRespVO.class);
                            // 如果令牌过期，设置状态为无效 (示例逻辑，可根据实际 token 刷新逻辑完善)
                            if (authDO.getExpiryTime() != null && authDO.getExpiryTime().isBefore(LocalDateTime.now())) {
                                vo.setStatus(1);
                            } else {
                                vo.setStatus(0);
                            }
                            return vo;
                        }).collect(Collectors.toList());
                s.setAuths(authVOs);
            }
        });
    }

    private ShopRespVO buildShopRespVO(ShopDO shop) {
        if (shop == null) {
            return null;
        }

        List<SellPlatformDO> sellPlatforms = sellPlatformService.getSellPlatformList(new SellPlatformReqVO());
        Map<Integer, SellPlatformDO> sellPlatformMap = sellPlatforms.stream().collect(Collectors.toMap(SellPlatformDO::getId, Function.identity()));

        // 获取授权详情
        List<ShopAuthDO> shopAuths = shopAuthMapper.selectList(new LambdaQueryWrapperX<ShopAuthDO>()
                .eq(ShopAuthDO::getShopId, shop.getId()));
        List<Long> authIds = shopAuths.stream().map(ShopAuthDO::getAuthId).collect(Collectors.toList());
        List<PlatformAuthRespVO> authVOs = Collections.emptyList();
        if (!authIds.isEmpty()) {
            List<PlatformAuthDO> authList = platformAuthMapper.selectList(new LambdaQueryWrapperX<PlatformAuthDO>()
                    .in(PlatformAuthDO::getId, authIds));
            authVOs = authList.stream().map(authDO -> {
                PlatformAuthRespVO vo = BeanUtils.toBean(authDO, PlatformAuthRespVO.class);
                if (authDO.getExpiryTime() != null && authDO.getExpiryTime().isBefore(LocalDateTime.now())) {
                    vo.setStatus(1);
                } else {
                    vo.setStatus(0);
                }
                return vo;
            }).collect(Collectors.toList());
        }

        List<PlatformAuthRespVO> finalAuthVOs = authVOs;
        return BeanUtils.toBean(shop, ShopRespVO.class, s -> {
            MapUtils.findAndThen(sellPlatformMap, s.getPlatform(), p -> s.setPlatformName(p.getName()));
            s.setAuths(finalAuthVOs);
        });
    }

    @Override
    public List<ShopDO> getShopListByPlatform(Integer platformId) {
        return shopMapper.selectList(new LambdaQueryWrapperX<ShopDO>().eq(ShopDO::getPlatform, platformId));
    }

    @Override
    public ShopModel getShopById(Long shopId) {
        ShopDO shopDO = shopMapper.selectById(shopId);
        SellPlatformDO platformDO = sellPlatformService.getSellPlatform(shopDO.getPlatform());
        ShopModel shopModel = BeanUtils.toBean(shopDO, ShopModel.class);
        shopModel.setPlatformCode(platformDO.getCode());
        shopModel.setMarketplace(shopDO.getMarketplaceId());
        return shopModel;
    }

    @Override
    public ShopModel getShopByExtraId(String shopId) {
        return getShopById(Long.valueOf(shopId));
    }

    @Override
//    @Cacheable(cacheNames = "shopRegion", key = "#shopId")
    public List<String> getShopMarketplace(String shopId) {
        if (StringUtils.isEmpty(shopId)) {
            return List.of("NA");
        }
        ShopDO shopDO = shopMapper.selectById(Long.valueOf(shopId));
        return shopDO == null ? List.of() : List.of(shopDO.getMarketplaceId());
    }

    @Override
    public List<CascaderShopRespVO> getCascaderShopList() {
        List<ShopDO> shopDOS = shopMapper.selectList(new ShopReqVO());
        if (CollectionUtils.isEmpty(shopDOS)) {
            return List.of();
        }

        // 根据查询的shopList，来分组平台信息，避免循环查询数据库 (N+1)
        Map<Integer, List<ShopDO>> shopDOListMap = shopDOS.stream()
                .filter(shopDO -> shopDO.getPlatform() != null)
                .collect(Collectors.groupingBy(ShopDO::getPlatform));

        List<SellPlatformDO> platformDOList = sellPlatformService.getSellPlatformList(new SellPlatformReqVO());
        if (CollectionUtils.isEmpty(platformDOList)) {
            return List.of();
        }
        
        List<CascaderShopRespVO> platformRespVOList = platformDOList.stream()
                .filter(platformDO -> shopDOListMap.containsKey(platformDO.getId()))
                .map(platformDO -> {
                    CascaderShopRespVO respVO = new CascaderShopRespVO();
                    respVO.setId(platformDO.getId());
                    respVO.setName(platformDO.getName());

                    List<ShopDO> shopDOList = shopDOListMap.get(platformDO.getId());
                    respVO.setChildren(shopDOList.stream().map(shopDO -> {
                        CascaderShopRespVO childRespVO = new CascaderShopRespVO();
                        childRespVO.setId(shopDO.getId());
                        childRespVO.setName(shopDO.getName());
                        childRespVO.setTimezone(shopDO.getTimezone());
                        return childRespVO;
                    }).collect(Collectors.toList()));

                    return respVO;
                }).collect(Collectors.toList());

        return platformRespVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopModel createOrLoadShop(ShopModel shopModel) {
        // 1. 根据 sellerId 和 marketplaceId 查找现有店铺
        ShopDO shop = shopMapper.selectOne(new LambdaQueryWrapperX<ShopDO>()
                .eq(ShopDO::getSellerId, shopModel.getSellerId())
                .eq(ShopDO::getMarketplaceId, shopModel.getMarketplace())
                .last("LIMIT 1"));

        if (shop == null) {
            // 2. 如果不存在，则创建新店铺
            shop = new ShopDO();
            shop.setName(shopModel.getName());
            shop.setPlatform(shopModel.getPlatform()); 
            shop.setMarketplaceId(shopModel.getMarketplace());
            shop.setRegion(shopModel.getRegion());
            shop.setCountryCode(shopModel.getCountryCode());
            shop.setCurrency(shopModel.getCurrency());
            shop.setAccountId(shopModel.getAccountId());
            shop.setSellerId(shopModel.getSellerId());
            shop.setTimezone(shopModel.getTimezone());
            shop.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认启用
            shopMapper.insert(shop);

        }
        shopModel.setId(shop.getId());
        return shopModel;
    }

    @Override
    public ShopModel getShopBySellerIdAndMarketplaceId(String sellerId, String marketplaceId) {
        ShopDO shop = shopMapper.selectOne(new LambdaQueryWrapperX<ShopDO>()
                .eq(ShopDO::getSellerId, sellerId)
                .eq(ShopDO::getMarketplaceId, marketplaceId)
                .last("LIMIT 1"));
        return BeanUtils.toBean(shop, ShopModel.class);
    }

    @Override
    @TenantIgnore
    public ShopModel getShopBySellerIdAndMarketplaceIdWithoutTenant(String sellerId, String marketplaceId) {
        return getShopBySellerIdAndMarketplaceId(sellerId, marketplaceId);
    }

    @Override
    @TenantIgnore
    public ShopModel getShopByIdWithoutTenant(Integer shopId) {
        return this.getShopById(shopId);
    }

    @Override
    @TenantIgnore
    public List<ShopModel> getAvailableShops() {
        List<ShopDO> shops = shopMapper.selectList();
        return BeanUtils.toBean(shops, ShopModel.class);
    }
}