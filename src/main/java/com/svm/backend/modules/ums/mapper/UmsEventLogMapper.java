package com.svm.backend.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.modules.ums.model.UmsEventLog;
import org.apache.ibatis.annotations.Param;

/**
 * @author kevinchang
 */
public interface UmsEventLogMapper extends BaseMapper<UmsEventLog> {

    /**
     * Fech Event By userId
     * @param page
     * @param userId
     * @return
     */
    Page<UmsEventLog> getEventLogByUserId(Page<UmsEventLog> page, @Param("userId") Long userId);

}
