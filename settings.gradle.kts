rootProject.name = "hzapp"

include("hzapp-server")


// ERP+ 子模块

include("hzapp-module-erplus:hzapp-module-erplus-api")
include("hzapp-module-erplus:hzapp-module-erplus-biz")
include("hzapp-module-erplus:hzapp-module-erplus-spapi")
include("hzapp-module-erplus:hzapp-module-erplus-adv")

// AMZ 子模块 (三层嵌套)
include("hzapp-module-erplus:hzapp-module-erplus-amz:hzapp-module-erplus-amz-api")
include("hzapp-module-erplus:hzapp-module-erplus-amz:hzapp-module-erplus-amz-biz")


