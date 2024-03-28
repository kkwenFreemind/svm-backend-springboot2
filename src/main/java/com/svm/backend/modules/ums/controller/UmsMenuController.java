package com.svm.backend.modules.ums.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.common.api.CommonPage;
import com.svm.backend.common.api.CommonResult;
import com.svm.backend.modules.ums.dto.UmsMenuNode;
import com.svm.backend.modules.ums.mapper.UmsEventLogMapper;
import com.svm.backend.modules.ums.model.UmsAdmin;
import com.svm.backend.modules.ums.model.UmsEventLog;
import com.svm.backend.modules.ums.model.UmsMenu;
import com.svm.backend.modules.ums.service.UmsMenuService;
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
 * 後台菜單管理Controller
 *
 * @author : kevin Chang
 */
@Controller
@Slf4j
@RequestMapping("/menu")
public class UmsMenuController {

    @Autowired
    private UmsMenuService menuService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UmsEventLogMapper umsEventLogMapper;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestBody UmsMenu umsMenu) {

        StopWatch sw = new StopWatch();
        sw.start("create Start");

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        boolean success = menuService.create(umsMenu);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        //新增事件到Table(ums_event_log)

        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/menu/create" , 1, "成功", memo);
            return CommonResult.success(null);
        } else {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/menu/create" , 0, "失敗", memo);
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
            @RequestBody UmsMenu umsMenu) {

        StopWatch sw = new StopWatch();
        sw.start("update Start");

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        boolean success = menuService.update(id, umsMenu);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/menu/update/id ", 1, "成功", memo);
            return CommonResult.success(null);
        } else {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/menu/update/id", 0, "失敗", memo);
            return CommonResult.failed();
        }
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsMenu> getItem(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id) {

        StopWatch sw = new StopWatch();
        sw.start("get id Start");

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        UmsMenu umsMenu = menuService.getById(id);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/menu/id", 1, "成功", memo);

        return CommonResult.success(umsMenu);
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id) {

        StopWatch sw = new StopWatch();
        sw.start("delete Start");

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        boolean success = menuService.removeById(id);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";


        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/menu/delete/id " , 1, "成功", memo);
            return CommonResult.success(null);
        } else {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/menu/delete/id " , 0, "失敗", memo);
            return CommonResult.failed();
        }
    }

    @RequestMapping(value = "/list/{parentId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsMenu>> list(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long parentId,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        StopWatch sw = new StopWatch();
        sw.start("list Start");

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        Page<UmsMenu> menuList = menuService.list(parentId, pageSize, pageNum);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/menu/list/parentId/"+parentId , 1, "成功", memo);

        return CommonResult.success(CommonPage.restPage(menuList));
    }


    @RequestMapping(value = "/treeList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsMenuNode>> treeList(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent) {

        StopWatch sw = new StopWatch();
        sw.start("treeList Start");

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        List<UmsMenuNode> list = menuService.treeList();
        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/menu/treeList" , 1, "成功", memo);

        return CommonResult.success(list);
    }

    @RequestMapping(value = "/updateHidden/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateHidden(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id,
            @RequestParam("hidden") Integer hidden) {

        StopWatch sw = new StopWatch();
        sw.start("updateHidden Start");

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        boolean success = menuService.updateHidden(id, hidden);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/menu/updateHidden", 1, "成功", memo);
            return CommonResult.success(null);
        } else {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/menu/updateHidden", 0, "失敗", memo);
            return CommonResult.failed();
        }
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
