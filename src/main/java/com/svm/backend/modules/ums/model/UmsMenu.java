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
 * 後台菜單表
 *
 * @author : kevin Chang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_menu")
public class UmsMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "父級ID")
    private Long parentId;

    @Schema(description = "創建時間")
    private Date createTime;

    @Schema(description = "菜單名稱")
    private String title;

    @Schema(description = "菜單級數")
    private Integer level;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "前端名稱")
    private String name;

    @Schema(description = "前端圖標")
    private String icon;

    @Schema(description = "前端是否隱藏")
    private Integer hidden;


}
