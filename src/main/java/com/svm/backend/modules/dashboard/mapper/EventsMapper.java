package com.svm.backend.modules.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.svm.backend.modules.dashboard.dto.EventsCount;
import com.svm.backend.modules.dashboard.dto.EventsCount3;
import com.svm.backend.modules.dashboard.model.Events;

import java.util.List;

/**
 * @author kevinchang
 */
public interface EventsMapper extends BaseMapper<Events> {

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
