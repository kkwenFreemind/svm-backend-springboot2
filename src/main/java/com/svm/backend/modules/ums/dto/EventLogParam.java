package com.svm.backend.modules.ums.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @Author : Kevin Chang
 * @create 2023/8/8 上午9:52
 */
@Getter
@Setter
public class EventLogParam {

    @NotEmpty
    private Long callerId;

    @NotEmpty
    private String callerUsername;

    @NotEmpty
    private String ipAddress;

    @NotEmpty
    private String requestMethod;

    private String userAgent;

    @NotEmpty
    private String apiName;

    @NotEmpty
    private Integer status;

    private String statusWording;

    private String remark;

    @NotEmpty
    private Integer eventType;

}
