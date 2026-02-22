

# SBCreateBudgetValue

## oneOf schemas
* [MonetaryBudgetValue1](MonetaryBudgetValue1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.SBCreateBudgetValue;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.MonetaryBudgetValue1;

public class Example {
    public static void main(String[] args) {
        SBCreateBudgetValue exampleSBCreateBudgetValue = new SBCreateBudgetValue();

        // create a new MonetaryBudgetValue1
        MonetaryBudgetValue1 exampleMonetaryBudgetValue1 = new MonetaryBudgetValue1();
        // set SBCreateBudgetValue to MonetaryBudgetValue1
        exampleSBCreateBudgetValue.setActualInstance(exampleMonetaryBudgetValue1);
        // to get back the MonetaryBudgetValue1 set earlier
        MonetaryBudgetValue1 testMonetaryBudgetValue1 = (MonetaryBudgetValue1) exampleSBCreateBudgetValue.getActualInstance();
    }
}
```


