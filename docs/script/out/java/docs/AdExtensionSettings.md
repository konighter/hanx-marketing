

# AdExtensionSettings

## oneOf schemas
* [PromptExtension](PromptExtension.md)
* [VideoExtension](VideoExtension.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AdExtensionSettings;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.PromptExtension;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.VideoExtension;

public class Example {
    public static void main(String[] args) {
        AdExtensionSettings exampleAdExtensionSettings = new AdExtensionSettings();

        // create a new PromptExtension
        PromptExtension examplePromptExtension = new PromptExtension();
        // set AdExtensionSettings to PromptExtension
        exampleAdExtensionSettings.setActualInstance(examplePromptExtension);
        // to get back the PromptExtension set earlier
        PromptExtension testPromptExtension = (PromptExtension) exampleAdExtensionSettings.getActualInstance();

        // create a new VideoExtension
        VideoExtension exampleVideoExtension = new VideoExtension();
        // set AdExtensionSettings to VideoExtension
        exampleAdExtensionSettings.setActualInstance(exampleVideoExtension);
        // to get back the VideoExtension set earlier
        VideoExtension testVideoExtension = (VideoExtension) exampleAdExtensionSettings.getActualInstance();
    }
}
```


