package com.svm.backend.modules.dashboard.dto;

import lombok.Data;

/**
 * @author kevinchang
 */
@Data
public class EventsCount3 {

    private String name;
    private Integer deliverEvent;
    private Integer tempEvent;
    private Integer offlineEvent;
}
