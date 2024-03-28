package com.svm.backend.modules.ums.controller;


import com.svm.backend.common.api.CommonResult;
import com.svm.backend.modules.ums.mapper.UmsEventLogMapper;
import com.svm.backend.modules.ums.model.UmsAdmin;
import com.svm.backend.modules.ums.model.UmsEventLog;
import com.svm.backend.modules.ums.model.UmsResourceCategory;
import com.svm.backend.modules.ums.service.UmsResourceCategoryService;
import com.svm.backend.modules.utils.IpUtil;
import com.svm.backend.security.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 後台資源分類管理Controller
 *
 * @author : kevin Chang
 */
@Controller
@Slf4j
@RequestMapping("/resourceCategory")
public class UmsResourceCategoryController {
    @Autowired
    private UmsResourceCategoryService resourceCategoryService;

    @Autowired
    private UmsEventLogMapper umsEventLogMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsResourceCategory>> listAll(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent) {

        StopWatch sw = new StopWatch();
        sw.start("create Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        List<UmsResourceCategory> resourceList = resourceCategoryService.listAll();

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/resourceCategory/listAll" , 1, "成功", memo);

        return CommonResult.success(resourceList);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestBody UmsResourceCategory umsResourceCategory) {

        StopWatch sw = new StopWatch();
        sw.start("create Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        boolean success = resourceCategoryService.create(umsResourceCategory);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/resourceCategory/create" , 1, "成功", memo);
            return CommonResult.success(null);
        } else {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/resourceCategory/create" , 0, "失敗", memo);
            return CommonResult.failed();
        }
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id,
            @RequestBody UmsResourceCategory umsResourceCategory) {

        StopWatch sw = new StopWatch();
        sw.start("create Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        umsResourceCategory.setId(id);
        boolean success = resourceCategoryService.updateById(umsResourceCategory);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/resourceCategory/update/"+id , 1, "成功", memo);
            return CommonResult.success(null);
        } else {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/resourceCategory/update/"+id , 0, "失敗", memo);
            return CommonResult.failed();
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id) {

        StopWatch sw = new StopWatch();
        sw.start("create Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        boolean success = resourceCategoryService.removeById(id);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/resourceCategory/delete/"+id , 1, "成功", memo);
            return CommonResult.success(null);
        } else {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/resourceCategory/delete/"+id , 0, "失敗", memo);
            return CommonResult.failed();
        }
    }

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
