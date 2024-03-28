package com.svm.backend.modules.dashboard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.common.api.CommonPage;
import com.svm.backend.common.api.CommonResult;
import com.svm.backend.modules.dashboard.dto.EventsCount;
import com.svm.backend.modules.dashboard.dto.EventsCount3;
import com.svm.backend.modules.dashboard.model.Events;
import com.svm.backend.modules.dashboard.service.EventsService;
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
@RequestMapping("/events")
@Slf4j
@Tag(name = "Events Controller")
public class EventsController {

    @Autowired
    private EventsService eventsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UmsEventLogMapper umsEventLogMapper;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<Events>> list(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "startDateTime", required = false) String startDateTime,
            @RequestParam(value = "endDateTime", required = false) String endDateTime,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {


        StopWatch sw = new StopWatch();
        sw.start("register Start");
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        //取得呼叫者的資訊
        log.info("pageSize:"+pageSize+",pageNum"+pageNum);
        log.info("startDateTime:"+startDateTime);
        log.info("endDateTime:"+endDateTime);
        Page<Events> eventList = eventsService.list(keyword,startDateTime,endDateTime,pageSize,pageNum);
        log.info("result:"+eventList.getSize());

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/events/list", 1, "成功", memo);

        return CommonResult.success(CommonPage.restPage(eventList));
    }

    @RequestMapping(value = "/getEventFail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<EventsCount>> getEventFail(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "eventType") String eventType,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        StopWatch sw = new StopWatch();
        sw.start("register Start");
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        //圓餅圖專用
        List<EventsCount> eventPage = eventsService.getEventFail(eventType);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/events/getEventFail", 1, "成功", memo);

        return CommonResult.success(eventPage);

    }

    @RequestMapping(value = "/getDeliverEventFail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<EventsCount>> getDeliverEventFail(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "eventType") String eventType) {

        StopWatch sw = new StopWatch();
        sw.start("register Start");
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);
        //圓餅圖專用
        List<EventsCount> eventPage = eventsService.getDeliverEventFail(eventType);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/events/getDeliverEventFail", 1, "成功", memo);


        return CommonResult.success(eventPage);
    }

    @RequestMapping(value = "/getEventCount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getEventCount(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent) {

        StopWatch sw = new StopWatch();
        sw.start("register Start");
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        List<EventsCount3> timeAndSumList = eventsService.getEventCount();

        List<String> deliveryList = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        List<String> offlineList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>(timeAndSumList.size());
        for (EventsCount3 data : timeAndSumList) {
            deliveryList.add(data.getDeliverEvent().toString());
            tempList.add(data.getTempEvent().toString());
            offlineList.add(data.getOfflineEvent().toString());
            timeList.add(data.getName());
        }
        map.put("deliveryList", deliveryList);
        map.put("tempList", tempList);
        map.put("offlineList", offlineList);
        map.put("timeList", timeList);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/events/getEventCount", 1, "成功", memo);

        return CommonResult.success(map);
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