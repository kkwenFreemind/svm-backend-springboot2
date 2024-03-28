package com.svm.backend.modules.dms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.dms.dto.DmsDeviceParam;
import com.svm.backend.modules.dms.model.DmsDevice;

/**
 * @author kevinchang
 */
public interface DmsDeviceService extends IService<DmsDevice> {


    /**
     * 根據設備名稱或暱稱分頁查詢
     *
     * @param keyword
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<DmsDevice> getDeviceList( String keyword, Integer pageSize, Integer pageNum);

    /**
     * 新增設備
     *
     * @param dmsDeviceParam
     * @return
     */
    DmsDevice create(DmsDeviceParam dmsDeviceParam);

    /**
     * 更新設備資訊
     *
     * @param id
     * @param device
     * @return
     */
    boolean update(Long id, DmsDevice device);

    /**
     * 刪除設備資訊
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

}
