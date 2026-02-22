

# UpdateAudioCreative

| UpdateAudioCreative | Description | | --- | --- | | `standardAudioSettings` | The standard audio experience settings. See the Audio Spec for more info: https://advertising.amazon.com/en-us/resources/ad-specs/audio-ads?ref_=a20m_us_spcs_spcs_aa |

## oneOf schemas
* [StandardAudioSettings2](StandardAudioSettings2.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.UpdateAudioCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.StandardAudioSettings2;

public class Example {
    public static void main(String[] args) {
        UpdateAudioCreative exampleUpdateAudioCreative = new UpdateAudioCreative();

        // create a new StandardAudioSettings2
        StandardAudioSettings2 exampleStandardAudioSettings2 = new StandardAudioSettings2();
        // set UpdateAudioCreative to StandardAudioSettings2
        exampleUpdateAudioCreative.setActualInstance(exampleStandardAudioSettings2);
        // to get back the StandardAudioSettings2 set earlier
        StandardAudioSettings2 testStandardAudioSettings2 = (StandardAudioSettings2) exampleUpdateAudioCreative.getActualInstance();
    }
}
```


