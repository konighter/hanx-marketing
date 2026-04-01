package com.hzltd.module.erplus.service.shop;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.collection.MapUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.tenant.core.aop.TenantIgnore;
import com.hzltd.module.erplus.api.service.AuthorizationApiFactory;
import com.hzltd.module.erplus.api.service.NotificationSubscriptionApiFactory;
import com.hzltd.module.erplus.controller.admin.authorization.vo.PlatformAuthRespVO;
import com.hzltd.module.erplus.dal.dataobject.authorization.PlatformAuthDO;
import com.hzltd.module.erplus.dal.dataobject.authorization.ShopAuthDO;
import com.hzltd.module.erplus.dal.mysql.authorization.PlatformAuthMapper;
import com.hzltd.module.erplus.dal.mysql.authorization.ShopAuthMapper;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformReqVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.*;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.dataobject.sellzone.SellZoneDO;
import com.hzltd.module.erplus.dal.dataobject.shop.ShopDO;
import com.hzltd.module.erplus.dal.mysql.shop.ShopMapper;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.spapi.model.authorization.AuthorizationModelV0;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import com.hzltd.module.erplus.service.sellzone.SellZoneService;
import com.hzltd.module.system.service.SystemShopService;
import com.hzltd.module.system.model.ShopModel;
import com.hzltd.module.spapi.service.notification.NotificationSubscriptionApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import com.hzltd.framework.common.enums.CommonStatusEnum;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.SELL_PLATFORM_NOT_EXISTS;
import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.SHOP_NOT_EXISTS;

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
    public ShopAuthRespVO submitShopAuth(ShopAuthReqVO authReqVO) {

        ShopDO shopDO = shopMapper.selectById(authReqVO.getShopId());
        if (shopDO == null) {
            throw exception(SHOP_NOT_EXISTS);
        }
        SellPlatformDO platformDO = sellPlatformService.getSellPlatform(shopDO.getPlatform());
        if ( platformDO == null) {
            throw exception(SELL_PLATFORM_NOT_EXISTS);
        }

        if (authReqVO.isSelfAuth()) {
            // 自授权
            AuthorizationModelV0 authorizationModel = new AuthorizationModelV0();
            authorizationModel.setAppKey(authReqVO.getAppKey());
            authorizationModel.setAppSecret(authReqVO.getAppSecret());
            authorizationModel.setRefreshToken(authReqVO.getRefreshToken());

            AuthorizationModelV0 accessTokenModel = authorizationApiFactory.getCrossApiService(CrossPlatformEnum.of(platformDO.getCode()))
                    .refreshAccessToken(authorizationModel);
            accessTokenModel.setAppKey(authReqVO.getAppKey());
            accessTokenModel.setAppSecret(authReqVO.getAppSecret());
            accessTokenModel.setRefreshToken(authReqVO.getRefreshToken());
            accessTokenModel.setSelfAuth(true);

            shopDO.setAuthInfo(accessTokenModel);
            shopDO.setAuthExpTime(LocalDateTime.now().plusSeconds(accessTokenModel.getExpireIn()));

            // 自授权时, 写入 sellerId
            if (authReqVO.getSellerId() != null && !authReqVO.getSellerId().isEmpty()) {
                shopDO.setSellerId(authReqVO.getSellerId());
                accessTokenModel.setSellerId(authReqVO.getSellerId());
            }

            shopMapper.updateById(shopDO);

            // 店铺授权成功后，自动初始化消息通知订阅
            try {
                NotificationSubscriptionApi notificationApi = notificationSubscriptionApiFactory
                        .getCrossApiService(CrossPlatformEnum.of(platformDO.getCode()));
                if (notificationApi != null) {
                    notificationApi.setupNotificationSubscriptions(shopDO.getId().longValue());
                }
            } catch (Exception e) {
                log.warn("初始化消息订阅失败，不影响授权流程, shopId={}", shopDO.getId(), e);
            }

        } else {
            // 平台授权
            AuthorizationModelV0 authorizationModel = new AuthorizationModelV0();
            authorizationModel.setState(shopDO.getId().toString());
            String authGrantUrl = authorizationApiFactory.getCrossApiService(CrossPlatformEnum.valueOf(shopDO.getPlatform())).grantAuthInfo(authorizationModel);
            ShopAuthRespVO respVO = new  ShopAuthRespVO();
            respVO.setAuthGrantState(shopDO.getId().toString());
            respVO.setAuthGrantUrl(authGrantUrl);
            return respVO;
            // TODO: OAuth 授权拿到 accessToken 后, 查询用户信息获取 sellerId 并填充到 shopDO
            // 示例: shopDO.setSellerId(userInfo.getSellerId()); shopMapper.updateById(shopDO);
        }


        return null;
    }

    @TenantIgnore
    @Override
    public void refreshShopPlatformAccessToken() {
        List<ShopDO> shopDOS = shopMapper.selectList(new LambdaQueryWrapperX<ShopDO>().lt(ShopDO::getAuthExpTime, LocalDateTime.now().plusMinutes(30)));
        if (CollectionUtils.isEmpty(shopDOS)) {
            return;
        }

        shopDOS.forEach(shop -> {
            log.info("刷新店铺 {} 的平台 AccessToken 开始", shop.getName());
            SellPlatformDO platformDO = sellPlatformService.getSellPlatform(shop.getPlatform());
            if (platformDO == null) {
                log.error("店铺 {} 关联的销售平台不存在", shop.getName());
                return;
            }
            AuthorizationModelV0 accessTokenModel = authorizationApiFactory.getCrossApiService(CrossPlatformEnum.of(platformDO.getCode()))
                    .refreshAccessToken(shop.getAuthInfo());
            accessTokenModel.setAppKey(shop.getAuthInfo().getAppKey());
            accessTokenModel.setAppSecret(shop.getAuthInfo().getAppSecret());
            accessTokenModel.setRefreshToken(shop.getAuthInfo().getRefreshToken());
            shop.setAuthInfo(accessTokenModel);
            shop.setAuthExpTime(LocalDateTime.now().plusSeconds(accessTokenModel.getExpireIn()));
            shopMapper.updateById(shop);
        });

    }

    @Override
    public ShopModel getShopById(Long shopId) {
        ShopDO shopDO = shopMapper.selectById(shopId);
        SellPlatformDO platformDO = sellPlatformService.getSellPlatform(shopDO.getPlatform());
        ShopModel shopModel = BeanUtils.toBean(shopDO, ShopModel.class);
        shopModel.setPlatformCode(platformDO.getCode());
        return shopModel;
    }

    @Override
    public ShopModel getShopByExtraId(String shopId) {
        return getShopById(Long.valueOf(shopId));
    }

    @Override
