

# ContentRating

## oneOf schemas
* [DspContentRating](DspContentRating.md)
* [TwitchContentRating](TwitchContentRating.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ContentRating;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DspContentRating;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.TwitchContentRating;

public class Example {
    public static void main(String[] args) {
        ContentRating exampleContentRating = new ContentRating();

        // create a new DspContentRating
        DspContentRating exampleDspContentRating = new DspContentRating();
        // set ContentRating to DspContentRating
        exampleContentRating.setActualInstance(exampleDspContentRating);
        // to get back the DspContentRating set earlier
        DspContentRating testDspContentRating = (DspContentRating) exampleContentRating.getActualInstance();

        // create a new TwitchContentRating
        TwitchContentRating exampleTwitchContentRating = new TwitchContentRating();
        // set ContentRating to TwitchContentRating
        exampleContentRating.setActualInstance(exampleTwitchContentRating);
        // to get back the TwitchContentRating set earlier
        TwitchContentRating testTwitchContentRating = (TwitchContentRating) exampleContentRating.getActualInstance();
    }
}
```


