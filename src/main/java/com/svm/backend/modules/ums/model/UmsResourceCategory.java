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
 * 資源分類
 *
 * @author : kevin Chang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_resource_category")

public class UmsResourceCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "創建時間")
    private Date createTime;

    @Schema(description = "分類名稱")
    private String name;

    @Schema(description = "排序")
    private Integer sort;


}
