package com.svm.backend.modules.ums.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * 用户登入參數
 *
 * @author : kevin Chang
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UmsAdminLoginParam {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

//    private Long invoiceNumber;
//    private String domain;
}
