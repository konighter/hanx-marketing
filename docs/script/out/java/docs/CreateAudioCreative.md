

# CreateAudioCreative

| CreateAudioCreative | Description | | --- | --- | | `standardAudioSettings` | The standard audio experience settings. See the Audio Spec for more info: https://advertising.amazon.com/en-us/resources/ad-specs/audio-ads?ref_=a20m_us_spcs_spcs_aa |

## oneOf schemas
* [StandardAudioSettings1](StandardAudioSettings1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.CreateAudioCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.StandardAudioSettings1;

public class Example {
    public static void main(String[] args) {
        CreateAudioCreative exampleCreateAudioCreative = new CreateAudioCreative();

        // create a new StandardAudioSettings1
        StandardAudioSettings1 exampleStandardAudioSettings1 = new StandardAudioSettings1();
        // set CreateAudioCreative to StandardAudioSettings1
        exampleCreateAudioCreative.setActualInstance(exampleStandardAudioSettings1);
        // to get back the StandardAudioSettings1 set earlier
        StandardAudioSettings1 testStandardAudioSettings1 = (StandardAudioSettings1) exampleCreateAudioCreative.getActualInstance();
    }
}
```


