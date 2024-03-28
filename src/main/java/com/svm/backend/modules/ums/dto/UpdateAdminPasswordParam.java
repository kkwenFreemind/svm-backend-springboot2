package com.svm.backend.modules.ums.dto;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * 修改用户名稱密碼參數
 *
 * @author : kevin Chang
 */
@Getter
@Setter
public class UpdateAdminPasswordParam {

    @NotEmpty
    private String username;

    @NotEmpty
    private String oldPassword;

    @NotEmpty
    @Length(min = 8, max = 10, message = "密碼長度需為8~13")
    private String newPassword;

}
