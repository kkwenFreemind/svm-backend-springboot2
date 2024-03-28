package com.svm.backend.modules.ums.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 後台用戶
 *
 * @author : kevin Chang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_admin")
public class UmsAdmin implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    @Schema(description = "電郵")
    private String email;

    @Schema(description = "手機號碼")
    private String mobile;

    @Schema(description = "暱稱")
    private String nickName;

    @Schema(description = "備註")
    private String note;

    @Schema(description = "創建時間")
    private Date createTime;

    @Schema(description = "異動時間")
    private Date updateTime;

    @Schema(description = "最新登入時間")
    private Date loginTime;

    @Schema(description = "登出時間")
    private Date logoutTime;

    @Schema(description = "帳號狀態：0->禁用；1->啟用")
    private Integer status;

    @Schema(description = "組織代碼")
    private Long orgId;

    @TableField(exist = false)
    @Schema(description = "組織編碼")
    private Long orgSn;

    @TableField(exist = false)
    @Schema(description = "組織名稱")
    private String orgName;



    @Schema(description = "創建者ID")
    private Long createBy;

    @Schema(description = "創建者")
    private String createName;

    @Schema(description = "異動者ID")
    private Long updateBy;

    @Schema(description = "異動者")
    private String updateName;
}
