package com.svm.backend.modules.dashboard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.dashboard.dto.EventsCount;
import com.svm.backend.modules.dashboard.dto.EventsCount3;
import com.svm.backend.modules.dashboard.model.Events;

import java.util.List;

/**
 * @author kevinchang
 */
public interface EventsService extends IService<Events> {

    /**
     *
     * Fetch Data By eventType
     * @param keyword
     * @param startDateTime
     * @param endDateTime
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<Events> list(String keyword, String startDateTime, String endDateTime, Integer pageSize, Integer pageNum);

    /**
     * Fetch Data By eventType
     * @param eventType
     * @return
     */
    List<EventsCount> getEventFail(String eventType);

    /**
     * Fetch Data By eventType
     * @param eventType
     * @return
     */
    List<EventsCount> getDeliverEventFail(String eventType);

    /**
     * Fetch All Data
     * @return
     */
    List<EventsCount3> getEventCount();
}
