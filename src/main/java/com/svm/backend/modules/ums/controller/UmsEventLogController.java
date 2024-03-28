package com.svm.backend.modules.ums.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.common.api.CommonPage;
import com.svm.backend.common.api.CommonResult;
import com.svm.backend.modules.ums.mapper.UmsEventLogMapper;
import com.svm.backend.modules.ums.model.UmsAdmin;
import com.svm.backend.modules.ums.model.UmsEventLog;
import com.svm.backend.modules.ums.service.UmsEventLogService;
import com.svm.backend.modules.utils.IpUtil;
import com.svm.backend.security.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author kevinchang
 */
@Controller
@RequestMapping("/operate")
@Slf4j
@Tag(name = "UmsEventLog Controller")
public class UmsEventLogController {

    @Autowired
    UmsEventLogService umsEventLogService;

    @Autowired
    private UmsEventLogMapper umsEventLogMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsEventLog>> list(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        StopWatch sw = new StopWatch();
        sw.start("operate list Start");

        String ipAddress = IpUtil.getIpAddr(request);

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        log.info("userId:" + caller.getId());

        Page<UmsEventLog> eventList = umsEventLogService.list(caller.getId(), keyword, pageSize, pageNum);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        log.info(caller.getId()+","+caller.getUsername()+","+ipAddress+","+request.getMethod()+","+userAgent+","+ "/operate/list"+","+ 1+","+ "成功"+","+ memo);

        return CommonResult.success(CommonPage.restPage(eventList));
    }

    @RequestMapping(value = "/listType", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsEventLog>> listType(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "startDateTime", required = false) String startDateTime,
            @RequestParam(value = "endDateTime", required = false) String endDateTime,
            @RequestParam(value = "logType", required = false) Integer logType,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        StopWatch sw = new StopWatch();
        sw.start("operate list Start");

        String ipAddress = IpUtil.getIpAddr(request);

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        log.info("userId:" + caller.getId());

        Page<UmsEventLog> eventList = umsEventLogService.listLogType(caller.getId(), keyword,startDateTime,endDateTime,logType, pageSize, pageNum);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        log.info(caller.getId()+","+caller.getUsername()+","+ ipAddress+","+request.getMethod()+","+userAgent+","+ "/operate/listType/"+logType +","+ 1+","+ "成功"+","+memo);

        return CommonResult.success(CommonPage.restPage(eventList));
    }

}
