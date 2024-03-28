package com.svm.backend.modules.dashboard.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.dashboard.dto.SalesCount;
import com.svm.backend.modules.dashboard.dto.TimeAmount;
import com.svm.backend.modules.dashboard.model.Sales;

import java.util.List;

/**
 * 銷售管理Service
 *
 * @author Kevin Chang
 */
public interface SalesService extends IService<Sales> {

    /**
     *
     * @param keyword
     * @param startDateTime
     * @param endDateTime
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<Sales> list(String keyword,String startDateTime, String endDateTime,Integer pageSize, Integer pageNum);

    /**
     * Get Today Amount
     * @param fromDate
     * @param toDate
     * @return
     */
    Long getTodayAmount(String fromDate, String toDate);

    /**
     * Get Amount By Product
     * @return
     */
    List<SalesCount> getSalesProdCount();

    /**
     * Get Amount By Machine
     * @return
     */
    List<SalesCount> getHotMachineCount();

    /**
     * Get Amount
     * @return
     */
    List<TimeAmount> getSalesAmount();

    /**
     * getSalesAmountLastWeek
     * @return
     */
    List<TimeAmount> getSalesAmountLastWeek();

    /**
     * getSalesAmountLastMonth
     * @return
     */
    List<TimeAmount> getSalesAmountLastMonth();

    /**
     * Get Amount
     * @return
     */
    List<TimeAmount> getSalesCount();

    /**
     * getTodaySalesCount
     * @return
     */
    Integer getTodaySalesCount();
}
