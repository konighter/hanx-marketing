

# CreateCreative

## oneOf schemas
* [AudioCreative](AudioCreative.md)
* [ComponentCreative](ComponentCreative.md)
* [DisplayCreative](DisplayCreative.md)
* [ProductCreative](ProductCreative.md)
* [ThirdPartyCreative](ThirdPartyCreative.md)
* [VideoCreative](VideoCreative.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.CreateCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AudioCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ComponentCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DisplayCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ProductCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ThirdPartyCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.VideoCreative;

public class Example {
    public static void main(String[] args) {
        CreateCreative exampleCreateCreative = new CreateCreative();

        // create a new AudioCreative
        AudioCreative exampleAudioCreative = new AudioCreative();
        // set CreateCreative to AudioCreative
        exampleCreateCreative.setActualInstance(exampleAudioCreative);
        // to get back the AudioCreative set earlier
        AudioCreative testAudioCreative = (AudioCreative) exampleCreateCreative.getActualInstance();

        // create a new ComponentCreative
        ComponentCreative exampleComponentCreative = new ComponentCreative();
        // set CreateCreative to ComponentCreative
        exampleCreateCreative.setActualInstance(exampleComponentCreative);
        // to get back the ComponentCreative set earlier
        ComponentCreative testComponentCreative = (ComponentCreative) exampleCreateCreative.getActualInstance();

        // create a new DisplayCreative
        DisplayCreative exampleDisplayCreative = new DisplayCreative();
        // set CreateCreative to DisplayCreative
        exampleCreateCreative.setActualInstance(exampleDisplayCreative);
        // to get back the DisplayCreative set earlier
        DisplayCreative testDisplayCreative = (DisplayCreative) exampleCreateCreative.getActualInstance();

        // create a new ProductCreative
        ProductCreative exampleProductCreative = new ProductCreative();
        // set CreateCreative to ProductCreative
        exampleCreateCreative.setActualInstance(exampleProductCreative);
        // to get back the ProductCreative set earlier
        ProductCreative testProductCreative = (ProductCreative) exampleCreateCreative.getActualInstance();

        // create a new ThirdPartyCreative
        ThirdPartyCreative exampleThirdPartyCreative = new ThirdPartyCreative();
        // set CreateCreative to ThirdPartyCreative
        exampleCreateCreative.setActualInstance(exampleThirdPartyCreative);
        // to get back the ThirdPartyCreative set earlier
        ThirdPartyCreative testThirdPartyCreative = (ThirdPartyCreative) exampleCreateCreative.getActualInstance();

        // create a new VideoCreative
        VideoCreative exampleVideoCreative = new VideoCreative();
        // set CreateCreative to VideoCreative
        exampleCreateCreative.setActualInstance(exampleVideoCreative);
        // to get back the VideoCreative set earlier
        VideoCreative testVideoCreative = (VideoCreative) exampleCreateCreative.getActualInstance();
    }
}
```


