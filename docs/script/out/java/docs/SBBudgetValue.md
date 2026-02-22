

# SBBudgetValue

## oneOf schemas
* [MonetaryBudgetValue](MonetaryBudgetValue.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.SBBudgetValue;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sb.MonetaryBudgetValue;

public class Example {
    public static void main(String[] args) {
        SBBudgetValue exampleSBBudgetValue = new SBBudgetValue();

        // create a new MonetaryBudgetValue
        MonetaryBudgetValue exampleMonetaryBudgetValue = new MonetaryBudgetValue();
        // set SBBudgetValue to MonetaryBudgetValue
        exampleSBBudgetValue.setActualInstance(exampleMonetaryBudgetValue);
        // to get back the MonetaryBudgetValue set earlier
        MonetaryBudgetValue testMonetaryBudgetValue = (MonetaryBudgetValue) exampleSBBudgetValue.getActualInstance();
    }
}
```


