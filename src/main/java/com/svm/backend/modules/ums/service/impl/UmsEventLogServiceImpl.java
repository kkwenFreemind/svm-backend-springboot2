package com.svm.backend.modules.ums.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.svm.backend.modules.ums.mapper.UmsEventLogMapper;
import com.svm.backend.modules.ums.model.UmsEventLog;
import com.svm.backend.modules.ums.service.UmsEventLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author kevinchang
 */
@Service
@Slf4j
public class UmsEventLogServiceImpl extends ServiceImpl<UmsEventLogMapper, UmsEventLog> implements UmsEventLogService {

    @Autowired
    UmsEventLogMapper umsEventLogMapper;

    /**
     * list
     *
     * @param userId
     * @param keyword
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public Page<UmsEventLog> list(Long userId, String keyword, Integer pageSize, Integer pageNum) {

        Page<UmsEventLog> page = new Page<>(pageNum, pageSize);
        QueryWrapper<UmsEventLog> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<UmsEventLog> lambda = wrapper.lambda();

        wrapper.lambda().eq(UmsEventLog::getUserId, userId)
                .orderByDesc(UmsEventLog::getId);

        if (StrUtil.isNotEmpty(keyword)) {
            lambda.like(UmsEventLog::getEvent, keyword).or()
                    .eq(UmsEventLog::getResult, keyword).or()
                    .like(UmsEventLog::getMemo,keyword);
        }

        return page(page, wrapper);

    }

    /**
     *
     * @param userId
     * @param keyword
     * @param logType
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public Page<UmsEventLog> listLogType(Long userId, String keyword, String startDateTime, String endDateTime,Integer logType, Integer pageSize, Integer pageNum) {

        Page<UmsEventLog> page = new Page<>(pageNum, pageSize);
        QueryWrapper<UmsEventLog> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<UmsEventLog> lambda = wrapper.lambda();

        wrapper.lambda().eq(UmsEventLog::getUserId, userId)
                .eq(UmsEventLog::getLogType,logType)
                .orderByDesc(UmsEventLog::getId);

        log.info("start/end DateTime====>"+startDateTime+","+endDateTime);

        if (StrUtil.isNotEmpty(keyword) && StrUtil.isNotEmpty(startDateTime) && StrUtil.isNotEmpty(endDateTime)) {
            lambda.like(UmsEventLog::getEvent, keyword).or()
                    .like(UmsEventLog::getResult, keyword).or()
                    .like(UmsEventLog::getMemo,keyword);

            lambda.between(UmsEventLog::getCreateTime, startDateTime, endDateTime);;

        } else if (StrUtil.isNotEmpty(keyword) ){
            lambda.like(UmsEventLog::getEvent, keyword).or()
                    .like(UmsEventLog::getResult, keyword).or()
                    .like(UmsEventLog::getMemo,keyword);
        }

        return page(page, wrapper);

    }
}
