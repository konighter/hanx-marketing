package com.hzltd.module.amz.spapi;

import com.amazon.SellingPartnerAPIAA.LWAAccessTokenCache;
import com.amazon.SellingPartnerAPIAA.LWAAuthorizationCredentials;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.api.adapter.AbsPlatformService;
import com.hzltd.module.amz.spapi.model.AmzPurchasableOfferModel;
import com.hzltd.module.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.amz.controller.admin.vo.AmzConfirmTransportOptionRequest;
import com.hzltd.module.amz.controller.admin.vo.AmzSetPackingInfoRequest;
import com.hzltd.module.amz.controller.admin.vo.AmzTransportOptionGenerateRequest;
import com.hzltd.module.spapi.enums.CrossListingStatus;
import com.hzltd.module.spapi.enums.CrossOrderStatus;
import com.hzltd.module.spapi.enums.RelationLevel;
import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.spapi.model.common.Image;
import com.hzltd.module.spapi.model.common.PriceModel;
import com.hzltd.module.spapi.model.common.ProductAttributeModel;
import com.hzltd.module.spapi.model.pricing.GetOfferResponse;
import com.hzltd.module.spapi.model.pricing.OfferModel;
import com.hzltd.module.spapi.model.pricing.PricingModel;
import com.hzltd.module.spapi.model.product.IssueModel;
import com.hzltd.module.spapi.model.product.MultiMarketProductModel;
import com.hzltd.module.spapi.model.product.ProductModel;
import com.hzltd.module.spapi.model.product.RelationShipModel;
import com.hzltd.module.infra.dal.dataobject.config.ConfigDO;
import com.hzltd.module.infra.service.config.ConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import software.amazon.spapi.api.fba.inventory.v1.FbaInventoryApi;
import software.amazon.spapi.api.finances.v0.DefaultApi;
import software.amazon.spapi.api.fulfillment.inbound.v2024_03_20.FbaInboundApi;
import software.amazon.spapi.api.listings.items.v2021_08_01.ListingsApi;
import software.amazon.spapi.api.notifications.v1.NotificationsApi;
import software.amazon.spapi.api.orders.v0.OrdersV0Api;
import software.amazon.spapi.api.pricing.v0.ProductPricingApi;
import software.amazon.spapi.api.productfees.v0.FeesApi;
import software.amazon.spapi.api.producttypedefinitions.v2020_09_01.DefinitionsApi;
import software.amazon.spapi.api.sellers.v1.SellersApi;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.*;
import software.amazon.spapi.models.listings.items.v2021_08_01.*;
import software.amazon.spapi.models.listings.items.v2021_08_01.Item;
import software.amazon.spapi.models.orders.v0.Order;
import software.amazon.spapi.models.pricing.v0.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class AbsAmzPlatformApiService extends AbsPlatformService {

    private static final String API_SANDBOX_ENDPOINT = "API_SANDBOX_ENDPOINT";

    @Resource
    private ConfigService configService;

    @Resource
    private LocalLWAAccessTokenCache localLWAAccessTokenCache;

    @Override
    public String getAuthScope() {
        return "AMAZON_SP";
    }

    public String getAuthEndpoint() {
        return "https://api.amazon.com/auth/o2/token";
    }

    public String getApiEndpoint(String marketPlaceId) {
        ConfigDO configDO = configService.getConfigByKey(API_SANDBOX_ENDPOINT);
        boolean isSandbox = configDO != null && configDO.getValue() != null && Boolean.parseBoolean(configDO.getValue());

        return isSandbox ? "https://sandbox.sellingpartnerapi-na.amazon.com" : "https://sellingpartnerapi-na.amazon.com";
    }

    public LWAAccessTokenCache getLocalTokenCache() {
        return localLWAAccessTokenCache;
    }

    public LWAAuthorizationCredentials getLWAAuthorizationCredentials(AuthorizationModel authorizationModel) {
        return LWAAuthorizationCredentials.builder()
                .clientId(authorizationModel.getAppKey())
                .clientSecret(authorizationModel.getAppSecret())
                .refreshToken(authorizationModel.getRefreshToken())
                .endpoint(this.getAuthEndpoint())
                .build();
    }

    public LWAAuthorizationCredentials getLWAAuthorizationCredentialsScoped(AuthorizationModel authorizationModel, String... scope) {
        return LWAAuthorizationCredentials.builder()
                .clientId(authorizationModel.getAppKey())
                .clientSecret(authorizationModel.getAppSecret())
                .withScopes(scope)
                .endpoint(this.getAuthEndpoint())
                .build();
    }


    //============== Api初始化 ==============
    public ListingsApi getListingsApi(ApiRequest<?> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request);
        List<String> marketPlaceIds = this.getShopMarkets(request.getShopId());
        return new ListingsApi.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentials(authorizationModel))
                .lwaAccessTokenCache(this.getLocalTokenCache())
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }

    public ProductPricingApi getPricingApi(ApiRequest<?> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request);
        List<String> marketPlaceIds = this.getShopMarkets(request.getShopId());
        return new ProductPricingApi.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentials(authorizationModel))
                .lwaAccessTokenCache(this.getLocalTokenCache())
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }

    public FbaInventoryApi getFbaInventoryApi(ApiRequest<?> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request);
        List<String> marketPlaceIds = this.getShopMarkets(request.getShopId());
        return new FbaInventoryApi.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentials(authorizationModel))
                .lwaAccessTokenCache(this.getLocalTokenCache())
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }

    public OrdersV0Api getOrdersApi(ApiRequest<?> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request);
        List<String> marketPlaceIds = this.getShopMarkets(request.getShopId());
        return new OrdersV0Api.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentials(authorizationModel))
                .lwaAccessTokenCache(this.getLocalTokenCache())
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }

    public OrdersV0Api getOrderApi(ApiRequest<?> request) {
        return getOrdersApi(request);
    }

    public SellersApi getSellersApi(ApiRequest<?> request) {
        AuthorizationModel authorizationModel = request.getAuthorizationModel() != null ? request.getAuthorizationModel() : this.getAuthorizationModel(request);
        List<String> marketPlaceIds = this.getShopMarkets(request.getShopId());
        return new SellersApi.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentials(authorizationModel))
                .lwaAccessTokenCache(this.getLocalTokenCache())
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }

    public FeesApi getFeesApi(ApiRequest<?> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request);
        List<String> marketPlaceIds = this.getShopMarkets(request.getShopId());
        return new FeesApi.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentials(authorizationModel))
                .lwaAccessTokenCache(this.getLocalTokenCache())
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }

    public DefinitionsApi getDefinitionsApi(ApiRequest<?> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request);
        List<String> marketPlaceIds = this.getShopMarkets(request.getShopId());
        return new DefinitionsApi.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentials(authorizationModel))
                .lwaAccessTokenCache(this.getLocalTokenCache())
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }

    public NotificationsApi getNotificationsApi(ApiRequest<?> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request);
        List<String> marketPlaceIds = this.getShopMarkets(request.getShopId());
        return new NotificationsApi.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentials(authorizationModel))
                .lwaAccessTokenCache(this.getLocalTokenCache())
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }

    public NotificationsApi getNotificationApi(ApiRequest<?> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request);
        List<String> marketPlaceIds = this.getShopMarkets(request.getShopId());
        return new NotificationsApi.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentialsScoped(authorizationModel, "sellingpartnerapi::notifications"))
                .lwaAccessTokenCache(this.getLocalTokenCache())
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }

    public NotificationsApi getNotificationApiAuth(ApiRequest<?> request) {
        return getNotificationsApi(request);
    }

    public FbaInboundApi getFbaInboundApi(ApiRequest<?> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request);
        List<String> marketPlaceIds = this.getShopMarkets(request.getShopId());
        return new FbaInboundApi.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentials(authorizationModel))
                .lwaAccessTokenCache(this.getLocalTokenCache())
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }
 
    public DefaultApi getFinancesDefaultV0Api(ApiRequest<?> apiRequest) {
        List<String> marketPlaceIds = this.getShopMarkets(apiRequest.getShopId());
        return new DefaultApi.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentials(this.getAuthorizationModel(apiRequest)))
                .lwaAccessTokenCache(this.getLocalTokenCache())
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }

    public software.amazon.spapi.api.fulfillment.inbound.v0.FbaInboundApi getFbaInboundApiV0(ApiRequest<?> apiRequest) {
        List<String> marketPlaceIds = this.getShopMarkets(apiRequest.getShopId());
        return new software.amazon.spapi.api.fulfillment.inbound.v0.FbaInboundApi.Builder()
                .lwaAuthorizationCredentials(this.getLWAAuthorizationCredentials(this.getAuthorizationModel(apiRequest)))
                .lwaAccessTokenCache(this.getLocalTokenCache())
                .endpoint(this.getApiEndpoint(marketPlaceIds.get(0)))
                .build();
    }


