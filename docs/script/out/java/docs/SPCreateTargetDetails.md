

# SPCreateTargetDetails

## oneOf schemas
* [KeywordTarget](KeywordTarget.md)
* [LocationTarget](LocationTarget.md)
* [ProductCategoryTarget](ProductCategoryTarget.md)
* [ProductTarget](ProductTarget.md)
* [ThemeTarget](ThemeTarget.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.SPCreateTargetDetails;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.KeywordTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.LocationTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.ProductCategoryTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.ProductTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.ThemeTarget;

public class Example {
    public static void main(String[] args) {
        SPCreateTargetDetails exampleSPCreateTargetDetails = new SPCreateTargetDetails();

        // create a new KeywordTarget
        KeywordTarget exampleKeywordTarget = new KeywordTarget();
        // set SPCreateTargetDetails to KeywordTarget
        exampleSPCreateTargetDetails.setActualInstance(exampleKeywordTarget);
        // to get back the KeywordTarget set earlier
        KeywordTarget testKeywordTarget = (KeywordTarget) exampleSPCreateTargetDetails.getActualInstance();

        // create a new LocationTarget
        LocationTarget exampleLocationTarget = new LocationTarget();
        // set SPCreateTargetDetails to LocationTarget
        exampleSPCreateTargetDetails.setActualInstance(exampleLocationTarget);
        // to get back the LocationTarget set earlier
        LocationTarget testLocationTarget = (LocationTarget) exampleSPCreateTargetDetails.getActualInstance();

        // create a new ProductCategoryTarget
        ProductCategoryTarget exampleProductCategoryTarget = new ProductCategoryTarget();
        // set SPCreateTargetDetails to ProductCategoryTarget
        exampleSPCreateTargetDetails.setActualInstance(exampleProductCategoryTarget);
        // to get back the ProductCategoryTarget set earlier
        ProductCategoryTarget testProductCategoryTarget = (ProductCategoryTarget) exampleSPCreateTargetDetails.getActualInstance();

        // create a new ProductTarget
        ProductTarget exampleProductTarget = new ProductTarget();
        // set SPCreateTargetDetails to ProductTarget
        exampleSPCreateTargetDetails.setActualInstance(exampleProductTarget);
        // to get back the ProductTarget set earlier
        ProductTarget testProductTarget = (ProductTarget) exampleSPCreateTargetDetails.getActualInstance();

        // create a new ThemeTarget
        ThemeTarget exampleThemeTarget = new ThemeTarget();
        // set SPCreateTargetDetails to ThemeTarget
        exampleSPCreateTargetDetails.setActualInstance(exampleThemeTarget);
        // to get back the ThemeTarget set earlier
        ThemeTarget testThemeTarget = (ThemeTarget) exampleSPCreateTargetDetails.getActualInstance();
    }
}
```


