

# SBTargetDetails

## oneOf schemas
* [KeywordTarget1](KeywordTarget1.md)
* [ProductCategoryTarget1](ProductCategoryTarget1.md)
* [ProductTarget1](ProductTarget1.md)
* [ThemeTarget1](ThemeTarget1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.SBTargetDetails;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.KeywordTarget1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.ProductCategoryTarget1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.ProductTarget1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.ThemeTarget1;

public class Example {
    public static void main(String[] args) {
        SBTargetDetails exampleSBTargetDetails = new SBTargetDetails();

        // create a new KeywordTarget1
        KeywordTarget1 exampleKeywordTarget1 = new KeywordTarget1();
        // set SBTargetDetails to KeywordTarget1
        exampleSBTargetDetails.setActualInstance(exampleKeywordTarget1);
        // to get back the KeywordTarget1 set earlier
        KeywordTarget1 testKeywordTarget1 = (KeywordTarget1) exampleSBTargetDetails.getActualInstance();

        // create a new ProductCategoryTarget1
        ProductCategoryTarget1 exampleProductCategoryTarget1 = new ProductCategoryTarget1();
        // set SBTargetDetails to ProductCategoryTarget1
        exampleSBTargetDetails.setActualInstance(exampleProductCategoryTarget1);
        // to get back the ProductCategoryTarget1 set earlier
        ProductCategoryTarget1 testProductCategoryTarget1 = (ProductCategoryTarget1) exampleSBTargetDetails.getActualInstance();

        // create a new ProductTarget1
        ProductTarget1 exampleProductTarget1 = new ProductTarget1();
        // set SBTargetDetails to ProductTarget1
        exampleSBTargetDetails.setActualInstance(exampleProductTarget1);
        // to get back the ProductTarget1 set earlier
        ProductTarget1 testProductTarget1 = (ProductTarget1) exampleSBTargetDetails.getActualInstance();

        // create a new ThemeTarget1
        ThemeTarget1 exampleThemeTarget1 = new ThemeTarget1();
        // set SBTargetDetails to ThemeTarget1
        exampleSBTargetDetails.setActualInstance(exampleThemeTarget1);
        // to get back the ThemeTarget1 set earlier
        ThemeTarget1 testThemeTarget1 = (ThemeTarget1) exampleSBTargetDetails.getActualInstance();
    }
}
```


