

# SPCreateBudgetValue

## oneOf schemas
* [MonetaryBudgetValue1](MonetaryBudgetValue1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.SPCreateBudgetValue;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.MonetaryBudgetValue1;

public class Example {
    public static void main(String[] args) {
        SPCreateBudgetValue exampleSPCreateBudgetValue = new SPCreateBudgetValue();

        // create a new MonetaryBudgetValue1
        MonetaryBudgetValue1 exampleMonetaryBudgetValue1 = new MonetaryBudgetValue1();
        // set SPCreateBudgetValue to MonetaryBudgetValue1
        exampleSPCreateBudgetValue.setActualInstance(exampleMonetaryBudgetValue1);
        // to get back the MonetaryBudgetValue1 set earlier
        MonetaryBudgetValue1 testMonetaryBudgetValue1 = (MonetaryBudgetValue1) exampleSPCreateBudgetValue.getActualInstance();
    }
}
```


