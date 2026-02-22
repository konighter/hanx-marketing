

# DisplayCallToAction

## oneOf schemas
* [ClickToAppDisplayCallToActionSettings1](ClickToAppDisplayCallToActionSettings1.md)
* [ClickToUrlDisplayCallToActionSettings1](ClickToUrlDisplayCallToActionSettings1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DisplayCallToAction;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ClickToAppDisplayCallToActionSettings1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ClickToUrlDisplayCallToActionSettings1;

public class Example {
    public static void main(String[] args) {
        DisplayCallToAction exampleDisplayCallToAction = new DisplayCallToAction();

        // create a new ClickToAppDisplayCallToActionSettings1
        ClickToAppDisplayCallToActionSettings1 exampleClickToAppDisplayCallToActionSettings1 = new ClickToAppDisplayCallToActionSettings1();
        // set DisplayCallToAction to ClickToAppDisplayCallToActionSettings1
        exampleDisplayCallToAction.setActualInstance(exampleClickToAppDisplayCallToActionSettings1);
        // to get back the ClickToAppDisplayCallToActionSettings1 set earlier
        ClickToAppDisplayCallToActionSettings1 testClickToAppDisplayCallToActionSettings1 = (ClickToAppDisplayCallToActionSettings1) exampleDisplayCallToAction.getActualInstance();

        // create a new ClickToUrlDisplayCallToActionSettings1
        ClickToUrlDisplayCallToActionSettings1 exampleClickToUrlDisplayCallToActionSettings1 = new ClickToUrlDisplayCallToActionSettings1();
        // set DisplayCallToAction to ClickToUrlDisplayCallToActionSettings1
        exampleDisplayCallToAction.setActualInstance(exampleClickToUrlDisplayCallToActionSettings1);
        // to get back the ClickToUrlDisplayCallToActionSettings1 set earlier
        ClickToUrlDisplayCallToActionSettings1 testClickToUrlDisplayCallToActionSettings1 = (ClickToUrlDisplayCallToActionSettings1) exampleDisplayCallToAction.getActualInstance();
    }
}
```


