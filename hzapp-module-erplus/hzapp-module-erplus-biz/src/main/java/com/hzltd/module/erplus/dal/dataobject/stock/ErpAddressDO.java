package com.hzltd.module.erplus.dal.dataobject.stock;


import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

@TableName("erplus_address")
@KeySequence("erplus_address_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpAddressDO extends BaseDO {

    @TableId
    private Integer id;

    private String contactName;

    private String phoneNumber;

    private String email;

    private String postalCode;

    private String countryCode;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String districtOrCountry;


}
