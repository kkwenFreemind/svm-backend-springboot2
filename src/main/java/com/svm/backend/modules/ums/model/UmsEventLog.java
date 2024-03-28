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
 * @author kevinchang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_event_log")
public class UmsEventLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "帳號Id")
    private Long userId;

    @Schema(description = "IP")
    private String ipAddress;

    @Schema(description = "請求類型")
    private String requestMethod;

    @Schema(description = "事件日期")
    private Date createTime;

    @Schema(description = "帳號")
    private String username;

    @Schema(description = "事件")
    private String event;

    @Schema(description = "結果：0->異常 1:->成功")
    private Integer status;

    @Schema(description = "結果描述")
    private String result;

    @Schema(description = "備註")
    private String memo;

    @Schema(description = "類型")
    private Integer logType;
}