//============== 数据转换 ==============

    public MultiMarketProductModel convertProduct(Item item) {

        MultiMarketProductModel multiMarketProductModel = new MultiMarketProductModel();

        if (item.getSummaries() != null) {
            item.getSummaries().forEach(itemByMarket -> {
                String market = itemByMarket.getMarketplaceId();
                ProductModel product = multiMarketProductModel.getOrCreate(market);
                product.setMarketId(market);
                product.setProductCode(itemByMarket.getAsin());
                product.setSellerSku(item.getSku());
                product.setFnSku(itemByMarket.getFnSku());
                product.setProductName(itemByMarket.getItemName());
                product.setCategory(itemByMarket.getProductType());
                if (itemByMarket.getMainImage() != null) {
                    ItemImage image = itemByMarket.getMainImage();
                    product.setMainImage(new Image().setUrl(image.getLink()).setWidth(image.getWidth()).setHeight(image.getHeight()));
                }

                product.setStatus(itemByMarket.getStatus().contains(ItemSummaryByMarketplace.StatusEnum.BUYABLE) ? CrossListingStatus.ACTIVATE : CrossListingStatus.PENDING);

                product.setCreateTime(LocalDateTime.ofEpochSecond(itemByMarket.getCreatedDate().toEpochSecond(), 0, ZoneOffset.UTC));
                product.setUpdateTime(LocalDateTime.ofEpochSecond(itemByMarket.getLastUpdatedDate().toEpochSecond(), 0, ZoneOffset.UTC));
            });
        }

        if (MapUtils.isNotEmpty(item.getAttributes())) {
            log.debug("Listing Attributes: sku={}, attrs={}", item.getSku(), item.getAttributes());

            item.getAttributes().forEach((key, val) -> {

                try {
                    List<Map<String, Object>> attrVal = JsonUtils.parseObject(JsonUtils.toJsonString(val), new TypeReference<List<Map<String, Object>>>() {
                    });
                    Map<String, Map<String, Object>> attrByMarket = Maps.newHashMap();
                    Map<String, Object> attrAllMarket = Maps.newHashMap();
                    attrVal.forEach(attr -> {
                        if (attr.containsKey("marketplace_id")) {
                            attrByMarket.put(attr.get("marketplace_id").toString(), attr);
                        } else {
                            attrAllMarket.put(key, attr);
                        }
                    });

                    attrByMarket.forEach((market, attr) -> {
                        ProductModel product = multiMarketProductModel.getOrCreate(market);
                        String attrValStr = "";
                        if (attr.containsKey("value")) {
                            attrValStr = attr.get("value").toString();
                        } else {
                            attrValStr = JsonUtils.toJsonString(attr);
                        }
                        product.addProductAttribute(new ProductAttributeModel().setAttrId(key).setAttrValues(List.of(new ProductAttributeModel.AttrValue().setCustomerValue(attrValStr))));

                        // 特殊属性填充到ProductModel中
                        if ("brand".equalsIgnoreCase(key)) {
                            product.setBrand(attrValStr);
                        }
                        if ("product_description".equalsIgnoreCase(key)) {
                            product.setProductDescription(attrValStr);
                        }

                        // 价格
                        if ("purchasable_offer".equalsIgnoreCase(key)) {
                            product.setPrices(convertPurchaseOfferAttr(attrValStr));
                        }


                    });
                } catch (Exception e) {
                    log.info("Error parse attribute: sku={}, key={}, val={}, error={}", item.getSku(), key, val, e.getMessage());
                }
            });

        }

        if (CollectionUtils.isNotEmpty(item.getIssues())) {
            List<IssueModel> issues = item.getIssues().stream().map(this::convert).collect(Collectors.toList());

            multiMarketProductModel.values().forEach(product -> product.setIssues(issues));
        }

        // fulfillmentAvailability 中是否包含 AMAZON_ 开头的渠道
        boolean isAmzFulfillment = CollectionUtils.isNotEmpty(item.getFulfillmentAvailability()) && item.getFulfillmentAvailability().stream().anyMatch(availability -> availability.getFulfillmentChannelCode().startsWith("AMAZON_"));
        multiMarketProductModel.values().forEach(product -> product.setFulfillType(isAmzFulfillment ? FulfillTypeEnum.FBA : FulfillTypeEnum.FBM));

        if (CollectionUtils.isNotEmpty(item.getOffers())) {
            item.getOffers().forEach(offer -> {
                ProductModel product = multiMarketProductModel.getOrCreate(offer.getMarketplaceId());
                product.addOffer(new PriceModel().setCurrency(offer.getPrice().getCurrencyCode()).setAmount(new BigDecimal(offer.getPrice().getAmount())).setType(offer.getOfferType().getValue()));
            });
        }


        if (CollectionUtils.isNotEmpty(item.getRelationships())) {

            item.getRelationships().forEach(relation -> {
                ProductModel productModel = multiMarketProductModel.get(relation.getMarketplaceId());

                List<RelationShipModel> relationShips = relation.getRelationships().stream().map(r -> {
                    return new RelationShipModel()
                            .setRelationLevel(CollectionUtils.isNotEmpty(r.getChildSkus()) ? RelationLevel.PARENT : RelationLevel.CHILD)
                            .setParentSku(CollectionUtils.isNotEmpty(r.getParentSkus()) ? r.getParentSkus().get(0) : null)
                            .setChildSkus(r.getChildSkus())
                            .setVariantTheme(r.getVariationTheme().getTheme())
                            .setVariantAttributes(r.getVariationTheme().getAttributes());
                }).collect(Collectors.toList());
                productModel.setRelationShips(relationShips);

            });
        }

        return multiMarketProductModel;
    }

    public GetOfferResponse convert(GetOffersResponse r) {
        GetOfferResponse response = new GetOfferResponse();

        GetOffersResult result = r.getPayload();

        response.setMarketId(result.getMarketplaceID())
                .setSellerSku(result.getSKU())
                .setProductCode(result.getASIN());

        if (result.getSummary() != null) {
            Summary summary = result.getSummary();
            response.setTotalOfferCount(summary.getTotalOfferCount())
                    .setLowestPrice(convert(summary.getLowestPrices()))
                    .setBuyBoxPrice(convert(summary.getBuyBoxPrices()));
        }

        if (CollectionUtils.isNotEmpty(result.getOffers())) {
            List<OfferModel> allOffers = Lists.newArrayList();
            result.getOffers().forEach(offer -> {
                if (offer.isMyOffer() != null && offer.isMyOffer()) {
                    response.setMyOffer(convert(offer));
                }
                allOffers.add(convert(offer));
            });
            response.setAllOffers(allOffers);
        }
        return response;
    }

    public OfferModel convert(BuyBoxPrices buyBoxPrices) {
        if (buyBoxPrices == null) {
            return null;
        }
        return buyBoxPrices.stream().filter(p -> p.getCondition().equals(ItemCondition.NEW.getValue())).map(
                p -> {
                    return new OfferModel()
                            .setListingPrice(convert(p.getListingPrice()))
                            .setLandedPrice(convert(p.getLandedPrice()))
                            .setShipPrice(convert(p.getShipping()))
                            .setSellerId(p.getSellerId());
                }

        ).findFirst().get();

    }

    public List<OfferModel> convert(LowestPrices prices) {
        if (prices == null) {
            return Lists.newArrayList();
        }
        return prices.stream().filter(p -> p.getCondition().equals(ItemCondition.NEW.getValue())).map(
                p -> {
                    return new OfferModel().setFulfillType("Amazon".equals(p.getFulfillmentChannel()) ? FulfillTypeEnum.FBA : FulfillTypeEnum.FBM)
                            .setListingPrice(convert(p.getListingPrice()))
                            .setLandedPrice(convert(p.getLandedPrice()))
                            .setShipPrice(convert(p.getShipping()));
                }

        ).collect(Collectors.toList());

    }

    public PricingModel convert(MoneyType price) {
        return new PricingModel().setCurrency(price.getCurrencyCode())
                .setAmount(price.getAmount());
    }

    public OfferModel convert(OfferDetail offer) {
        return new OfferModel()
                .setFulfillType(offer.isIsFulfilledByAmazon() ? FulfillTypeEnum.FBA : FulfillTypeEnum.FBM)
                .setListingPrice(convert(offer.getListingPrice()))
                .setShipPrice(convert(offer.getShipping()))
                .setSellerId(offer.getSellerId())
                .setBuyBox(offer.isIsBuyBoxWinner());

    }

    public IssueModel convert(Issue issue) {
        return new IssueModel()
                .setCode(issue.getCode())
                .setMessage(issue.getMessage())
                .setSeverity(issue.getSeverity().getValue())
                .setEnforcementActions(issue.getEnforcements() != null ? issue.getEnforcements().getActions().stream().map(IssueEnforcementAction::getAction).collect(Collectors.toList()) : null);
    }

    public String convert(FulfillTypeEnum fulfillType) {
        return FulfillTypeEnum.FBA.equals(fulfillType) ? "AFN" : "MFN";
    }

    public CrossOrderStatus convert(Order.OrderStatusEnum status) {
        return CrossOrderStatus.of(status.getValue());
    }

    public List<PriceModel> convertPurchaseOfferAttr(String attValStr) {
        if (StringUtils.isBlank(attValStr)) {
            return Lists.newArrayList();
        }
        AmzPurchasableOfferModel amzPurchasableOfferModel = JsonUtils.parseObject(attValStr, AmzPurchasableOfferModel.class);
        if (amzPurchasableOfferModel == null) {
            return Lists.newArrayList();
        }

        return List.of(new PriceModel()
                .setCurrency(amzPurchasableOfferModel.getCurrency())
                .setAmount(BigDecimal.valueOf(amzPurchasableOfferModel.getPrice()))
                .setStartAt(formatToTimestamp(amzPurchasableOfferModel.getStartAt()))
                .setEndAt(formatToTimestamp(amzPurchasableOfferModel.getEndAt()))

        );
    }

    public SetPackingInformationRequest convert(AmzSetPackingInfoRequest request) {

        List<PackageGroupingInput> groupingInputs = request.getOption().getGroupItems().entrySet().stream().map(item -> {
            return new PackageGroupingInput()
                    .packingGroupId(item.getKey())
                    .boxes(item.getValue().stream().map(box -> new BoxInput()
                            .quantity(box.getBoxQuantity())
                            .contentInformationSource(BoxContentInformationSource.BOX_CONTENT_PROVIDED)
                            .items(List.of(new ItemInput()
                                    .msku(box.getMsku())
                                    .quantity(box.getQuantityInBox())
                                    .labelOwner(LabelOwner.SELLER)
                                    .prepOwner(PrepOwner.SELLER)))
                            .weight(new Weight().value(new BigDecimal(box.getBoxWeight())).unit(UnitOfWeight.KG))
                            .dimensions(new Dimensions().height(BigDecimal.valueOf(box.getBoxHeight()))
                                    .width(BigDecimal.valueOf(box.getBoxWidth()))
                                    .length(BigDecimal.valueOf(box.getBoxLength()))
                                    .unitOfMeasurement(UnitOfMeasurement.CM))).toList()
                    );
        }).toList();

        return new SetPackingInformationRequest().packageGroupings(groupingInputs);
    }

    public Long formatToTimestampYYYYMMDD(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        try {
            return DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.parse(dateStr).toInstant().getEpochSecond();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Long formatToTimestamp(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        return OffsetDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toEpochSecond();
    }

    public String formatToUTC(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(timestamp), ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public org.threeten.bp.OffsetDateTime convertToOffsetDateTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return org.threeten.bp.OffsetDateTime.ofInstant(org.threeten.bp.Instant.ofEpochSecond(timestamp), org.threeten.bp.ZoneOffset.UTC);
    }

    public GenerateTransportationOptionsRequest convert(AmzTransportOptionGenerateRequest request) {
        GenerateTransportationOptionsRequest r = new GenerateTransportationOptionsRequest();
        r.setPlacementOptionId(request.getPlacementOptionId());
        r.setShipmentTransportationConfigurations(request.getShipmentIds().stream()
                .map(id -> new ShipmentTransportationConfiguration()
                        .shipmentId(id)
                        .readyToShipWindow(new WindowInput().start(convertToOffsetDateTime(formatToTimestampYYYYMMDD(request.getShipmentDate()))))
                )
                .collect(Collectors.toList()));

        return r;
    }

    public ConfirmTransportationOptionsRequest convert(AmzConfirmTransportOptionRequest r) {
        ConfirmTransportationOptionsRequest request = new ConfirmTransportationOptionsRequest();

        request.setTransportationSelections(r.getShipments().stream()
                .map(shipment ->
                        new TransportationSelection()
                                .shipmentId(shipment.getShipmentId())
                                .transportationOptionId(shipment.getTransportationOptionId())
                ).collect(Collectors.toList())
        );

        return request;

    }


}




