

# BrandStoreCallToAction

## oneOf schemas
* [BrandStoreCallToActionSettings](BrandStoreCallToActionSettings.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.BrandStoreCallToAction;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.BrandStoreCallToActionSettings;

public class Example {
    public static void main(String[] args) {
        BrandStoreCallToAction exampleBrandStoreCallToAction = new BrandStoreCallToAction();

        // create a new BrandStoreCallToActionSettings
        BrandStoreCallToActionSettings exampleBrandStoreCallToActionSettings = new BrandStoreCallToActionSettings();
        // set BrandStoreCallToAction to BrandStoreCallToActionSettings
        exampleBrandStoreCallToAction.setActualInstance(exampleBrandStoreCallToActionSettings);
        // to get back the BrandStoreCallToActionSettings set earlier
        BrandStoreCallToActionSettings testBrandStoreCallToActionSettings = (BrandStoreCallToActionSettings) exampleBrandStoreCallToAction.getActualInstance();
    }
}
```


