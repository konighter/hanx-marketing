

# OverridableTargets

## oneOf schemas
* [KeywordTarget1](KeywordTarget1.md)
* [ThemeTarget1](ThemeTarget1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.OverridableTargets;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.KeywordTarget1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.ThemeTarget1;

public class Example {
    public static void main(String[] args) {
        OverridableTargets exampleOverridableTargets = new OverridableTargets();

        // create a new KeywordTarget1
        KeywordTarget1 exampleKeywordTarget1 = new KeywordTarget1();
        // set OverridableTargets to KeywordTarget1
        exampleOverridableTargets.setActualInstance(exampleKeywordTarget1);
        // to get back the KeywordTarget1 set earlier
        KeywordTarget1 testKeywordTarget1 = (KeywordTarget1) exampleOverridableTargets.getActualInstance();

        // create a new ThemeTarget1
        ThemeTarget1 exampleThemeTarget1 = new ThemeTarget1();
        // set OverridableTargets to ThemeTarget1
        exampleOverridableTargets.setActualInstance(exampleThemeTarget1);
        // to get back the ThemeTarget1 set earlier
        ThemeTarget1 testThemeTarget1 = (ThemeTarget1) exampleOverridableTargets.getActualInstance();
    }
}
```


