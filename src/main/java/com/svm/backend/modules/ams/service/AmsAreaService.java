package com.svm.backend.modules.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.ams.model.AmsArea;

import java.util.List;

/**
 * @author kevinchang
 */

public interface AmsAreaService extends IService<AmsArea> {

    /**
     * Fetch Data by AreaId
     * @param id areaId
     * @return AmsArea
     */
    AmsArea getAreaById(Long id);


    /**
     * Fetch All Area Data
     * @return List
     */
    List<AmsArea> listAll();

    /**
     * Fetch Area Data By CityId
     * @param cityId
     * @return List
     */
    List<AmsArea> listAreaByCityId(Long cityId);

    /**
     * Fetch Area Data By CityId
     * @param cityId
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<AmsArea> listAreaByCityId(Long cityId, Integer pageSize, Integer pageNum);

    /**
     * Fetch Area Data By Keyyword
     * @param keyword
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<AmsArea> list(String keyword, Integer pageSize, Integer pageNum);
}
