package com.svm.backend.modules.ums.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.common.api.CommonPage;
import com.svm.backend.common.api.CommonResult;
import com.svm.backend.modules.oms.model.OmsOrganization;
import com.svm.backend.modules.oms.service.OmsOrganizationService;
import com.svm.backend.modules.ums.dto.UmsAdminLoginParam;
import com.svm.backend.modules.ums.dto.UmsAdminParam;
import com.svm.backend.modules.ums.dto.UpdateAdminPasswordParam;
import com.svm.backend.modules.ums.mapper.UmsEventLogMapper;
import com.svm.backend.modules.ums.model.UmsAdmin;
import com.svm.backend.modules.ums.model.UmsEventLog;
import com.svm.backend.modules.ums.model.UmsRole;
import com.svm.backend.modules.ums.service.UmsAdminService;
import com.svm.backend.modules.ums.service.UmsRoleService;
import com.svm.backend.modules.utils.IpUtil;
import com.svm.backend.security.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.executable.ValidateOnExecution;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 後台帳號管理
 *
 * @author : kevin Chang
 */
@Controller
@RequestMapping("/admin")
@Slf4j
@Tag(name = "UmsAdmin Controllerr")
public class UmsAdminController {

    private static String COMPANY_ADMIN_ACCOUNT = "admin";
    private static int FAILED_PARAMETER_CHECK = -1;
    private static int NO_SUCH_ACCOUNT = -2;
    private static int OLD_PASSWORD_ERROR = -3;
    private static int INSUFFICIENT_PASSWORD_STRENGTH = -4;

    @Autowired
    private UmsEventLogMapper umsEventLogMapper;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private UmsAdminService adminService;

    @Autowired
    private UmsRoleService roleService;

