package com.svm.backend.modules.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.svm.backend.modules.dashboard.dto.SalesCount;
import com.svm.backend.modules.dashboard.dto.TimeAmount;
import com.svm.backend.modules.dashboard.model.Sales;

import java.util.List;


/**
 * @author kevinchang
 */
public interface SalesMapper extends BaseMapper<Sales> {

    /**
     * Get Amount By Date
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
    List<SalesCount> getHotMachine();

    /**
     * Get Total Amount
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
     * Get Sales Data
     * @return
     */
    List<TimeAmount> getSalesCount();

    /**
     * Get Today Sales  Count
     * @return
     */
    Integer getTodaySalesCount();
}
