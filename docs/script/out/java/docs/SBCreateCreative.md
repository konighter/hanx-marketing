

# SBCreateCreative

## oneOf schemas
* [ComponentCreative](ComponentCreative.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.SBCreateCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.ComponentCreative;

public class Example {
    public static void main(String[] args) {
        SBCreateCreative exampleSBCreateCreative = new SBCreateCreative();

        // create a new ComponentCreative
        ComponentCreative exampleComponentCreative = new ComponentCreative();
        // set SBCreateCreative to ComponentCreative
        exampleSBCreateCreative.setActualInstance(exampleComponentCreative);
        // to get back the ComponentCreative set earlier
        ComponentCreative testComponentCreative = (ComponentCreative) exampleSBCreateCreative.getActualInstance();
    }
}
```


