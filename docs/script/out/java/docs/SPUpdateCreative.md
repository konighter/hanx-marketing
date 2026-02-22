

# SPUpdateCreative

## oneOf schemas
* [ProductCreative2](ProductCreative2.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.SPUpdateCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.ProductCreative2;

public class Example {
    public static void main(String[] args) {
        SPUpdateCreative exampleSPUpdateCreative = new SPUpdateCreative();

        // create a new ProductCreative2
        ProductCreative2 exampleProductCreative2 = new ProductCreative2();
        // set SPUpdateCreative to ProductCreative2
        exampleSPUpdateCreative.setActualInstance(exampleProductCreative2);
        // to get back the ProductCreative2 set earlier
        ProductCreative2 testProductCreative2 = (ProductCreative2) exampleSPUpdateCreative.getActualInstance();
    }
}
```


