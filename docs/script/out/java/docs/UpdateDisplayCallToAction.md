

# UpdateDisplayCallToAction

## oneOf schemas
* [ClickToAppDisplayCallToActionSettings2](ClickToAppDisplayCallToActionSettings2.md)
* [ClickToUrlDisplayCallToActionSettings2](ClickToUrlDisplayCallToActionSettings2.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.UpdateDisplayCallToAction;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ClickToAppDisplayCallToActionSettings2;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ClickToUrlDisplayCallToActionSettings2;

public class Example {
    public static void main(String[] args) {
        UpdateDisplayCallToAction exampleUpdateDisplayCallToAction = new UpdateDisplayCallToAction();

        // create a new ClickToAppDisplayCallToActionSettings2
        ClickToAppDisplayCallToActionSettings2 exampleClickToAppDisplayCallToActionSettings2 = new ClickToAppDisplayCallToActionSettings2();
        // set UpdateDisplayCallToAction to ClickToAppDisplayCallToActionSettings2
        exampleUpdateDisplayCallToAction.setActualInstance(exampleClickToAppDisplayCallToActionSettings2);
        // to get back the ClickToAppDisplayCallToActionSettings2 set earlier
        ClickToAppDisplayCallToActionSettings2 testClickToAppDisplayCallToActionSettings2 = (ClickToAppDisplayCallToActionSettings2) exampleUpdateDisplayCallToAction.getActualInstance();

        // create a new ClickToUrlDisplayCallToActionSettings2
        ClickToUrlDisplayCallToActionSettings2 exampleClickToUrlDisplayCallToActionSettings2 = new ClickToUrlDisplayCallToActionSettings2();
        // set UpdateDisplayCallToAction to ClickToUrlDisplayCallToActionSettings2
        exampleUpdateDisplayCallToAction.setActualInstance(exampleClickToUrlDisplayCallToActionSettings2);
        // to get back the ClickToUrlDisplayCallToActionSettings2 set earlier
        ClickToUrlDisplayCallToActionSettings2 testClickToUrlDisplayCallToActionSettings2 = (ClickToUrlDisplayCallToActionSettings2) exampleUpdateDisplayCallToAction.getActualInstance();
    }
}
```


