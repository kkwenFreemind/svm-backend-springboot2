package com.svm.backend.modules.dashboard.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.svm.backend.modules.dashboard.dto.EventsCount;
import com.svm.backend.modules.dashboard.dto.EventsCount3;
import com.svm.backend.modules.dashboard.mapper.EventsMapper;
import com.svm.backend.modules.dashboard.model.Events;
import com.svm.backend.modules.dashboard.service.EventsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kevinchang
 */
@Service
@Slf4j
public class EventsServiceImpl extends ServiceImpl<EventsMapper, Events> implements EventsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsServiceImpl.class);

    @Autowired
    private EventsMapper eventsMapper;


    @Override
    public Page<Events> list(String keyword, String startDateTime, String endDateTime, Integer pageSize, Integer pageNum) {
        Page<Events> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Events> wrapper = new QueryWrapper<>();

        if (StrUtil.isNotEmpty(keyword) && StrUtil.isNotEmpty(startDateTime) && StrUtil.isNotEmpty(endDateTime)) {
            LambdaQueryWrapper<Events> lambda = wrapper.lambda();
            lambda.like(Events::getCpname, keyword);
            lambda.between(Events::getEventTime, startDateTime, endDateTime);

        } else if (StrUtil.isNotEmpty(keyword)) {
            LambdaQueryWrapper<Events> lambda = wrapper.lambda();
            lambda.like(Events::getCpname, keyword)
                    .or().like(Events::getType, keyword)
                    .or().like(Events::getName, keyword);

        } else if (StrUtil.isNotEmpty(startDateTime) && StrUtil.isNotEmpty(endDateTime)) {
            LambdaQueryWrapper<Events> lambda = wrapper.lambda();
            lambda.between(Events::getEventTime, startDateTime, endDateTime);
        } else {
            wrapper.lambda().orderByDesc(Events::getEventTime);
        }
        return page(page, wrapper);
    }

    @Override
    public List<EventsCount> getEventFail(String eventType) {
        return eventsMapper.getEventFail(eventType);
    }

    @Override
    public List<EventsCount> getDeliverEventFail(String eventType) {
        return eventsMapper.getDeliverEventFail(eventType);
    }

    @Override
    public List<EventsCount3> getEventCount() {
        return eventsMapper.getEventCount();
    }
}
