

# SPCreative

## oneOf schemas
* [ProductCreative1](ProductCreative1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.SPCreative;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.ProductCreative1;

public class Example {
    public static void main(String[] args) {
        SPCreative exampleSPCreative = new SPCreative();

        // create a new ProductCreative1
        ProductCreative1 exampleProductCreative1 = new ProductCreative1();
        // set SPCreative to ProductCreative1
        exampleSPCreative.setActualInstance(exampleProductCreative1);
        // to get back the ProductCreative1 set earlier
        ProductCreative1 testProductCreative1 = (ProductCreative1) exampleSPCreative.getActualInstance();
    }
}
```