    @Autowired
    private OmsOrganizationService omsOrganizationService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    /**
     * 新增一筆帳號
     *
     * @param bearer
     * @param userAgent
     * @param umsAdminParam
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    @ValidateOnExecution
    public CommonResult<UmsAdmin> register(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @Validated @RequestBody UmsAdminParam umsAdminParam) {

        StopWatch sw = new StopWatch();
        sw.start("register Start");

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        //判斷是否帳號重複
        UmsAdmin checkAdmin = adminService.getAdminByUsername(umsAdminParam.getUsername());

        if (checkAdmin == null) {

            umsAdminParam.setCreateName(caller.getUsername());
            umsAdminParam.setCreateBy(caller.getId());
            UmsAdmin umsAdmin = adminService.register(umsAdminParam);
            if (umsAdmin == null) {
                insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "新增帳號", 0, "失敗", "failed:無法新增" + umsAdminParam.getUsername() + "，請檢查欄位:",0);
                return CommonResult.failed("無法新增，請檢查欄位");
            }

            sw.stop();
            String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
            //新增事件到Table(ums_event_log)
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/admin/register " + umsAdmin.getUsername(), 1, "成功", memo,0);

            return CommonResult.success(umsAdmin);
        } else {
            return CommonResult.failed(umsAdminParam.getUsername() + "帳號已存在或為系統保留字，無法新增");
        }
    }

    /**
     * 登入
     *
     * @param userAgent
     * @param umsAdminLoginParam
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(
            HttpServletRequest request,
            @RequestHeader(value = "User-Agent") String userAgent,
            @Validated @RequestBody UmsAdminLoginParam umsAdminLoginParam) {

        StopWatch sw = new StopWatch();
        sw.start("Login Start");

        String username = umsAdminLoginParam.getUsername();
        String password = umsAdminLoginParam.getPassword();
        String ipAddress = IpUtil.getIpAddr(request);

        try {
            //透過帳號密碼，驗證成功後取得Token
            String token = adminService.login(username, password);

            if (token == null) {
                insertEventLog(999L, username, ipAddress,request.getMethod(),userAgent, "登入", 0, "失敗", "failed:" + username + "無法登入->" + userAgent,1);
                return CommonResult.validateFailed("無法登入");
            }

            Map<String, String> tokenMap = new HashMap<>(10);
            tokenMap.put("token", token);
            tokenMap.put("tokenHead", tokenHead);

            //更新登入時間
            UmsAdmin umsAdmin = adminService.getAdminByUsername(username);
            umsAdmin.setLoginTime(new Date());
            adminService.update(umsAdmin.getId(), umsAdmin);

            sw.stop();
            String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
            //新增事件到Table(ums_event_log)
            insertEventLog(umsAdmin.getId(), umsAdmin.getUsername(), ipAddress,request.getMethod(),userAgent, "/admin/login", 1, "成功",memo ,1);

            return CommonResult.success(tokenMap);

        } catch (Exception ex) {
            UmsAdmin badUser = adminService.getAdminByUsername(username);
            if (badUser == null) {
                insertEventLog(999L, username, ipAddress, request.getMethod(),userAgent, "登入", 0, "失敗", ex.toString(),1);
            } else {
                insertEventLog(badUser.getId(), username, ipAddress, request.getMethod(),userAgent, "登入", 0, "失敗", ex.toString(),1);
            }
            return CommonResult.failed();
        }
    }

    /**
     * 更新Token
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult refreshToken(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent) {

        StopWatch sw = new StopWatch();
        sw.start("refreshToken Start");

        String token = request.getHeader(tokenHeader);
        String ipAddress = IpUtil.getIpAddr(request);

        //取得呼叫者的資訊 caller
        String token2 = bearer;
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);

        String refreshToken = adminService.refreshToken(token);

        if (refreshToken == null) {
            //新增事件到Table(ums_event_log)
            sw.stop();
            String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/admin/refreshToken", 0, "失敗", "failed:Token已經過期;"+memo,0);
            return CommonResult.failed("Token已經過期！");
        }

        Map<String, String> tokenMap = new HashMap<>(10);
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        //新增事件到Table(ums_event_log)
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/admin/refreshToken", 1, "成功", memo,0);

        return CommonResult.success(tokenMap);
    }


    /**
     * 取得帳號資訊
     *
     * @param bearer
     * @param userAgent
     * @param principal
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getAdminInfo(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            Principal principal) {

        StopWatch sw = new StopWatch();
        sw.start("info Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        if (principal == null) {
            return CommonResult.unauthorized(null);
        }

        String username = principal.getName();

        UmsAdmin umsAdmin = adminService.getAdminByUsername(username);
        Map<String, Object> data = new HashMap<>(10);
        data.put("username", umsAdmin.getUsername());
        data.put("menus", roleService.getMenuList(umsAdmin.getId()));

        List<UmsRole> roleList = adminService.getRoleList(umsAdmin.getId());
        if (CollUtil.isNotEmpty(roleList)) {
            List<String> roles = roleList.stream().map(UmsRole::getName).collect(Collectors.toList());
            data.put("roles", roles);
        }

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        //新增事件到Table(ums_event_log)
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/admin/info", 1, "成功", memo,0);
        return CommonResult.success(data);
    }


    /**
     * 登出
     *
     * @param bearer
     * @param userAgent
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult logout(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent) {


        StopWatch sw = new StopWatch();
        sw.start("logout Start");
        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        //登出時間更新
        caller.setLogoutTime(new Date());
        adminService.update(caller.getId(), caller);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        //新增事件到Table(ums_event_log)
        insertEventLog(caller.getId(), caller.getUsername(),ipAddress, request.getMethod(),userAgent, "/admin/logout", 1, "成功", memo,1);

        return CommonResult.success("ok");
    }

    /**
     * 列舉該公司下的所有帳號
     *
     * @param bearer
     * @param keyword
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsAdmin>> list(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestHeader(value = "User-Agent") String userAgent) {

        StopWatch sw = new StopWatch();
        sw.start("list Start");

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);
        Page<UmsAdmin> adminList = adminService.list( keyword, pageSize, pageNum);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/admin/list", 1, "成功", memo,0);

        return CommonResult.success(CommonPage.restPage(adminList));
    }

    @RequestMapping(value = "/listMyAccount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsAdmin>> listMyAccount(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestHeader(value = "User-Agent") String userAgent) {

        StopWatch sw = new StopWatch();
        sw.start("listMyAccount Start");
        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        Page<UmsAdmin> adminList = adminService.listMyAccount(caller.getUsername(), pageSize, pageNum);
        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        insertEventLog(caller.getId(), caller.getUsername(),ipAddress,request.getMethod(), userAgent, "/admin/listMyAccount", 1, "成功", memo,0);

        return CommonResult.success(CommonPage.restPage(adminList));
    }

    /**
     * 依據帳號ID，取得該帳號的資訊
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsAdmin> getItem(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestHeader(value = "User-Agent") String userAgent) {

        StopWatch sw = new StopWatch();
        sw.start("listMyAccount Start");
        String ipAddress = IpUtil.getIpAddr(request);

        UmsAdmin admin = adminService.getById(id);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        insertEventLog(admin.getId(), admin.getUsername(),ipAddress,request.getMethod(), userAgent, "/admin/id", 1, "成功", memo,0);
        return CommonResult.success(admin);
    }

    /**
     * 依據帳號ID，更新該帳號的資訊
     *
     * @param bearer
     * @param id
     * @param admin
     * @return
     */
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id, @RequestBody UmsAdmin admin) {

        StopWatch sw = new StopWatch();
        sw.start("update Start");
        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        UmsAdmin user = adminService.getById(id);
        //取得組織代碼
        log.info("umsAdminParam :{}", admin.getOrgId());
        OmsOrganization omsOrganization = new OmsOrganization();
        omsOrganization = omsOrganizationService.getById(admin.getOrgId());

        admin.setUpdateTime(new Date());
        admin.setUpdateBy(caller.getId());
        admin.setUpdateName(caller.getUsername());

        boolean success = adminService.update(id, admin);
        if (success) {
            sw.stop();
            String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
            //新增事件到Table(ums_event_log)
            insertEventLog(caller.getId(), caller.getUsername(),ipAddress, request.getMethod(),userAgent, "/admin/update/" + id, 1, "成功", memo,0);
            return CommonResult.success(null);
        }
        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        //新增事件到Table(ums_event_log)
        insertEventLog(admin.getId(), admin.getUsername(),ipAddress,request.getMethod(), userAgent, "/admin/update/" + id, 1, "失敗", memo,0);

        return CommonResult.failed();
    }


    /**
     * 更新該用戶的密碼
     * 未更新公司類別
     *
     * @param updatePasswordParam
     * @return
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePassword(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @Validated @RequestBody UpdateAdminPasswordParam updatePasswordParam,
            @RequestHeader(value = "User-Agent") String userAgent) {

        StopWatch sw = new StopWatch();
        sw.start("updatePassword Start");
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);

        int status = adminService.updatePassword(updatePasswordParam, caller);
        String ipAddress = IpUtil.getIpAddr(request);

        /**
         *     private static int FAILED_PARAMETER_CHECK  = -1;
         *     private static int NO_SUCH_ACCOUNT = -2;
         *     private static int OLD_PASSWORD_ERROR=-3;
         *     private static int INSUFFICIENT_PASSWORD_STRENGTH = -4;
         */

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        if (status > 0) {
            insertEventLog(caller.getId(), caller.getUsername(),ipAddress, request.getMethod(),userAgent, "/admin/updatePassword", status, "成功", memo,0);

            return CommonResult.success(null);
        } else if (status == FAILED_PARAMETER_CHECK) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/admin/updatePassword", status, "未通過參數檢查", memo,0);
            return CommonResult.failed("未通過參數檢查");
        } else if (status == NO_SUCH_ACCOUNT) {
            insertEventLog(caller.getId(), caller.getUsername(),ipAddress, request.getMethod(),userAgent, "/admin/updatePassword", status, "無該用戶帳號", memo,0);
            return CommonResult.failed("無該用戶帳號");
        } else if (status == OLD_PASSWORD_ERROR) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/admin/updatePassword", status, "舊密碼錯誤", memo,0);
            return CommonResult.failed("舊密碼錯誤");
        } else if (status == INSUFFICIENT_PASSWORD_STRENGTH) {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/admin/updatePassword", status, "密碼強度不足", memo,0);
            return CommonResult.passwordError("密碼強度不足");
        } else {
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "POST:/admin/updatePassword", status, "失敗", memo,0);
            return CommonResult.failed();
        }
    }

    /**
     * 依據帳號ID，刪除該帳號的資訊
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id) {

        StopWatch sw = new StopWatch();
        sw.start("delete Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        UmsAdmin user = adminService.getById(id);
        String ipAddress = IpUtil.getIpAddr(request);

        if (user.getUsername().contains(COMPANY_ADMIN_ACCOUNT)) {
            return CommonResult.failed("系統帳號不可刪除");
        } else {
            boolean success = adminService.delete(id);
            int count = adminService.updateRole(id, null);
            sw.stop();
            String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
            if (success) {
                insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "/admin/delete/" + id + ",username:" + user.getUsername(), 1, "成功", memo,0);
                return CommonResult.success(null);
            }
            insertEventLog(caller.getId(), caller.getUsername(),ipAddress, request.getMethod(),userAgent, "POST:/admin/delete/" + id + ",username:" + user.getUsername(), 0, "失敗", memo,0);
        }
        return CommonResult.failed();
    }

    /**
     * 更新帳號的起停狀態
     *
     * @param id
     * @param status
     * @return
     */
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

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        UmsAdmin user = adminService.getById(id);
        String ipAddress = IpUtil.getIpAddr(request);

        String statusMessage = "";
        if (status > 0) {
            statusMessage = "帳號 Enabled";
        } else {
            statusMessage = "帳號 Disabled";
        }
        UmsAdmin umsAdmin = new UmsAdmin();
        umsAdmin.setUpdateTime(new Date());
        umsAdmin.setUpdateBy(caller.getId());
        umsAdmin.setUpdateName(caller.getUsername());
        umsAdmin.setStatus(status);
        boolean success = adminService.update(id, umsAdmin);
        if (success) {
            sw.stop();
            String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "POST:/admin/updateStatus/" + id + ",username:" + user.getUsername(), 1, "成功:", "success:" + memo,0);
            return CommonResult.success(null);
        } else {
            sw.stop();
            String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress,request.getMethod(),userAgent, "POST:/admin/updateStatus/" + id + ",username:" + user.getUsername(), 0, "失敗", "failed:" + memo,0);
        }
        return CommonResult.failed();
    }

    @RequestMapping(value = "/role/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateRole(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestParam("adminId") Long adminId,
            @RequestParam("roleIds") List<Long> roleIds,
            @RequestHeader(value = "User-Agent") String userAgent) {

        StopWatch sw = new StopWatch();
        sw.start("updateRole Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        int count = adminService.updateRole(adminId, roleIds);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";

        if (count >= 0) {

            //紀錄誰改了誰的帳號
            UmsAdmin umsAdmin = adminService.getById(adminId);
            umsAdmin.setUpdateBy(caller.getId());
            umsAdmin.setUpdateName(caller.getUsername());
            umsAdmin.setUpdateTime(new Date());
            boolean success = adminService.update(adminId, umsAdmin);

            insertEventLog(caller.getId(), caller.getUsername(),ipAddress, request.getMethod(),userAgent, "POST:/admin/role/update/" + adminId + ",username:" + caller.getUsername(), 1, "成功:", memo ,0);
            return CommonResult.success(count);

        }else{
            insertEventLog(caller.getId(), caller.getUsername(),ipAddress, request.getMethod(),userAgent, "POST:/admin/role/update/" + adminId + ",username:" + caller.getUsername(), 0, "失敗:", memo ,0);

            return CommonResult.failed();
        }

    }


    /**
     * 增加公司別判斷
     *
     * @param adminId
     * @return
     */
    @RequestMapping(value = "/role/{adminId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsRole>> getRoleList(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @PathVariable Long adminId,
            @RequestHeader(value = "User-Agent") String userAgent) {

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        List<UmsRole> roleList = adminService.getRoleList(adminId);
        insertEventLog(caller.getId(), caller.getUsername(),ipAddress, request.getMethod(),userAgent,
                "GET:/admin/role/" + adminId + ",username:" + caller.getUsername(),
                1, "成功:", "success" ,0);

        return CommonResult.success(roleList);
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
            String memo,Integer logType) {
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
            eventLog.setLogType(logType);
            return umsEventLogMapper.insert(eventLog);
        } catch (Exception ex) {
            log.info(String.valueOf(ex));
            return -1;
        }
    }
}
