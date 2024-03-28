package com.svm.backend.modules.ums.model;

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
 * 後台資源
 *
 * @author : kevin Chang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_resource")
public class UmsResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "創建時間")
    private Date createTime;

    @Schema(description = "資源名稱")
    private String name;

    @Schema(description = "資源URL")
    private String url;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "資源分類ID")
    private Long categoryId;

    @Override
    public String toString(){

        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        StringBuilder dataBuilder = new StringBuilder();
        appendFieldValue(dataBuilder, id.toString());
        appendFieldValue(dataBuilder, simpleDateFormat.format(createTime));
        appendFieldValue(dataBuilder, name);
        appendFieldValue(dataBuilder, url);
        appendFieldValue(dataBuilder, description);
        appendFieldValue(dataBuilder, categoryId.toString());

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
