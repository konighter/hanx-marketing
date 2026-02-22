

# SBUpdateCreative

## oneOf schemas
* [ComponentCreative2](ComponentCreative2.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.SBUpdateCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.ComponentCreative2;

public class Example {
    public static void main(String[] args) {
        SBUpdateCreative exampleSBUpdateCreative = new SBUpdateCreative();

        // create a new ComponentCreative2
        ComponentCreative2 exampleComponentCreative2 = new ComponentCreative2();
        // set SBUpdateCreative to ComponentCreative2
        exampleSBUpdateCreative.setActualInstance(exampleComponentCreative2);
        // to get back the ComponentCreative2 set earlier
        ComponentCreative2 testComponentCreative2 = (ComponentCreative2) exampleSBUpdateCreative.getActualInstance();
    }
}
```


