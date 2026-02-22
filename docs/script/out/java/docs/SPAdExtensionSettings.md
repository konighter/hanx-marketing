

# SPAdExtensionSettings

## oneOf schemas
* [PromptExtension](PromptExtension.md)
* [VideoExtension](VideoExtension.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.SPAdExtensionSettings;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.PromptExtension;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.VideoExtension;

public class Example {
    public static void main(String[] args) {
        SPAdExtensionSettings exampleSPAdExtensionSettings = new SPAdExtensionSettings();

        // create a new PromptExtension
        PromptExtension examplePromptExtension = new PromptExtension();
        // set SPAdExtensionSettings to PromptExtension
        exampleSPAdExtensionSettings.setActualInstance(examplePromptExtension);
        // to get back the PromptExtension set earlier
        PromptExtension testPromptExtension = (PromptExtension) exampleSPAdExtensionSettings.getActualInstance();

        // create a new VideoExtension
        VideoExtension exampleVideoExtension = new VideoExtension();
        // set SPAdExtensionSettings to VideoExtension
        exampleSPAdExtensionSettings.setActualInstance(exampleVideoExtension);
        // to get back the VideoExtension set earlier
        VideoExtension testVideoExtension = (VideoExtension) exampleSPAdExtensionSettings.getActualInstance();
    }
}
```


