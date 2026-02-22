

# SBCreative

## oneOf schemas
* [ComponentCreative1](ComponentCreative1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.SBCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.ComponentCreative1;

public class Example {
    public static void main(String[] args) {
        SBCreative exampleSBCreative = new SBCreative();

        // create a new ComponentCreative1
        ComponentCreative1 exampleComponentCreative1 = new ComponentCreative1();
        // set SBCreative to ComponentCreative1
        exampleSBCreative.setActualInstance(exampleComponentCreative1);
        // to get back the ComponentCreative1 set earlier
        ComponentCreative1 testComponentCreative1 = (ComponentCreative1) exampleSBCreative.getActualInstance();
    }
}
```


