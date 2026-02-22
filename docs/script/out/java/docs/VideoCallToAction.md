

# VideoCallToAction

## oneOf schemas
* [ClickToUrlVideoCallToActionSettings1](ClickToUrlVideoCallToActionSettings1.md)
* [LearnMoreVideoCallToActionSettings1](LearnMoreVideoCallToActionSettings1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.VideoCallToAction;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ClickToUrlVideoCallToActionSettings1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.LearnMoreVideoCallToActionSettings1;

public class Example {
    public static void main(String[] args) {
        VideoCallToAction exampleVideoCallToAction = new VideoCallToAction();

        // create a new ClickToUrlVideoCallToActionSettings1
        ClickToUrlVideoCallToActionSettings1 exampleClickToUrlVideoCallToActionSettings1 = new ClickToUrlVideoCallToActionSettings1();
        // set VideoCallToAction to ClickToUrlVideoCallToActionSettings1
        exampleVideoCallToAction.setActualInstance(exampleClickToUrlVideoCallToActionSettings1);
        // to get back the ClickToUrlVideoCallToActionSettings1 set earlier
        ClickToUrlVideoCallToActionSettings1 testClickToUrlVideoCallToActionSettings1 = (ClickToUrlVideoCallToActionSettings1) exampleVideoCallToAction.getActualInstance();

        // create a new LearnMoreVideoCallToActionSettings1
        LearnMoreVideoCallToActionSettings1 exampleLearnMoreVideoCallToActionSettings1 = new LearnMoreVideoCallToActionSettings1();
        // set VideoCallToAction to LearnMoreVideoCallToActionSettings1
        exampleVideoCallToAction.setActualInstance(exampleLearnMoreVideoCallToActionSettings1);
        // to get back the LearnMoreVideoCallToActionSettings1 set earlier
        LearnMoreVideoCallToActionSettings1 testLearnMoreVideoCallToActionSettings1 = (LearnMoreVideoCallToActionSettings1) exampleVideoCallToAction.getActualInstance();
    }
}
```


