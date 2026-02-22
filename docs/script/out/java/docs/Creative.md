

# Creative

## oneOf schemas
* [AudioCreative1](AudioCreative1.md)
* [ComponentCreative1](ComponentCreative1.md)
* [DisplayCreative1](DisplayCreative1.md)
* [ProductCreative1](ProductCreative1.md)
* [ThirdPartyCreative1](ThirdPartyCreative1.md)
* [VideoCreative1](VideoCreative1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.Creative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AudioCreative1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ComponentCreative1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DisplayCreative1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ProductCreative1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ThirdPartyCreative1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.VideoCreative1;

public class Example {
    public static void main(String[] args) {
        Creative exampleCreative = new Creative();

        // create a new AudioCreative1
        AudioCreative1 exampleAudioCreative1 = new AudioCreative1();
        // set Creative to AudioCreative1
        exampleCreative.setActualInstance(exampleAudioCreative1);
        // to get back the AudioCreative1 set earlier
        AudioCreative1 testAudioCreative1 = (AudioCreative1) exampleCreative.getActualInstance();

        // create a new ComponentCreative1
        ComponentCreative1 exampleComponentCreative1 = new ComponentCreative1();
        // set Creative to ComponentCreative1
        exampleCreative.setActualInstance(exampleComponentCreative1);
        // to get back the ComponentCreative1 set earlier
        ComponentCreative1 testComponentCreative1 = (ComponentCreative1) exampleCreative.getActualInstance();

        // create a new DisplayCreative1
        DisplayCreative1 exampleDisplayCreative1 = new DisplayCreative1();
        // set Creative to DisplayCreative1
        exampleCreative.setActualInstance(exampleDisplayCreative1);
        // to get back the DisplayCreative1 set earlier
        DisplayCreative1 testDisplayCreative1 = (DisplayCreative1) exampleCreative.getActualInstance();

        // create a new ProductCreative1
        ProductCreative1 exampleProductCreative1 = new ProductCreative1();
        // set Creative to ProductCreative1
        exampleCreative.setActualInstance(exampleProductCreative1);
        // to get back the ProductCreative1 set earlier
        ProductCreative1 testProductCreative1 = (ProductCreative1) exampleCreative.getActualInstance();

        // create a new ThirdPartyCreative1
        ThirdPartyCreative1 exampleThirdPartyCreative1 = new ThirdPartyCreative1();
        // set Creative to ThirdPartyCreative1
        exampleCreative.setActualInstance(exampleThirdPartyCreative1);
        // to get back the ThirdPartyCreative1 set earlier
        ThirdPartyCreative1 testThirdPartyCreative1 = (ThirdPartyCreative1) exampleCreative.getActualInstance();

        // create a new VideoCreative1
        VideoCreative1 exampleVideoCreative1 = new VideoCreative1();
        // set Creative to VideoCreative1
        exampleCreative.setActualInstance(exampleVideoCreative1);
        // to get back the VideoCreative1 set earlier
        VideoCreative1 testVideoCreative1 = (VideoCreative1) exampleCreative.getActualInstance();
    }
}
```


