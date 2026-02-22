

# CreateVideoCallToAction

## oneOf schemas
* [ClickToUrlVideoCallToActionSettings](ClickToUrlVideoCallToActionSettings.md)
* [LearnMoreVideoCallToActionSettings](LearnMoreVideoCallToActionSettings.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.CreateVideoCallToAction;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ClickToUrlVideoCallToActionSettings;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.LearnMoreVideoCallToActionSettings;

public class Example {
    public static void main(String[] args) {
        CreateVideoCallToAction exampleCreateVideoCallToAction = new CreateVideoCallToAction();

        // create a new ClickToUrlVideoCallToActionSettings
        ClickToUrlVideoCallToActionSettings exampleClickToUrlVideoCallToActionSettings = new ClickToUrlVideoCallToActionSettings();
        // set CreateVideoCallToAction to ClickToUrlVideoCallToActionSettings
        exampleCreateVideoCallToAction.setActualInstance(exampleClickToUrlVideoCallToActionSettings);
        // to get back the ClickToUrlVideoCallToActionSettings set earlier
        ClickToUrlVideoCallToActionSettings testClickToUrlVideoCallToActionSettings = (ClickToUrlVideoCallToActionSettings) exampleCreateVideoCallToAction.getActualInstance();

        // create a new LearnMoreVideoCallToActionSettings
        LearnMoreVideoCallToActionSettings exampleLearnMoreVideoCallToActionSettings = new LearnMoreVideoCallToActionSettings();
        // set CreateVideoCallToAction to LearnMoreVideoCallToActionSettings
        exampleCreateVideoCallToAction.setActualInstance(exampleLearnMoreVideoCallToActionSettings);
        // to get back the LearnMoreVideoCallToActionSettings set earlier
        LearnMoreVideoCallToActionSettings testLearnMoreVideoCallToActionSettings = (LearnMoreVideoCallToActionSettings) exampleCreateVideoCallToAction.getActualInstance();
    }
}
```


