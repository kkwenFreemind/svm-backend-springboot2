package com.svm.backend.modules.ums.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * 用户登入參數
 *
 * @author : kevin Chang
 */
@Getter
@Setter
public class UmsAdminParam {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    private String icon;

    @Email
    private String email;

    private String mobile;

    private String nickName;

    private String note;

    private Long orgId;

    private String createName;

    private Long createBy;

}
