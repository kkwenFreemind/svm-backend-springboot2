package com.svm.backend.modules.dms.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * 新增設備參數
 *
 * @author Kevin Chang
 */
@Getter
@Setter
public class DmsDeviceParam {

    @NotEmpty
    private String deviceSn;

    private Long deviceType;
    private Long orgId;
    private Integer cityId;
    private Integer areaId;

    private String address;


}
