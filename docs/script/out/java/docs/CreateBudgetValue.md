

# CreateBudgetValue

## oneOf schemas
* [MonetaryBudgetValue1](MonetaryBudgetValue1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.CreateBudgetValue;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.MonetaryBudgetValue1;

public class Example {
    public static void main(String[] args) {
        CreateBudgetValue exampleCreateBudgetValue = new CreateBudgetValue();

        // create a new MonetaryBudgetValue1
        MonetaryBudgetValue1 exampleMonetaryBudgetValue1 = new MonetaryBudgetValue1();
        // set CreateBudgetValue to MonetaryBudgetValue1
        exampleCreateBudgetValue.setActualInstance(exampleMonetaryBudgetValue1);
        // to get back the MonetaryBudgetValue1 set earlier
        MonetaryBudgetValue1 testMonetaryBudgetValue1 = (MonetaryBudgetValue1) exampleCreateBudgetValue.getActualInstance();
    }
}
```


