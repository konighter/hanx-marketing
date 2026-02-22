

# UpdateCreative

## oneOf schemas
* [AudioCreative2](AudioCreative2.md)
* [ComponentCreative2](ComponentCreative2.md)
* [DisplayCreative2](DisplayCreative2.md)
* [ProductCreative2](ProductCreative2.md)
* [ThirdPartyCreative2](ThirdPartyCreative2.md)
* [VideoCreative2](VideoCreative2.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.UpdateCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AudioCreative2;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ComponentCreative2;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DisplayCreative2;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ProductCreative2;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ThirdPartyCreative2;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.VideoCreative2;

public class Example {
    public static void main(String[] args) {
        UpdateCreative exampleUpdateCreative = new UpdateCreative();

        // create a new AudioCreative2
        AudioCreative2 exampleAudioCreative2 = new AudioCreative2();
        // set UpdateCreative to AudioCreative2
        exampleUpdateCreative.setActualInstance(exampleAudioCreative2);
        // to get back the AudioCreative2 set earlier
        AudioCreative2 testAudioCreative2 = (AudioCreative2) exampleUpdateCreative.getActualInstance();

        // create a new ComponentCreative2
        ComponentCreative2 exampleComponentCreative2 = new ComponentCreative2();
        // set UpdateCreative to ComponentCreative2
        exampleUpdateCreative.setActualInstance(exampleComponentCreative2);
        // to get back the ComponentCreative2 set earlier
        ComponentCreative2 testComponentCreative2 = (ComponentCreative2) exampleUpdateCreative.getActualInstance();

        // create a new DisplayCreative2
        DisplayCreative2 exampleDisplayCreative2 = new DisplayCreative2();
        // set UpdateCreative to DisplayCreative2
        exampleUpdateCreative.setActualInstance(exampleDisplayCreative2);
        // to get back the DisplayCreative2 set earlier
        DisplayCreative2 testDisplayCreative2 = (DisplayCreative2) exampleUpdateCreative.getActualInstance();

        // create a new ProductCreative2
        ProductCreative2 exampleProductCreative2 = new ProductCreative2();
        // set UpdateCreative to ProductCreative2
        exampleUpdateCreative.setActualInstance(exampleProductCreative2);
        // to get back the ProductCreative2 set earlier
        ProductCreative2 testProductCreative2 = (ProductCreative2) exampleUpdateCreative.getActualInstance();

        // create a new ThirdPartyCreative2
        ThirdPartyCreative2 exampleThirdPartyCreative2 = new ThirdPartyCreative2();
        // set UpdateCreative to ThirdPartyCreative2
        exampleUpdateCreative.setActualInstance(exampleThirdPartyCreative2);
        // to get back the ThirdPartyCreative2 set earlier
        ThirdPartyCreative2 testThirdPartyCreative2 = (ThirdPartyCreative2) exampleUpdateCreative.getActualInstance();

        // create a new VideoCreative2
        VideoCreative2 exampleVideoCreative2 = new VideoCreative2();
        // set UpdateCreative to VideoCreative2
        exampleUpdateCreative.setActualInstance(exampleVideoCreative2);
        // to get back the VideoCreative2 set earlier
        VideoCreative2 testVideoCreative2 = (VideoCreative2) exampleUpdateCreative.getActualInstance();
    }
}
```


