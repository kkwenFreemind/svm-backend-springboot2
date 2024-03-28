package com.svm.backend.modules.ums.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.common.api.CommonPage;
import com.svm.backend.common.api.CommonResult;
import com.svm.backend.modules.ums.mapper.UmsEventLogMapper;
import com.svm.backend.modules.ums.model.*;
import com.svm.backend.modules.ums.service.UmsAdminRoleRelationService;
import com.svm.backend.modules.ums.service.UmsRoleService;
import com.svm.backend.modules.utils.IpUtil;
import com.svm.backend.security.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 後台用户角色管理
 *
 * @author : kevin Chang
 */
@Controller
@RequestMapping("/role")
@Slf4j
@Tag(name = "UmsRole Controller")
public class UmsRoleController {
    @Autowired
    private UmsRoleService roleService;

    @Autowired
    private UmsAdminRoleRelationService umsAdminRoleRelationService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UmsEventLogMapper umsEventLogMapper;

    /**
     * 新增角色
     *
     * @param bearer
     * @param userAgent
     * @param role
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestBody UmsRole role) {

        StopWatch sw = new StopWatch();
        sw.start("logout Start");

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);
        boolean success = roleService.create(role);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        //新增事件到Table(ums_event_log)

        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/create", 1, "成功", memo);
            return CommonResult.success(null);
        }
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/create", 0, "失敗", memo);
        return CommonResult.failed();
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id,
            @RequestBody UmsRole role) {

        //取得呼叫者的資訊
        StopWatch sw = new StopWatch();
        sw.start("logout Start");

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        role.setId(id);
        boolean success = roleService.updateById(role);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/update/" + id, 1, "成功", memo);
            return CommonResult.success(null);
        }
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/update/" + id, 0, "失敗", memo);
        return CommonResult.failed();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam("ids") List<Long> ids) {

        //取得呼叫者的資訊
        StopWatch sw = new StopWatch();
        sw.start("delete Start");

        //取得呼叫者的資訊
        UmsAdmin admin = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        //是否有用戶使用該角色
        List<UmsAdminRoleRelation> userList = umsAdminRoleRelationService.getUserList(ids.get(0));
        if(userList.size()==0) {
            //該角色下的menu & resource 關聯是否該一起刪除
            int menu_count = roleService.deleteRoleMenu(ids.get(0), null);
            int resource_count = roleService.deleteRoleResource(ids.get(0), null);
            boolean success = roleService.delete(ids);

            sw.stop();
            String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

            if (success) {
                insertEventLog(admin.getId(), admin.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/delete/" + ids, 1, "成功", memo);
                return CommonResult.success(null);
            }
            insertEventLog(admin.getId(), admin.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/delete/" + ids, 0, "失敗", memo);
            return CommonResult.failed();
        }else{
            return CommonResult.failed("有用戶帳號使用該角色，無法刪除");
        }
    }


    /**
     *  Admin 畫面下拉式選單使用
     * @param request
     * @param bearer
     * @param userAgent
     * @return
     */
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsRole>> listAll(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent) {

        //取得呼叫者的資訊
        StopWatch sw = new StopWatch();
        sw.start("logout Start");

        //取得呼叫者的資訊
        UmsAdmin admin = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);
        //取得呼叫者的資訊

        List<UmsRole> roleList = roleService.listAllRole();

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        insertEventLog(admin.getId(), admin.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/listAll/", 1, "成功", memo);

        return CommonResult.success(roleList);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsRole>> list(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        StopWatch sw = new StopWatch();
        sw.start("list Start");

        //取得呼叫者的資訊
        UmsAdmin admin = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        Page<UmsRole> roleList = roleService.listAllRole(keyword, pageSize, pageNum);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        insertEventLog(admin.getId(), admin.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/list/", 1, "成功", memo);

        return CommonResult.success(CommonPage.restPage(roleList));
    }

    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id,
            @RequestParam(value = "status") Integer status) {


        StopWatch sw = new StopWatch();
        sw.start("updateStatus Start");

        //取得呼叫者的資訊
        UmsAdmin admin = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        UmsRole umsRole = new UmsRole();
        umsRole.setId(id);
        umsRole.setStatus(status);
        boolean success = roleService.updateById(umsRole);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        if (success) {
            insertEventLog(admin.getId(), admin.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/updateStatus/"+id, 1, "成功", memo);

            return CommonResult.success(null);
        }
        insertEventLog(admin.getId(), admin.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/updateStatus/"+id, 0, "失敗", memo);

        return CommonResult.failed();
    }

    @RequestMapping(value = "/listMenu/{roleId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsMenu>> listMenu(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long roleId) {

        StopWatch sw = new StopWatch();
        sw.start("updateStatus Start");

        //取得呼叫者的資訊
        UmsAdmin admin = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        List<UmsMenu> roleList = roleService.listMenu(roleId);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(admin.getId(), admin.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/listMenu/"+roleId, 1, "成功", memo);

        return CommonResult.success(roleList);
    }

    @RequestMapping(value = "/listResource/{roleId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsResource>> listResource(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long roleId) {

        StopWatch sw = new StopWatch();
        sw.start("updateStatus Start");

        //取得呼叫者的資訊
        UmsAdmin admin = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        List<UmsResource> roleList = roleService.listResource(roleId);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(admin.getId(), admin.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/listResource/"+roleId, 1, "成功", memo);

        return CommonResult.success(roleList);
    }

    @RequestMapping(value = "/allocMenu", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult allocMenu(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam Long roleId,
            @RequestParam List<Long> menuIds) {

        StopWatch sw = new StopWatch();
        sw.start("allocMenu Start");

        //取得呼叫者的資訊
        UmsAdmin admin = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        int count = roleService.allocMenu(roleId, menuIds);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(admin.getId(), admin.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/allocMenu/"+roleId, 1, "成功", memo);

        return CommonResult.success(count);
    }

    @RequestMapping(value = "/allocResource", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult allocResource(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam Long roleId,
            @RequestParam List<Long> resourceIds) {

        StopWatch sw = new StopWatch();
        sw.start("allocMenu Start");

        //取得呼叫者的資訊
        UmsAdmin admin = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        int count = roleService.allocResource(roleId, resourceIds);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(admin.getId(), admin.getUsername(), ipAddress, request.getMethod(), userAgent, "/role/allocResource/"+roleId, 1, "成功", memo);

        return CommonResult.success(count);
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
