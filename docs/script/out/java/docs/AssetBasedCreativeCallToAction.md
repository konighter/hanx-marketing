

# AssetBasedCreativeCallToAction

## oneOf schemas
* [AssetBasedCreativeCallToActionSettings](AssetBasedCreativeCallToActionSettings.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AssetBasedCreativeCallToAction;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AssetBasedCreativeCallToActionSettings;

public class Example {
    public static void main(String[] args) {
        AssetBasedCreativeCallToAction exampleAssetBasedCreativeCallToAction = new AssetBasedCreativeCallToAction();

        // create a new AssetBasedCreativeCallToActionSettings
        AssetBasedCreativeCallToActionSettings exampleAssetBasedCreativeCallToActionSettings = new AssetBasedCreativeCallToActionSettings();
        // set AssetBasedCreativeCallToAction to AssetBasedCreativeCallToActionSettings
        exampleAssetBasedCreativeCallToAction.setActualInstance(exampleAssetBasedCreativeCallToActionSettings);
        // to get back the AssetBasedCreativeCallToActionSettings set earlier
        AssetBasedCreativeCallToActionSettings testAssetBasedCreativeCallToActionSettings = (AssetBasedCreativeCallToActionSettings) exampleAssetBasedCreativeCallToAction.getActualInstance();
    }
}
```


