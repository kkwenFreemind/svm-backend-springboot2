package com.svm.backend.modules.dashboard.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author kevinchang
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sales")
public class Sales implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    @Schema(description = "門市名稱")
    private String cpname;

    @Schema(description = "智範機編號")
    private String machine;

    @Schema(description = "商品名稱")
    private String name;

    @Schema(description = "金額")
    private Integer amount;

    @Schema(description = "數量")
    private Integer volume;

    @Schema(description = "交易日期")
    private Date txTime;

    @Schema(description = "新增壓日期")
    private Date createTime;


}
