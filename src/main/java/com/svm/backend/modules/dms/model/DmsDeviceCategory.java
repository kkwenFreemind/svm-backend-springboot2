package com.svm.backend.modules.dms.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 設備分類
 *
 * @author Kevin Chang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dms_device_category")
public class DmsDeviceCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "創建時間")
    private Date createTime;

    @Schema(description = "分類名稱")
    private String name;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "備註")
    private String note;

}
