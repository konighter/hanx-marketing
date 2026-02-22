

# CreateAdExtensionSettings

## oneOf schemas
* [PromptExtension1](PromptExtension1.md)
* [VideoExtension1](VideoExtension1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.CreateAdExtensionSettings;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.PromptExtension1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.VideoExtension1;

public class Example {
    public static void main(String[] args) {
        CreateAdExtensionSettings exampleCreateAdExtensionSettings = new CreateAdExtensionSettings();

        // create a new PromptExtension1
        PromptExtension1 examplePromptExtension1 = new PromptExtension1();
        // set CreateAdExtensionSettings to PromptExtension1
        exampleCreateAdExtensionSettings.setActualInstance(examplePromptExtension1);
        // to get back the PromptExtension1 set earlier
        PromptExtension1 testPromptExtension1 = (PromptExtension1) exampleCreateAdExtensionSettings.getActualInstance();

        // create a new VideoExtension1
        VideoExtension1 exampleVideoExtension1 = new VideoExtension1();
        // set CreateAdExtensionSettings to VideoExtension1
        exampleCreateAdExtensionSettings.setActualInstance(exampleVideoExtension1);
        // to get back the VideoExtension1 set earlier
        VideoExtension1 testVideoExtension1 = (VideoExtension1) exampleCreateAdExtensionSettings.getActualInstance();
    }
}
```


