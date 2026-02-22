

# SPBudgetValue

## oneOf schemas
* [MonetaryBudgetValue](MonetaryBudgetValue.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.SPBudgetValue;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.MonetaryBudgetValue;

public class Example {
    public static void main(String[] args) {
        SPBudgetValue exampleSPBudgetValue = new SPBudgetValue();

        // create a new MonetaryBudgetValue
        MonetaryBudgetValue exampleMonetaryBudgetValue = new MonetaryBudgetValue();
        // set SPBudgetValue to MonetaryBudgetValue
        exampleSPBudgetValue.setActualInstance(exampleMonetaryBudgetValue);
        // to get back the MonetaryBudgetValue set earlier
        MonetaryBudgetValue testMonetaryBudgetValue = (MonetaryBudgetValue) exampleSPBudgetValue.getActualInstance();
    }
}
```


