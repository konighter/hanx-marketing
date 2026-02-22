

# SPCreateAdExtensionSettings

## oneOf schemas
* [PromptExtension1](PromptExtension1.md)
* [VideoExtension1](VideoExtension1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.SPCreateAdExtensionSettings;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.PromptExtension1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.VideoExtension1;

public class Example {
    public static void main(String[] args) {
        SPCreateAdExtensionSettings exampleSPCreateAdExtensionSettings = new SPCreateAdExtensionSettings();

        // create a new PromptExtension1
        PromptExtension1 examplePromptExtension1 = new PromptExtension1();
        // set SPCreateAdExtensionSettings to PromptExtension1
        exampleSPCreateAdExtensionSettings.setActualInstance(examplePromptExtension1);
        // to get back the PromptExtension1 set earlier
        PromptExtension1 testPromptExtension1 = (PromptExtension1) exampleSPCreateAdExtensionSettings.getActualInstance();

        // create a new VideoExtension1
        VideoExtension1 exampleVideoExtension1 = new VideoExtension1();
        // set SPCreateAdExtensionSettings to VideoExtension1
        exampleSPCreateAdExtensionSettings.setActualInstance(exampleVideoExtension1);
        // to get back the VideoExtension1 set earlier
        VideoExtension1 testVideoExtension1 = (VideoExtension1) exampleSPCreateAdExtensionSettings.getActualInstance();
    }
}
```