//    @Cacheable(cacheNames = "shopRegion", key = "#shopId")
    public List<String> getShopRegion(String shopId) {
        if (StringUtils.isEmpty(shopId)) {
            return List.of("NA");
        }
        ShopDO shopDO = shopMapper.selectById(Long.valueOf(shopId));
        return shopDO == null ? List.of() : List.of(shopDO.getRegion());
    }

    @Override
        public List<CascaderShopRespVO> getCascaderShopList() {

        List<SellPlatformDO> platformDOList = sellPlatformService.getSellPlatformList(new SellPlatformReqVO());
        if (CollectionUtils.isEmpty(platformDOList)) {
            return List.of();
        }
        List<CascaderShopRespVO> platformRespVOList = platformDOList.stream().map(platformDO -> {
            CascaderShopRespVO respVO = new CascaderShopRespVO();
            respVO.setId(platformDO.getId());
            respVO.setName(platformDO.getName());

            List<ShopDO> shopDOList = shopMapper.selectList(new LambdaQueryWrapperX<ShopDO>().eq(ShopDO::getPlatform, platformDO.getId()));
            if (CollectionUtils.isEmpty(shopDOList)) {
                return respVO;
            }
            respVO.setChildren(shopDOList.stream().map(shopDO -> {
                CascaderShopRespVO childRespVO = new CascaderShopRespVO();
                childRespVO.setId(shopDO.getId());
                childRespVO.setName(shopDO.getName());
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
}