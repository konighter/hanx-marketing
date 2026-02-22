

# BudgetValue

## oneOf schemas
* [MonetaryBudgetValue](MonetaryBudgetValue.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.BudgetValue;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.MonetaryBudgetValue;

public class Example {
    public static void main(String[] args) {
        BudgetValue exampleBudgetValue = new BudgetValue();

        // create a new MonetaryBudgetValue
        MonetaryBudgetValue exampleMonetaryBudgetValue = new MonetaryBudgetValue();
        // set BudgetValue to MonetaryBudgetValue
        exampleBudgetValue.setActualInstance(exampleMonetaryBudgetValue);
        // to get back the MonetaryBudgetValue set earlier
        MonetaryBudgetValue testMonetaryBudgetValue = (MonetaryBudgetValue) exampleBudgetValue.getActualInstance();
    }
}
```


