package com.svm.backend.modules.dashboard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.common.api.CommonPage;
import com.svm.backend.common.api.CommonResult;
import com.svm.backend.modules.dashboard.dto.SalesCount;
import com.svm.backend.modules.dashboard.dto.TimeAmount;
import com.svm.backend.modules.dashboard.model.Sales;
import com.svm.backend.modules.dashboard.service.SalesService;
import com.svm.backend.modules.ums.mapper.UmsEventLogMapper;
import com.svm.backend.modules.ums.model.UmsAdmin;
import com.svm.backend.modules.ums.model.UmsEventLog;
import com.svm.backend.modules.utils.IpUtil;
import com.svm.backend.security.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * @author kevinchang
 */
@Controller
@RequestMapping("/sales")
@Slf4j
@Tag(name = "Sales Controller")
public class SalesController {

    @Autowired
    private SalesService salesService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UmsEventLogMapper umsEventLogMapper;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<Sales>> list(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "startDateTime", required = false) String startDateTime,
            @RequestParam(value = "endDateTime", required = false) String endDateTime,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        StopWatch sw = new StopWatch();
        sw.start("list Start");
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        //取得呼叫者的資訊
        log.info("pageSize:"+pageSize+",pageNum"+pageNum);
        log.info("startDateTime:"+startDateTime);
        log.info("endDateTime:"+endDateTime);
        Page<Sales> salesList = salesService.list(keyword,startDateTime,endDateTime,pageSize,pageNum);
        log.info("result:"+salesList.getSize());

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/sales/list", 1, "成功", memo);


        return CommonResult.success(CommonPage.restPage(salesList));
    }

    @RequestMapping(value = "/getSaleProdCount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<SalesCount>> getSaleProdCount(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        StopWatch sw = new StopWatch();
        sw.start("register Start");
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        List<SalesCount> prodCountPage = salesService.getSalesProdCount();

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/sales/getSaleProdCount", 1, "成功", memo);


        return CommonResult.success(prodCountPage);

    }

    @RequestMapping(value = "/getHotMachineCount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<SalesCount>> getHotMachineCount(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        StopWatch sw = new StopWatch();
        sw.start("getHotMachineCount Start");
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        List<SalesCount> hotMachineList = salesService.getHotMachineCount();

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/sales/getHotMachineCount", 1, "成功", memo);

        return CommonResult.success(hotMachineList);

    }

    @RequestMapping(value = "/getSalesAmount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getSalesAmount(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        StopWatch sw = new StopWatch();
        sw.start("getSalesAmount Start");
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        List<TimeAmount> weekList = salesService.getSalesAmountLastWeek();
        List<TimeAmount> monthList = salesService.getSalesAmountLastMonth();

        //Last Week
        List<String> sumListWeek = new ArrayList<>();
        List<String> timeListWeek = new ArrayList<>();
        for (TimeAmount weekData : weekList) {
            sumListWeek.add(weekData.getAmount().toString());
            timeListWeek.add(weekData.getTime());
        }

        //Last Month
        List<String> sumListMonth = new ArrayList<>();
        List<String> timeListMonth= new  ArrayList<>();
        for (TimeAmount monthData : monthList) {
            sumListMonth.add(monthData.getAmount().toString());
            timeListMonth.add(monthData.getTime());
        }

        int initLenght = 10;
        Map<String, Object> map = new HashMap<>(initLenght);
        map.put("sumList_week", sumListWeek);
        map.put("timeList_week", timeListWeek);

        map.put("sumList_month",sumListMonth);
        map.put("timeList_month", timeListMonth);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/sales/getSalesAmount", 1, "成功", memo);


        return CommonResult.success(map);

    }


    @RequestMapping(value = "/getSalesCount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getSalesCount(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        StopWatch sw = new StopWatch();
        sw.start("getSalesCount Start");
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        List<TimeAmount> countList = salesService.getSalesCount();
        //Last Week
        List<String> sumList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        for (TimeAmount data : countList) {
            sumList.add(data.getAmount().toString());
            timeList.add(data.getTime());
        }

        int initLenght= 10;
        Map<String, Object> map = new HashMap<>(initLenght);
        map.put("sumList", sumList);
        map.put("timeList", timeList);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/sales/getSalesCount", 1, "成功", memo);


        return CommonResult.success(map);

    }
    @RequestMapping(value = "/getTodaySalesCount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getTodaySalesCount(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent){

        StopWatch sw = new StopWatch();
        sw.start("getSalesCount Start");
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        Integer result = salesService.getTodaySalesCount();

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/sales/getTodaySalesCount", 1, "成功", memo);


        return CommonResult.success(result);
    }

    /**
     * 紀錄事件
     *
     * @param username
     * @param userAgent
     * @param event
     * @param result
     * @return
     */
    private int insertEventLog(
            Long userId, String username, String ipAddress,String method,
            String userAgent, String event, Integer status, String result,
            String memo) {
        //EventLog的初始化
        try {
            UmsEventLog eventLog = new UmsEventLog();
            eventLog.setUserId(userId);
            eventLog.setUsername(username);
            eventLog.setIpAddress(ipAddress);
            eventLog.setRequestMethod(method);
            eventLog.setCreateTime(new Date());
            eventLog.setMemo(userAgent);
            eventLog.setEvent(event);
            eventLog.setStatus(status);
            eventLog.setResult(result);
            eventLog.setMemo(memo);
            return umsEventLogMapper.insert(eventLog);
        } catch (Exception ex) {
            log.info(String.valueOf(ex));
            return -1;
        }
    }
}