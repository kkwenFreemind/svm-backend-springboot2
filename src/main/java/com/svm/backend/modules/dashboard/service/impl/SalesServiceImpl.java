package com.svm.backend.modules.dashboard.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.svm.backend.modules.dashboard.dto.SalesCount;
import com.svm.backend.modules.dashboard.dto.TimeAmount;
import com.svm.backend.modules.dashboard.mapper.SalesMapper;
import com.svm.backend.modules.dashboard.model.Sales;
import com.svm.backend.modules.dashboard.service.SalesService;
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
public class SalesServiceImpl extends ServiceImpl<SalesMapper, Sales> implements SalesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesServiceImpl.class);

    @Autowired
    private SalesMapper salesMapper;


    @Override
    public Page<Sales> list(String keyword, String startDateTime, String endDateTime, Integer pageSize, Integer pageNum) {
        Page<Sales> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Sales> wrapper = new QueryWrapper<>();

        if(StrUtil.isNotEmpty(keyword) && StrUtil.isNotEmpty(startDateTime) && StrUtil.isNotEmpty(endDateTime)) {
            LambdaQueryWrapper<Sales> lambda = wrapper.lambda();
            lambda.between(Sales::getTxTime, startDateTime, endDateTime);
            lambda.like(Sales::getCpname, keyword);
        }else if (StrUtil.isNotEmpty(keyword)) {
            LambdaQueryWrapper<Sales> lambda = wrapper.lambda();
            lambda.like(Sales::getCpname, keyword);
            lambda.or().like(Sales::getName, keyword);
        }else if(StrUtil.isNotEmpty(startDateTime) && StrUtil.isNotEmpty(endDateTime)){
            LambdaQueryWrapper<Sales> lambda = wrapper.lambda();
            lambda.between(Sales::getTxTime, startDateTime , endDateTime);
        }else {
            wrapper.lambda().orderByDesc(Sales::getTxTime);
        }
        return page(page, wrapper);
    }

    @Override
    public Long getTodayAmount(String fromDate, String toDate) {
        return salesMapper.getTodayAmount(fromDate, toDate);
    }

    @Override
    public List<SalesCount> getSalesProdCount() {
        return salesMapper.getSalesProdCount();
    }

    @Override
    public List<SalesCount> getHotMachineCount() {
        return salesMapper.getHotMachine();
    }

    @Override
    public List<TimeAmount> getSalesAmount() {
        return salesMapper.getSalesAmount();
    }
    public List<TimeAmount> getSalesAmountLastWeek() {
        return salesMapper.getSalesAmountLastWeek();
    }
    public List<TimeAmount> getSalesAmountLastMonth() {
        return salesMapper.getSalesAmountLastMonth();
    }

    public List<TimeAmount> getSalesCount() {
        return salesMapper.getSalesCount();
    }

    public Integer getTodaySalesCount() {
        return salesMapper.getTodaySalesCount();
    }


}
