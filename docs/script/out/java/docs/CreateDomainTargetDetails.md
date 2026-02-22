

# CreateDomainTargetDetails

## oneOf schemas
* [AdvertiserDomainList](AdvertiserDomainList.md)
* [DomainFileTarget](DomainFileTarget.md)
* [DomainListTarget](DomainListTarget.md)
* [DomainNameTarget](DomainNameTarget.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.CreateDomainTargetDetails;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AdvertiserDomainList;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DomainFileTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DomainListTarget;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DomainNameTarget;

public class Example {
    public static void main(String[] args) {
        CreateDomainTargetDetails exampleCreateDomainTargetDetails = new CreateDomainTargetDetails();

        // create a new AdvertiserDomainList
        AdvertiserDomainList exampleAdvertiserDomainList = new AdvertiserDomainList();
        // set CreateDomainTargetDetails to AdvertiserDomainList
        exampleCreateDomainTargetDetails.setActualInstance(exampleAdvertiserDomainList);
        // to get back the AdvertiserDomainList set earlier
        AdvertiserDomainList testAdvertiserDomainList = (AdvertiserDomainList) exampleCreateDomainTargetDetails.getActualInstance();

        // create a new DomainFileTarget
        DomainFileTarget exampleDomainFileTarget = new DomainFileTarget();
        // set CreateDomainTargetDetails to DomainFileTarget
        exampleCreateDomainTargetDetails.setActualInstance(exampleDomainFileTarget);
        // to get back the DomainFileTarget set earlier
        DomainFileTarget testDomainFileTarget = (DomainFileTarget) exampleCreateDomainTargetDetails.getActualInstance();

        // create a new DomainListTarget
        DomainListTarget exampleDomainListTarget = new DomainListTarget();
        // set CreateDomainTargetDetails to DomainListTarget
        exampleCreateDomainTargetDetails.setActualInstance(exampleDomainListTarget);
        // to get back the DomainListTarget set earlier
        DomainListTarget testDomainListTarget = (DomainListTarget) exampleCreateDomainTargetDetails.getActualInstance();

        // create a new DomainNameTarget
        DomainNameTarget exampleDomainNameTarget = new DomainNameTarget();
        // set CreateDomainTargetDetails to DomainNameTarget
        exampleCreateDomainTargetDetails.setActualInstance(exampleDomainNameTarget);
        // to get back the DomainNameTarget set earlier
        DomainNameTarget testDomainNameTarget = (DomainNameTarget) exampleCreateDomainTargetDetails.getActualInstance();
    }
}
```


