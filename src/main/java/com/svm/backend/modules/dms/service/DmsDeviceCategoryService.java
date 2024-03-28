package com.svm.backend.modules.dms.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.dms.model.DmsDeviceCategory;

import java.util.List;

/**
 * 設備分類管理Service
 *
 * @author Kevin Chang
 */
public interface DmsDeviceCategoryService extends IService<DmsDeviceCategory> {

    /**
     * 獲得所有資源分類
     * @return
     */
    List<DmsDeviceCategory> listAll();

    /**
     * 創建資源分類
     * @param dmsDeviceCategory
     * @return
     */
    boolean create(DmsDeviceCategory dmsDeviceCategory);


    /**
     * 根據設備類型或備註，分頁查詢設備分類資訊
     *
     * @param keyword
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<DmsDeviceCategory> list(String keyword, Integer pageSize, Integer pageNum);

    /**
     * Fetch Device Info By DeviceType
     * @param deviceType
     * @return
     */
    DmsDeviceCategory getCateInfoById(Long deviceType);


}
