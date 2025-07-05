package com.hzltd.module.erpls.api.model.common;

import lombok.Data;

@Data
public class SecurityModel {

    private String originCountry;

    private Boolean ifBattery;

    private BatteryModel batteryInfo;

}
