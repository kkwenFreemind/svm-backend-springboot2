package com.svm.backend.modules.ams.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author kevinchang
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ams_area")
public class AmsArea implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "城市代碼")
    private Long cityId;

    @Schema(description = "行政區名稱")
    private String name;


}
