

# CreateDisplayCallToAction

## oneOf schemas
* [ClickToAppDisplayCallToActionSettings](ClickToAppDisplayCallToActionSettings.md)
* [ClickToUrlDisplayCallToActionSettings](ClickToUrlDisplayCallToActionSettings.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.CreateDisplayCallToAction;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ClickToAppDisplayCallToActionSettings;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ClickToUrlDisplayCallToActionSettings;

public class Example {
    public static void main(String[] args) {
        CreateDisplayCallToAction exampleCreateDisplayCallToAction = new CreateDisplayCallToAction();

        // create a new ClickToAppDisplayCallToActionSettings
        ClickToAppDisplayCallToActionSettings exampleClickToAppDisplayCallToActionSettings = new ClickToAppDisplayCallToActionSettings();
        // set CreateDisplayCallToAction to ClickToAppDisplayCallToActionSettings
        exampleCreateDisplayCallToAction.setActualInstance(exampleClickToAppDisplayCallToActionSettings);
        // to get back the ClickToAppDisplayCallToActionSettings set earlier
        ClickToAppDisplayCallToActionSettings testClickToAppDisplayCallToActionSettings = (ClickToAppDisplayCallToActionSettings) exampleCreateDisplayCallToAction.getActualInstance();

        // create a new ClickToUrlDisplayCallToActionSettings
        ClickToUrlDisplayCallToActionSettings exampleClickToUrlDisplayCallToActionSettings = new ClickToUrlDisplayCallToActionSettings();
        // set CreateDisplayCallToAction to ClickToUrlDisplayCallToActionSettings
        exampleCreateDisplayCallToAction.setActualInstance(exampleClickToUrlDisplayCallToActionSettings);
        // to get back the ClickToUrlDisplayCallToActionSettings set earlier
        ClickToUrlDisplayCallToActionSettings testClickToUrlDisplayCallToActionSettings = (ClickToUrlDisplayCallToActionSettings) exampleCreateDisplayCallToAction.getActualInstance();
    }
}
```


