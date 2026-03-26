package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 广告属性 DO
 *
 * @author 翰展科技
 */
@TableName("ads_ad_attribute")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAdAttributeDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adId;

    private String attrType;

    private String attrKey;

    private String attrValue;

    private String attrValueClass;

}
