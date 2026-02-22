

# CreateContentRating

## oneOf schemas
* [DspContentRating1](DspContentRating1.md)
* [TwitchContentRating1](TwitchContentRating1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.CreateContentRating;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DspContentRating1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.TwitchContentRating1;

public class Example {
    public static void main(String[] args) {
        CreateContentRating exampleCreateContentRating = new CreateContentRating();

        // create a new DspContentRating1
        DspContentRating1 exampleDspContentRating1 = new DspContentRating1();
        // set CreateContentRating to DspContentRating1
        exampleCreateContentRating.setActualInstance(exampleDspContentRating1);
        // to get back the DspContentRating1 set earlier
        DspContentRating1 testDspContentRating1 = (DspContentRating1) exampleCreateContentRating.getActualInstance();

        // create a new TwitchContentRating1
        TwitchContentRating1 exampleTwitchContentRating1 = new TwitchContentRating1();
        // set CreateContentRating to TwitchContentRating1
        exampleCreateContentRating.setActualInstance(exampleTwitchContentRating1);
        // to get back the TwitchContentRating1 set earlier
        TwitchContentRating1 testTwitchContentRating1 = (TwitchContentRating1) exampleCreateContentRating.getActualInstance();
    }
}
```


