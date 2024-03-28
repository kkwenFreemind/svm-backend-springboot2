package com.svm.backend.modules.oms.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 組織資訊
 *
 * @author kevinchang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oms_organization")
public class OmsOrganization implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "上層組織代碼")
    private Long parentId;

    @Schema(description = "組織編號")
    private String nameSn;

    @Schema(description = "組織名稱")
    private String name;

    @Schema(description = "層級")
    private Integer level;

    @Schema(description = "狀態")
    private Integer status;

    @Schema(description = "創建時間")
    private Date createTime;

    @Schema(description = "排序")
    private Integer sort;

    @Override
    public String toString(){

        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        StringBuilder dataBuilder = new StringBuilder();
//        appendFieldValue(dataBuilder, id.toString());
        appendFieldValue(dataBuilder, simpleDateFormat.format(createTime));
        appendFieldValue(dataBuilder, name);
        appendFieldValue(dataBuilder, parentId.toString());
        appendFieldValue(dataBuilder, nameSn);
        appendFieldValue(dataBuilder, name);
        appendFieldValue(dataBuilder, level.toString());
        appendFieldValue(dataBuilder, status.toString());

        return dataBuilder.toString();
    }

    private void appendFieldValue(StringBuilder dataBuilder, String fieldValue) {
        if(fieldValue != null) {
            dataBuilder.append(fieldValue).append(",");
        } else {
            dataBuilder.append("").append(",");
        }
    }
}
