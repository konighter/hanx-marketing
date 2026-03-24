package com.hzltd.module.amz.api.spapi.mapping.data;

import com.google.common.collect.Lists;
import com.hzltd.module.amz.api.spapi.mapping.AttributeSchemaMapper;
import com.hzltd.module.amz.api.spapi.mapping.AttributeValueMapper;
import com.hzltd.module.amz.api.spapi.proto.ProductTypeSchemaItem;
import com.hzltd.module.spapi.model.category.AttributeValueModel;
import com.hzltd.module.spapi.model.category.CategoryAttributeModel;
import com.hzltd.module.spapi.model.system.ProductSpuModel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class OriginCountryMapper implements AttributeValueMapper, AttributeSchemaMapper {

    private static final List<String> countryEnum = List.of("GB","TL","WD","WZ","XK","XM","BT","VI","VG","TP","CN","CF","DK","UA","UZ","UG","UY","TD","YE","AM","IL","IQ","IR","BZ","CV","RU","BG","HR","GU","GM","IS","GN","GW","LI","CG","CD","LY","LR","CA","GH","GA","HU","MP","GS","YU","AQ","SS","ZA","BQ","BW","QA","RW","LU","IN","ID","GT","EC","ER","SY","CU","TW","SZ","KG","DJ","TV","KZ","CO","CR","CM","TM","TR","LC","KN","ST","XE","XY","BL","VC","PM","CX","SH","MF","SX","SM","GY","TZ","EG","ET","KI","TJ","SN","RS","CS","SL","CY","SC","MX","TG","DM","DO","AT","VE","BD","AO","AI","AG","AD","FM","NI","NG","NE","NP","XN","PS","BS","PK","BB","PG","PY","PA","BH","BR","BF","BV","GR","PW","CK","CW","XC","KY","DE","IT","SB","ZR","TK","LV","NO","CZ","MD","MA","MC","BN","FJ","SK","SI","SJ","LK","SG","NC","NZ","JP","CL","IM","KP","unknown","KH","GG","GD","GL","GE","VA","BE","MR","MU","TO","SA","FR","TF","PF","GF","FO","PL","XB","PR","BA","TH","JE","ZW","HN","HT","AU","MO","IE","EE","JM","TC","TT","TA","BO","NR","SE","CH","GP","WF","VU","RE","BY","BM","PN","GI","FK","KW","KM","CI","CC","PE","TN","LT","SO","JO","NA","NU","MM","RO","US","UM","AS","LA","KE","FI","SD","SR","UK","IO","NL","AN","MZ","LS","PH","SV","WS","PT","MN","MS","BI","EH","ES","IC","NF","BJ","ZM","GQ","HM","VN","AX","AZ","AF","DZ","AL","AE","OM","AR","AC","AW","KR","HK","MK","MV","MW","MQ","MY","YT","MH","MT","MG","ML","LB","ME");

    private static final List<String> countryEnumName = List.of("GB","Timor-Leste","WD","WZ","XK","XM","不丹","不列颠岛(美)","不列颠岛(英)","东帝汶","中国","中非共和国","丹麦","乌克兰","乌兹别克斯坦","乌干达","乌拉圭","乍得","也门","亚美尼亚","以色列","伊拉克","伊朗","伯利兹","佛得角","俄罗斯","保加利亚","克罗地亚","关岛","冈比亚","冰岛","几内亚","几内亚比绍","列支敦士登","刚果","刚果民主共和国","利比亚","利比里亚","加拿大","加纳","加蓬","匈牙利","北马里亚纳群岛共荣邦","南乔治亚和南德桑威奇群岛","南斯拉夫","南极洲","南苏丹","南非","博奈尔岛、圣尤斯达蒂斯和萨巴岛","博茨瓦纳","卡塔尔","卢旺达","卢森堡","印度","印度尼西亚","危地马拉 (瓜地馬拉)","厄瓜多尔","厄立特里亚","叙利亚","古巴","台湾","史瓦济兰","吉尔吉斯斯坦","吉布提","吐瓦鲁","哈萨克斯坦","哥伦比亚","哥斯达黎加","喀麦隆","土库曼","土耳其","圣卢西亚","圣基茨和尼维斯","圣多美普林西比","圣尤斯塔提马斯岛","圣巴特勒米岛","圣巴特岛","圣文森特和格林纳丁斯","圣皮埃尔和密克隆岛","圣诞岛","圣赫勒那","圣马丁","圣马丁岛","圣马力诺","圭亚那","坦桑尼亚","埃及","埃塞俄比亚","基里巴斯","塔吉克斯坦","塞内加尔","塞尔维亚","塞尔维亚和黑山","塞拉利昂","塞浦路斯","塞舌尔","墨西哥","多哥","多米尼克","多米尼加共和国","奥地利","委内瑞拉","孟加拉国","安哥拉","安圭拉","安提瓜和巴布达","安道尔","密克罗尼西亚","尼加拉瓜","尼日利亚","尼日尔","尼泊尔","尼维斯岛","巴勒斯坦","巴哈马","巴基斯坦","巴巴多斯","巴布亚新几内亚","巴拉圭","巴拿马","巴林","巴西","布基纳法索","布韦岛","希腊","帕劳","库克群岛","库拉索岛","库拉索岛(荷兰)","开曼群岛","德国","意大利","所罗门群岛","扎伊尔","托克劳","拉脱维亚","挪威","捷克共和国","摩尔多瓦","摩洛哥","摩纳哥","文莱达鲁萨兰国","斐济","斯洛伐克","斯洛文尼亚","斯瓦尔巴特和扬马延群岛","斯里兰卡","新加坡","新喀里多尼亚","新西兰","日本","智利","曼岛","朝鲜","未来之城","柬埔寨","根西岛","格林纳达","格陵兰","格鲁吉亚","梵帝冈","比利时","毛里塔尼亚","毛里求斯","汤加","沙特阿拉伯","法国","法属南方领土","法属波利尼西亚","法屬圭亞那","法罗群岛","波兰","波内尔岛","波多黎各","波斯尼亚 - 黑塞哥维那","泰国","泽西岛","津巴布韦","洪都拉斯","海地","澳大利亚","澳门","爱尔兰","爱沙尼亚","牙买加","特克斯和凯科斯群岛","特立尼达和多巴哥","特里斯坦-达库尼亚群岛","玻利维亚","瑙鲁","瑞典","瑞士","瓜德罗普","瓦利斯和富图纳群岛","瓦努阿图","留尼汪","白俄罗斯","百慕达群岛","皮特开恩群岛","直布罗陀","福克兰群岛（马尔维纳斯）","科威特","科摩罗","科特迪瓦","科科斯（基林）群岛","秘鲁","突尼斯","立陶宛","索马里","约旦","纳米比亚","纽埃","缅甸","罗马尼亚","美国","美国边远小岛","美属萨摩亚","老挝","肯尼亚","芬兰","苏丹","苏里南","英国","英联邦的印度洋领域","荷兰","荷属安的列斯","莫桑比克","莱索托","菲律宾","萨尔瓦多","萨摩亚","葡萄牙","蒙古","蒙特塞拉特岛","蒲隆地","西撒哈拉","西班牙","西班牙加那利群岛","诺福克岛","贝宁","赞比亚","赤道几内亚","赫德和麦克唐纳群岛","越南","阿兰群岛","阿塞拜疆","阿富汗","阿尔及利亚","阿尔巴尼亚","阿拉伯联合酋长国","阿曼","阿根廷","阿森松岛","阿鲁巴","韩国","香港","马其顿","马尔代夫","马拉维","马提尼克","马来西亚","马约特岛","马绍尔群岛","马耳他","马达加斯加","马里","黎巴嫩","黑山");

    @Override
    public void mapAttributeValue(ProductSpuModel spuModel, CategoryAttributeModel categoryAttributeModel) {
        if (CollectionUtils.isEmpty(categoryAttributeModel.getOptions())) {
            categoryAttributeModel.setOptions(Lists.newArrayList());
            for (int i = 0; i < countryEnum.size(); i++) {
                categoryAttributeModel.getOptions().add(AttributeValueModel.of(countryEnum.get(i), countryEnumName.get(i)));
            }
        }
    }

    @Override
    public void mapAttributeSchema(CategoryAttributeModel categoryAttributeModel, ProductTypeSchemaItem schemaItem) {
        // 处理country_of_origin的options
        if (CollectionUtils.isEmpty(categoryAttributeModel.getOptions())) {
            ProductTypeSchemaItem valueSchemaItem = schemaItem.getItems().getProperties().get("value");
            categoryAttributeModel.setOptions(Lists.newArrayList());
            for (int i = 0; i < valueSchemaItem.getEnumValues().size(); i++) {
                categoryAttributeModel.getOptions().add(AttributeValueModel.of(valueSchemaItem.getEnumValues().get(i), valueSchemaItem.getEnumNames().get(i)));
            }
        }
    }

    @Override
    public String getAttribute() {
        return "country_of_origin";
    }
}
