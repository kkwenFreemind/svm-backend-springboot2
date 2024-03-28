package com.svm.backend.modules.dms.model;

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
 * @author kevinchang
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dms_device")
public class DmsDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "組織代碼")
    private Long orgId;

    @Schema(description = "設備類型代碼")
    private Long deviceType;

    @Schema(description = "城市代碼")
    private Integer cityId;

    @Schema(description = "行政區代碼")
    private Integer areaId;

    @Schema(description = "設備地址")
    private String address;

    @Schema(description = "設備編號")
    private String deviceSn;

    @Schema(description = "經度")
    private Double lat;

    @Schema(description = "緯度")
    private Double lng;

    @Schema(description = "創建日期")
    private Date createTime;

    @Schema(description = "設備狀態")
    private Integer status;

    @TableField(exist = false)
    @Schema(description = "設備類型名稱")
    private String deviceTypeName;

    @TableField(exist = false)
    @Schema(description = "組織編號")
    private String orgSn;

    @Schema(description = "別名")
    private String alisName;

    @TableField(exist = false)
    @Schema(description = "組織名稱")
    private String orgName;

    @TableField(exist = false)
    @Schema(description = "城市名稱")
    private String cityName;

    @TableField(exist = false)
    @Schema(description = "行政區名稱")
    private String areaName;

    @TableField(exist = false)
    @Schema(description = "完整地址")
    private String fullAddress;

}
