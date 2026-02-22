

# CreateAssetBasedCreativeCallToAction

## oneOf schemas
* [AssetBasedCreativeCallToActionSettings1](AssetBasedCreativeCallToActionSettings1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.CreateAssetBasedCreativeCallToAction;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AssetBasedCreativeCallToActionSettings1;

public class Example {
    public static void main(String[] args) {
        CreateAssetBasedCreativeCallToAction exampleCreateAssetBasedCreativeCallToAction = new CreateAssetBasedCreativeCallToAction();

        // create a new AssetBasedCreativeCallToActionSettings1
        AssetBasedCreativeCallToActionSettings1 exampleAssetBasedCreativeCallToActionSettings1 = new AssetBasedCreativeCallToActionSettings1();
        // set CreateAssetBasedCreativeCallToAction to AssetBasedCreativeCallToActionSettings1
        exampleCreateAssetBasedCreativeCallToAction.setActualInstance(exampleAssetBasedCreativeCallToActionSettings1);
        // to get back the AssetBasedCreativeCallToActionSettings1 set earlier
        AssetBasedCreativeCallToActionSettings1 testAssetBasedCreativeCallToActionSettings1 = (AssetBasedCreativeCallToActionSettings1) exampleCreateAssetBasedCreativeCallToAction.getActualInstance();
    }
}
```


