package com.svm.backend.modules.ums.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.common.api.CommonPage;
import com.svm.backend.common.api.CommonResult;
import com.svm.backend.modules.ums.mapper.UmsEventLogMapper;
import com.svm.backend.modules.ums.model.UmsAdmin;
import com.svm.backend.modules.ums.model.UmsEventLog;
import com.svm.backend.modules.ums.model.UmsResource;
import com.svm.backend.modules.ums.service.UmsResourceService;
import com.svm.backend.modules.utils.IpUtil;
import com.svm.backend.security.component.DynamicSecurityMetadataSource;
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
 * 後台資源管理Controller
 *
 * @author : kevin Chang
 */
@Controller
@RequestMapping("/resource")
@Slf4j
public class UmsResourceController {

    @Autowired
    private UmsResourceService resourceService;
    @Autowired
    private DynamicSecurityMetadataSource dynamicSecurityMetadataSource;

    @Autowired
    private UmsEventLogMapper umsEventLogMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestBody UmsResource umsResource) {

        StopWatch sw = new StopWatch();
        sw.start("create Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        boolean success = resourceService.create(umsResource);
        dynamicSecurityMetadataSource.clearDataSource();

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/resource/create", 1, "成功", memo);
            return CommonResult.success(null);
        } else {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/resource/create", 1, "成功", memo);
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
            @RequestBody UmsResource umsResource) {

        StopWatch sw = new StopWatch();
        sw.start("create Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        boolean success = resourceService.update(id, umsResource);
        dynamicSecurityMetadataSource.clearDataSource();

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/resource/update", 1, "成功", memo);
            return CommonResult.success(null);
        } else {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/resource/update", 0, "失敗", memo);
            return CommonResult.failed();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsResource> getItem(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id) {

        StopWatch sw = new StopWatch();
        sw.start("create Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        UmsResource umsResource = resourceService.getById(id);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/resource/" + id, 1, "成功", memo);

        return CommonResult.success(umsResource);
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

        boolean success = resourceService.delete(id);
        dynamicSecurityMetadataSource.clearDataSource();

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/resource/delete/" + id, 1, "成功", memo);
            return CommonResult.success(null);
        } else {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/resource/delete/" + id, 0, "失敗", memo);
            return CommonResult.failed();
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsResource>> list(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String nameKeyword,
            @RequestParam(required = false) String urlKeyword,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        StopWatch sw = new StopWatch();
        sw.start("create Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);


        Page<UmsResource> resourceList = resourceService.list(categoryId, nameKeyword, urlKeyword, pageSize, pageNum);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/resource/list" , 1, "成功", memo);

        return CommonResult.success(CommonPage.restPage(resourceList));
    }

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsResource>> listAll(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent) {

        StopWatch sw = new StopWatch();
        sw.start("create Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        List<UmsResource> resourceList = resourceService.list();

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/resource/listAll" , 1, "成功", memo);

        return CommonResult.success(resourceList);
    }

    private int insertEventLog(
            Long userId, String username, String ipAddress, String method,
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
