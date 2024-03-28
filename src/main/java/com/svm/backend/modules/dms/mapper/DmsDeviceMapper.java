package com.svm.backend.modules.dms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.modules.dms.model.DmsDevice;


/**
 * @author kevinchang
 */
public interface DmsDeviceMapper extends BaseMapper<DmsDevice> {

    /**
     * Fetch Device Info By CompanyID
     * @param page
     * @return
     */
    Page<DmsDevice> getDeviceList(Page<DmsDevice> page);

}
