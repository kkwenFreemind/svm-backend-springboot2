package com.svm.backend.modules.ums.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 後台用戶角色
 *
 * @author : kevin Chang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_role")

public class UmsRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "名稱")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "後台用户數目")
    private Integer adminCount;

    @Schema(description = "創建時間")
    private Date createTime;

    @Schema(description = "啟用狀態：0->停用；1->啟用")
    private Integer status;


    private Integer sort;


}
