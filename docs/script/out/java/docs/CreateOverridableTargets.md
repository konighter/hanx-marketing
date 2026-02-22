

# CreateOverridableTargets

## oneOf schemas
* [KeywordTarget](KeywordTarget.md)
* [ThemeTarget](ThemeTarget.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.CreateOverridableTargets;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.KeywordTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ThemeTarget;

public class Example {
    public static void main(String[] args) {
        CreateOverridableTargets exampleCreateOverridableTargets = new CreateOverridableTargets();

        // create a new KeywordTarget
        KeywordTarget exampleKeywordTarget = new KeywordTarget();
        // set CreateOverridableTargets to KeywordTarget
        exampleCreateOverridableTargets.setActualInstance(exampleKeywordTarget);
        // to get back the KeywordTarget set earlier
        KeywordTarget testKeywordTarget = (KeywordTarget) exampleCreateOverridableTargets.getActualInstance();

        // create a new ThemeTarget
        ThemeTarget exampleThemeTarget = new ThemeTarget();
        // set CreateOverridableTargets to ThemeTarget
        exampleCreateOverridableTargets.setActualInstance(exampleThemeTarget);
        // to get back the ThemeTarget set earlier
        ThemeTarget testThemeTarget = (ThemeTarget) exampleCreateOverridableTargets.getActualInstance();
    }
}
```


