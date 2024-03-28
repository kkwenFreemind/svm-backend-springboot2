package com.svm.backend.modules.ums.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author kevinchang
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PayloadParam {


    private String sub;
    private Long created;
    private Long exp;
}
