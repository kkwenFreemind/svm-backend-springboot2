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
@TableName("events")
public class Events implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    @Schema(description = "事件名稱")
    private String type;

    @Schema(description = "智範機編號")
    private String machine;

    @Schema(description = "商品名稱")
    private String name;

    @Schema(description = "cpname")
    private String cpname;

    @Schema(description = "事件日期")
    private Date eventTime;


}
