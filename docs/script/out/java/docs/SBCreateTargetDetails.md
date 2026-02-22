

# SBCreateTargetDetails

## oneOf schemas
* [KeywordTarget](KeywordTarget.md)
* [ProductCategoryTarget](ProductCategoryTarget.md)
* [ProductTarget](ProductTarget.md)
* [ThemeTarget](ThemeTarget.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.SBCreateTargetDetails;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.KeywordTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.ProductCategoryTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.ProductTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.ThemeTarget;

public class Example {
    public static void main(String[] args) {
        SBCreateTargetDetails exampleSBCreateTargetDetails = new SBCreateTargetDetails();

        // create a new KeywordTarget
        KeywordTarget exampleKeywordTarget = new KeywordTarget();
        // set SBCreateTargetDetails to KeywordTarget
        exampleSBCreateTargetDetails.setActualInstance(exampleKeywordTarget);
        // to get back the KeywordTarget set earlier
        KeywordTarget testKeywordTarget = (KeywordTarget) exampleSBCreateTargetDetails.getActualInstance();

        // create a new ProductCategoryTarget
        ProductCategoryTarget exampleProductCategoryTarget = new ProductCategoryTarget();
        // set SBCreateTargetDetails to ProductCategoryTarget
        exampleSBCreateTargetDetails.setActualInstance(exampleProductCategoryTarget);
        // to get back the ProductCategoryTarget set earlier
        ProductCategoryTarget testProductCategoryTarget = (ProductCategoryTarget) exampleSBCreateTargetDetails.getActualInstance();

        // create a new ProductTarget
        ProductTarget exampleProductTarget = new ProductTarget();
        // set SBCreateTargetDetails to ProductTarget
        exampleSBCreateTargetDetails.setActualInstance(exampleProductTarget);
        // to get back the ProductTarget set earlier
        ProductTarget testProductTarget = (ProductTarget) exampleSBCreateTargetDetails.getActualInstance();

        // create a new ThemeTarget
        ThemeTarget exampleThemeTarget = new ThemeTarget();
        // set SBCreateTargetDetails to ThemeTarget
        exampleSBCreateTargetDetails.setActualInstance(exampleThemeTarget);
        // to get back the ThemeTarget set earlier
        ThemeTarget testThemeTarget = (ThemeTarget) exampleSBCreateTargetDetails.getActualInstance();
    }
}
```


