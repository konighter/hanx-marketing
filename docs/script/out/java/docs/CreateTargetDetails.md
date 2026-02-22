

# CreateTargetDetails

## oneOf schemas
* [AdInitiationTarget](AdInitiationTarget.md)
* [AdPlayerSizeTarget](AdPlayerSizeTarget.md)
* [AppTarget](AppTarget.md)
* [AudienceTarget](AudienceTarget.md)
* [BrandSafetyCategoryTarget](BrandSafetyCategoryTarget.md)
* [BrandSafetyTierTarget](BrandSafetyTierTarget.md)
* [ContentCategoryTarget](ContentCategoryTarget.md)
* [ContentGenreTarget](ContentGenreTarget.md)
* [ContentInstreamPositionTarget](ContentInstreamPositionTarget.md)
* [ContentOutstreamPositionTarget](ContentOutstreamPositionTarget.md)
* [ContentRatingTarget](ContentRatingTarget.md)
* [DayPartTarget](DayPartTarget.md)
* [DeviceTarget](DeviceTarget.md)
* [DomainTarget](DomainTarget.md)
* [FoldPositionTarget](FoldPositionTarget.md)
* [InventorySourceTarget](InventorySourceTarget.md)
* [KeywordTarget](KeywordTarget.md)
* [LocationTarget](LocationTarget.md)
* [NativeContentPositionTarget](NativeContentPositionTarget.md)
* [PlacementTypeTarget](PlacementTypeTarget.md)
* [ProductAudienceTarget](ProductAudienceTarget.md)
* [ProductCategoryTarget](ProductCategoryTarget.md)
* [ProductTarget](ProductTarget.md)
* [ThemeTarget](ThemeTarget.md)
* [ThirdPartyTarget](ThirdPartyTarget.md)
* [VideoAdFormatTarget](VideoAdFormatTarget.md)
* [VideoContentDurationTarget](VideoContentDurationTarget.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.CreateTargetDetails;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AdInitiationTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AdPlayerSizeTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AppTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AudienceTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.BrandSafetyCategoryTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.BrandSafetyTierTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ContentCategoryTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ContentGenreTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ContentInstreamPositionTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ContentOutstreamPositionTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ContentRatingTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DayPartTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DeviceTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DomainTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.FoldPositionTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.InventorySourceTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.KeywordTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.LocationTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.NativeContentPositionTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.PlacementTypeTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ProductAudienceTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ProductCategoryTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ProductTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ThemeTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ThirdPartyTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.VideoAdFormatTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.VideoContentDurationTarget;

public class Example {
    public static void main(String[] args) {
        CreateTargetDetails exampleCreateTargetDetails = new CreateTargetDetails();

        // create a new AdInitiationTarget
        AdInitiationTarget exampleAdInitiationTarget = new AdInitiationTarget();
        // set CreateTargetDetails to AdInitiationTarget
        exampleCreateTargetDetails.setActualInstance(exampleAdInitiationTarget);
        // to get back the AdInitiationTarget set earlier
        AdInitiationTarget testAdInitiationTarget = (AdInitiationTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new AdPlayerSizeTarget
        AdPlayerSizeTarget exampleAdPlayerSizeTarget = new AdPlayerSizeTarget();
        // set CreateTargetDetails to AdPlayerSizeTarget
        exampleCreateTargetDetails.setActualInstance(exampleAdPlayerSizeTarget);
        // to get back the AdPlayerSizeTarget set earlier
        AdPlayerSizeTarget testAdPlayerSizeTarget = (AdPlayerSizeTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new AppTarget
        AppTarget exampleAppTarget = new AppTarget();
        // set CreateTargetDetails to AppTarget
        exampleCreateTargetDetails.setActualInstance(exampleAppTarget);
        // to get back the AppTarget set earlier
        AppTarget testAppTarget = (AppTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new AudienceTarget
        AudienceTarget exampleAudienceTarget = new AudienceTarget();
        // set CreateTargetDetails to AudienceTarget
        exampleCreateTargetDetails.setActualInstance(exampleAudienceTarget);
        // to get back the AudienceTarget set earlier
        AudienceTarget testAudienceTarget = (AudienceTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new BrandSafetyCategoryTarget
        BrandSafetyCategoryTarget exampleBrandSafetyCategoryTarget = new BrandSafetyCategoryTarget();
        // set CreateTargetDetails to BrandSafetyCategoryTarget
        exampleCreateTargetDetails.setActualInstance(exampleBrandSafetyCategoryTarget);
        // to get back the BrandSafetyCategoryTarget set earlier
        BrandSafetyCategoryTarget testBrandSafetyCategoryTarget = (BrandSafetyCategoryTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new BrandSafetyTierTarget
        BrandSafetyTierTarget exampleBrandSafetyTierTarget = new BrandSafetyTierTarget();
        // set CreateTargetDetails to BrandSafetyTierTarget
        exampleCreateTargetDetails.setActualInstance(exampleBrandSafetyTierTarget);
        // to get back the BrandSafetyTierTarget set earlier
        BrandSafetyTierTarget testBrandSafetyTierTarget = (BrandSafetyTierTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new ContentCategoryTarget
        ContentCategoryTarget exampleContentCategoryTarget = new ContentCategoryTarget();
        // set CreateTargetDetails to ContentCategoryTarget
        exampleCreateTargetDetails.setActualInstance(exampleContentCategoryTarget);
        // to get back the ContentCategoryTarget set earlier
        ContentCategoryTarget testContentCategoryTarget = (ContentCategoryTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new ContentGenreTarget
        ContentGenreTarget exampleContentGenreTarget = new ContentGenreTarget();
        // set CreateTargetDetails to ContentGenreTarget
        exampleCreateTargetDetails.setActualInstance(exampleContentGenreTarget);
        // to get back the ContentGenreTarget set earlier
        ContentGenreTarget testContentGenreTarget = (ContentGenreTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new ContentInstreamPositionTarget
        ContentInstreamPositionTarget exampleContentInstreamPositionTarget = new ContentInstreamPositionTarget();
        // set CreateTargetDetails to ContentInstreamPositionTarget
        exampleCreateTargetDetails.setActualInstance(exampleContentInstreamPositionTarget);
        // to get back the ContentInstreamPositionTarget set earlier
        ContentInstreamPositionTarget testContentInstreamPositionTarget = (ContentInstreamPositionTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new ContentOutstreamPositionTarget
        ContentOutstreamPositionTarget exampleContentOutstreamPositionTarget = new ContentOutstreamPositionTarget();
        // set CreateTargetDetails to ContentOutstreamPositionTarget
        exampleCreateTargetDetails.setActualInstance(exampleContentOutstreamPositionTarget);
        // to get back the ContentOutstreamPositionTarget set earlier
        ContentOutstreamPositionTarget testContentOutstreamPositionTarget = (ContentOutstreamPositionTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new ContentRatingTarget
        ContentRatingTarget exampleContentRatingTarget = new ContentRatingTarget();
        // set CreateTargetDetails to ContentRatingTarget
        exampleCreateTargetDetails.setActualInstance(exampleContentRatingTarget);
        // to get back the ContentRatingTarget set earlier
        ContentRatingTarget testContentRatingTarget = (ContentRatingTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new DayPartTarget
        DayPartTarget exampleDayPartTarget = new DayPartTarget();
        // set CreateTargetDetails to DayPartTarget
        exampleCreateTargetDetails.setActualInstance(exampleDayPartTarget);
        // to get back the DayPartTarget set earlier
        DayPartTarget testDayPartTarget = (DayPartTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new DeviceTarget
        DeviceTarget exampleDeviceTarget = new DeviceTarget();
        // set CreateTargetDetails to DeviceTarget
        exampleCreateTargetDetails.setActualInstance(exampleDeviceTarget);
        // to get back the DeviceTarget set earlier
        DeviceTarget testDeviceTarget = (DeviceTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new DomainTarget
        DomainTarget exampleDomainTarget = new DomainTarget();
        // set CreateTargetDetails to DomainTarget
        exampleCreateTargetDetails.setActualInstance(exampleDomainTarget);
        // to get back the DomainTarget set earlier
        DomainTarget testDomainTarget = (DomainTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new FoldPositionTarget
        FoldPositionTarget exampleFoldPositionTarget = new FoldPositionTarget();
        // set CreateTargetDetails to FoldPositionTarget
        exampleCreateTargetDetails.setActualInstance(exampleFoldPositionTarget);
        // to get back the FoldPositionTarget set earlier
        FoldPositionTarget testFoldPositionTarget = (FoldPositionTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new InventorySourceTarget
        InventorySourceTarget exampleInventorySourceTarget = new InventorySourceTarget();
        // set CreateTargetDetails to InventorySourceTarget
        exampleCreateTargetDetails.setActualInstance(exampleInventorySourceTarget);
        // to get back the InventorySourceTarget set earlier
        InventorySourceTarget testInventorySourceTarget = (InventorySourceTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new KeywordTarget
        KeywordTarget exampleKeywordTarget = new KeywordTarget();
        // set CreateTargetDetails to KeywordTarget
        exampleCreateTargetDetails.setActualInstance(exampleKeywordTarget);
        // to get back the KeywordTarget set earlier
        KeywordTarget testKeywordTarget = (KeywordTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new LocationTarget
        LocationTarget exampleLocationTarget = new LocationTarget();
        // set CreateTargetDetails to LocationTarget
        exampleCreateTargetDetails.setActualInstance(exampleLocationTarget);
        // to get back the LocationTarget set earlier
        LocationTarget testLocationTarget = (LocationTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new NativeContentPositionTarget
        NativeContentPositionTarget exampleNativeContentPositionTarget = new NativeContentPositionTarget();
        // set CreateTargetDetails to NativeContentPositionTarget
        exampleCreateTargetDetails.setActualInstance(exampleNativeContentPositionTarget);
        // to get back the NativeContentPositionTarget set earlier
        NativeContentPositionTarget testNativeContentPositionTarget = (NativeContentPositionTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new PlacementTypeTarget
        PlacementTypeTarget examplePlacementTypeTarget = new PlacementTypeTarget();
        // set CreateTargetDetails to PlacementTypeTarget
        exampleCreateTargetDetails.setActualInstance(examplePlacementTypeTarget);
        // to get back the PlacementTypeTarget set earlier
        PlacementTypeTarget testPlacementTypeTarget = (PlacementTypeTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new ProductAudienceTarget
        ProductAudienceTarget exampleProductAudienceTarget = new ProductAudienceTarget();
        // set CreateTargetDetails to ProductAudienceTarget
        exampleCreateTargetDetails.setActualInstance(exampleProductAudienceTarget);
        // to get back the ProductAudienceTarget set earlier
        ProductAudienceTarget testProductAudienceTarget = (ProductAudienceTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new ProductCategoryTarget
        ProductCategoryTarget exampleProductCategoryTarget = new ProductCategoryTarget();
        // set CreateTargetDetails to ProductCategoryTarget
        exampleCreateTargetDetails.setActualInstance(exampleProductCategoryTarget);
        // to get back the ProductCategoryTarget set earlier
        ProductCategoryTarget testProductCategoryTarget = (ProductCategoryTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new ProductTarget
        ProductTarget exampleProductTarget = new ProductTarget();
        // set CreateTargetDetails to ProductTarget
        exampleCreateTargetDetails.setActualInstance(exampleProductTarget);
        // to get back the ProductTarget set earlier
        ProductTarget testProductTarget = (ProductTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new ThemeTarget
        ThemeTarget exampleThemeTarget = new ThemeTarget();
        // set CreateTargetDetails to ThemeTarget
        exampleCreateTargetDetails.setActualInstance(exampleThemeTarget);
        // to get back the ThemeTarget set earlier
        ThemeTarget testThemeTarget = (ThemeTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new ThirdPartyTarget
        ThirdPartyTarget exampleThirdPartyTarget = new ThirdPartyTarget();
        // set CreateTargetDetails to ThirdPartyTarget
        exampleCreateTargetDetails.setActualInstance(exampleThirdPartyTarget);
        // to get back the ThirdPartyTarget set earlier
        ThirdPartyTarget testThirdPartyTarget = (ThirdPartyTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new VideoAdFormatTarget
        VideoAdFormatTarget exampleVideoAdFormatTarget = new VideoAdFormatTarget();
        // set CreateTargetDetails to VideoAdFormatTarget
        exampleCreateTargetDetails.setActualInstance(exampleVideoAdFormatTarget);
        // to get back the VideoAdFormatTarget set earlier
        VideoAdFormatTarget testVideoAdFormatTarget = (VideoAdFormatTarget) exampleCreateTargetDetails.getActualInstance();

        // create a new VideoContentDurationTarget
        VideoContentDurationTarget exampleVideoContentDurationTarget = new VideoContentDurationTarget();
        // set CreateTargetDetails to VideoContentDurationTarget
        exampleCreateTargetDetails.setActualInstance(exampleVideoContentDurationTarget);
        // to get back the VideoContentDurationTarget set earlier
        VideoContentDurationTarget testVideoContentDurationTarget = (VideoContentDurationTarget) exampleCreateTargetDetails.getActualInstance();
    }
}
```


