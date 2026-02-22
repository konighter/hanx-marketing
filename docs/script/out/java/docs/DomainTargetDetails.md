

# DomainTargetDetails

## oneOf schemas
* [AdvertiserDomainList1](AdvertiserDomainList1.md)
* [DomainFileTarget1](DomainFileTarget1.md)
* [DomainListTarget1](DomainListTarget1.md)
* [DomainNameTarget1](DomainNameTarget1.md)

## Example
```java
// Import classes:
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DomainTargetDetails;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.AdvertiserDomainList1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DomainFileTarget1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DomainListTarget1;
import com.hzltd.module.erplus.adv.adapter.amazon.model.all.DomainNameTarget1;

public class Example {
    public static void main(String[] args) {
        DomainTargetDetails exampleDomainTargetDetails = new DomainTargetDetails();

        // create a new AdvertiserDomainList1
        AdvertiserDomainList1 exampleAdvertiserDomainList1 = new AdvertiserDomainList1();
        // set DomainTargetDetails to AdvertiserDomainList1
        exampleDomainTargetDetails.setActualInstance(exampleAdvertiserDomainList1);
        // to get back the AdvertiserDomainList1 set earlier
        AdvertiserDomainList1 testAdvertiserDomainList1 = (AdvertiserDomainList1) exampleDomainTargetDetails.getActualInstance();

        // create a new DomainFileTarget1
        DomainFileTarget1 exampleDomainFileTarget1 = new DomainFileTarget1();
        // set DomainTargetDetails to DomainFileTarget1
        exampleDomainTargetDetails.setActualInstance(exampleDomainFileTarget1);
        // to get back the DomainFileTarget1 set earlier
        DomainFileTarget1 testDomainFileTarget1 = (DomainFileTarget1) exampleDomainTargetDetails.getActualInstance();

        // create a new DomainListTarget1
        DomainListTarget1 exampleDomainListTarget1 = new DomainListTarget1();
        // set DomainTargetDetails to DomainListTarget1
        exampleDomainTargetDetails.setActualInstance(exampleDomainListTarget1);
        // to get back the DomainListTarget1 set earlier
        DomainListTarget1 testDomainListTarget1 = (DomainListTarget1) exampleDomainTargetDetails.getActualInstance();

        // create a new DomainNameTarget1
        DomainNameTarget1 exampleDomainNameTarget1 = new DomainNameTarget1();
        // set DomainTargetDetails to DomainNameTarget1
        exampleDomainTargetDetails.setActualInstance(exampleDomainNameTarget1);
        // to get back the DomainNameTarget1 set earlier
        DomainNameTarget1 testDomainNameTarget1 = (DomainNameTarget1) exampleDomainTargetDetails.getActualInstance();
    }
}
```


