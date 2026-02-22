

# CreateBrandStoreCallToAction

## oneOf schemas
* [BrandStoreCallToActionSettings1](BrandStoreCallToActionSettings1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.CreateBrandStoreCallToAction;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.BrandStoreCallToActionSettings1;

public class Example {
    public static void main(String[] args) {
        CreateBrandStoreCallToAction exampleCreateBrandStoreCallToAction = new CreateBrandStoreCallToAction();

        // create a new BrandStoreCallToActionSettings1
        BrandStoreCallToActionSettings1 exampleBrandStoreCallToActionSettings1 = new BrandStoreCallToActionSettings1();
        // set CreateBrandStoreCallToAction to BrandStoreCallToActionSettings1
        exampleCreateBrandStoreCallToAction.setActualInstance(exampleBrandStoreCallToActionSettings1);
        // to get back the BrandStoreCallToActionSettings1 set earlier
        BrandStoreCallToActionSettings1 testBrandStoreCallToActionSettings1 = (BrandStoreCallToActionSettings1) exampleCreateBrandStoreCallToAction.getActualInstance();
    }
}
```


