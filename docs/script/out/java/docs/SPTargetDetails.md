

# SPTargetDetails

## oneOf schemas
* [KeywordTarget1](KeywordTarget1.md)
* [LocationTarget1](LocationTarget1.md)
* [ProductCategoryTarget1](ProductCategoryTarget1.md)
* [ProductTarget1](ProductTarget1.md)
* [ThemeTarget1](ThemeTarget1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.SPTargetDetails;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.KeywordTarget1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.LocationTarget1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.ProductCategoryTarget1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.ProductTarget1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.ThemeTarget1;

public class Example {
    public static void main(String[] args) {
        SPTargetDetails exampleSPTargetDetails = new SPTargetDetails();

        // create a new KeywordTarget1
        KeywordTarget1 exampleKeywordTarget1 = new KeywordTarget1();
        // set SPTargetDetails to KeywordTarget1
        exampleSPTargetDetails.setActualInstance(exampleKeywordTarget1);
        // to get back the KeywordTarget1 set earlier
        KeywordTarget1 testKeywordTarget1 = (KeywordTarget1) exampleSPTargetDetails.getActualInstance();

        // create a new LocationTarget1
        LocationTarget1 exampleLocationTarget1 = new LocationTarget1();
        // set SPTargetDetails to LocationTarget1
        exampleSPTargetDetails.setActualInstance(exampleLocationTarget1);
        // to get back the LocationTarget1 set earlier
        LocationTarget1 testLocationTarget1 = (LocationTarget1) exampleSPTargetDetails.getActualInstance();

        // create a new ProductCategoryTarget1
        ProductCategoryTarget1 exampleProductCategoryTarget1 = new ProductCategoryTarget1();
        // set SPTargetDetails to ProductCategoryTarget1
        exampleSPTargetDetails.setActualInstance(exampleProductCategoryTarget1);
        // to get back the ProductCategoryTarget1 set earlier
        ProductCategoryTarget1 testProductCategoryTarget1 = (ProductCategoryTarget1) exampleSPTargetDetails.getActualInstance();

        // create a new ProductTarget1
        ProductTarget1 exampleProductTarget1 = new ProductTarget1();
        // set SPTargetDetails to ProductTarget1
        exampleSPTargetDetails.setActualInstance(exampleProductTarget1);
        // to get back the ProductTarget1 set earlier
        ProductTarget1 testProductTarget1 = (ProductTarget1) exampleSPTargetDetails.getActualInstance();

        // create a new ThemeTarget1
        ThemeTarget1 exampleThemeTarget1 = new ThemeTarget1();
        // set SPTargetDetails to ThemeTarget1
        exampleSPTargetDetails.setActualInstance(exampleThemeTarget1);
        // to get back the ThemeTarget1 set earlier
        ThemeTarget1 testThemeTarget1 = (ThemeTarget1) exampleSPTargetDetails.getActualInstance();
    }
}
```


