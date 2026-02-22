

# SPCreateCreative

## oneOf schemas
* [ProductCreative](ProductCreative.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.SPCreateCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.ProductCreative;

public class Example {
    public static void main(String[] args) {
        SPCreateCreative exampleSPCreateCreative = new SPCreateCreative();

        // create a new ProductCreative
        ProductCreative exampleProductCreative = new ProductCreative();
        // set SPCreateCreative to ProductCreative
        exampleSPCreateCreative.setActualInstance(exampleProductCreative);
        // to get back the ProductCreative set earlier
        ProductCreative testProductCreative = (ProductCreative) exampleSPCreateCreative.getActualInstance();
    }
}
```


