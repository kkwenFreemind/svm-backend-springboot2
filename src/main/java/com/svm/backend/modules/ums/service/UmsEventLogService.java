package com.svm.backend.modules.ums.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.ums.model.UmsEventLog;

/**
 * @author kevinchang
 */
public interface UmsEventLogService extends IService<UmsEventLog> {

    /**
     * Fetch Data By userId or keyword
     * @param userId
     * @param keyword
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<UmsEventLog> list(Long userId, String keyword, Integer pageSize, Integer pageNum);


    /**
     * Fetch Data By userId or keyword
     *
     * @param userId
     * @param keyword
     * @param logType
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<UmsEventLog> listLogType(Long userId, String keyword, String startDateTime, String endDateTime,Integer logType, Integer pageSize, Integer pageNum);

}
