package com.svm.backend.modules.oms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.common.api.CommonPage;
import com.svm.backend.common.api.CommonResult;
import com.svm.backend.modules.oms.dto.OmsOrganizationNode;
import com.svm.backend.modules.oms.model.OmsOrganization;
import com.svm.backend.modules.oms.service.OmsOrganizationService;
import com.svm.backend.modules.ums.mapper.UmsEventLogMapper;
import com.svm.backend.modules.ums.model.UmsAdmin;
import com.svm.backend.modules.ums.model.UmsEventLog;
import com.svm.backend.modules.utils.IpUtil;
import com.svm.backend.security.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 單位組織架構管理
 *
 * @author kevinchang
 */
@Controller
@RequestMapping("/org")
@Slf4j
@Tag(name = "OmsOrganization Controller")
public class OmsOrganizationController {

    @Autowired
    private OmsOrganizationService organizationService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UmsEventLogMapper umsEventLogMapper;

    /**
     * 根據ID取得單筆組織資料
     *
     * @param bearer
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsOrganization> getItem(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id) {

        //step1: get request information
        StopWatch sw = new StopWatch();
        sw.start("list organization Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String callerName = caller.getUsername();
        String ipAddress = IpUtil.getIpAddr(request);

        //step2 :do api request
        OmsOrganization organization = organizationService.getById(id);


        //step3: stop request and log it
        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms ";

        JSONObject jsonObject = new JSONObject(organization);
        String json = jsonObject.toString();

        log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "列舉單一組織：/org/" + id + "," + 1 + "," + "成功" + "," + memo + "," + json);

        //step4: return result
        return CommonResult.success(organization);
    }

    /**
     * 列出該層級ID之下的所有組織
     *
     * @param parentId
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/list/{parentId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsOrganization>> listOrganizationPage(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long parentId,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        //step1: get request information
        StopWatch sw = new StopWatch();
        sw.start("list organization Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String callerName = caller.getUsername();
        String ipAddress = IpUtil.getIpAddr(request);

        //step2 :do api request
        Page<OmsOrganization> orgList = organizationService.list( parentId, pageSize, pageNum);

        //step3: stop request and log it
        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms ";
        log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "列舉下層組織：/org/list/{parentId}" + parentId + "," + 1 + "," + "成功" + "," + memo + "," +orgList.toString() );

        //step4: return result
        return CommonResult.success(CommonPage.restPage(orgList));
    }

    @RequestMapping(value = "/listLevel", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsOrganization>> listLevel(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        StopWatch sw = new StopWatch();
        sw.start("listLevel organization Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String callerName = caller.getUsername();
        String ipAddress = IpUtil.getIpAddr(request);

        Page<OmsOrganization> orgList = organizationService.listLevel( pageSize, pageNum);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms ";
        log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "列舉組織：/org/listLevel" + "," + 1 + "," + "成功" + "," + memo + "," +orgList.toString());

        return CommonResult.success(CommonPage.restPage(orgList));
    }

    /**
     * 建立組織
     *
     * @param organization
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult createOrganization(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestBody OmsOrganization organization) {

        StopWatch sw = new StopWatch();
        sw.start("create organization Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String callerName = caller.getUsername();
        String ipAddress = IpUtil.getIpAddr(request);

        //準備name_sn

        String orgName = organization.getName();
        Integer orgLevel = organization.getLevel();
        Long orgParnet = organization.getParentId();

        log.info("orgName---->" + orgName + "," + orgLevel + "," + orgParnet);

        //用上層單位，計算出name_sn
        OmsOrganization upperOrg = organizationService.getById(orgParnet);
        String upperSn = upperOrg.getNameSn();
        log.info("upperSn---->" + upperSn);

        Long currId = Long.valueOf(1);
        //找出最大id
        try {
            currId = organizationService.getMaxId();
            log.info("currId---->" + currId);
        } catch (Exception ex) {
            log.info(String.valueOf(ex));
        }

        String nextId = String.format("%03d", currId + 1);
        String orgSn = upperSn + nextId;

        log.info("orgSn---->" + orgSn);

        //org_sn 已經準備好了，可以新增
        organization.setNameSn(orgSn);
        organization.setCreateTime(new Date());
        log.info("account: " + callerName + " create organization data: " + organization.toString());
        boolean success = organizationService.create(organization);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms ";

        JSONObject jsonObject = new JSONObject(organization);
        String json = jsonObject.toString();

        if (success) {
            log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "新增組織單位：/org/create" + "," + 1 + "," + "成功" + "," + memo + "," + json + "," + 0);
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "新增組織單位：/org/create", 1, "成功", memo + json, 0);
            return CommonResult.success(null);
        }
        log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "新增組織單位：/org/create" + "," + 0 + "," + "失敗" + "," + memo + "," + json + "," + 0);
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "新增組織單位：/org/create", 0, "失敗", memo + json, 0);

        return CommonResult.failed();
    }


    /**
     * 更新組織
     *
     * @param id
     * @param organization
     * @return
     */
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateOrganization(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id,
            @RequestBody OmsOrganization organization) {

        StopWatch sw = new StopWatch();
        sw.start("update organization Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String callerName = caller.getUsername();
        String ipAddress = IpUtil.getIpAddr(request);

        organization.setId(id);
        boolean success = organizationService.updateById(organization);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms ";

        JSONObject jsonObject = new JSONObject(organization);
        String json = jsonObject.toString();


        if (success) {
            log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "更新組織單位：/org/update" + "," + 1 + "," + "成功" + "," + memo + "," + json + "," + 0);
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "更新組織單位：/org/update", 1, "成功", memo + json, 0);
            return CommonResult.success(null);
        }
        log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "更新組織單位：/org/update" + "," + 0 + "," + "失敗" + "," + memo + "," + json + "," + 0);
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "更新組織單位：/org/update", 0, "失敗", memo + json, 0);

        return CommonResult.failed();
    }

    /**
     * 刪除組織
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteOrganization(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id) {

        StopWatch sw = new StopWatch();
        sw.start("delete organization Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String callerName = caller.getUsername();
        String ipAddress = IpUtil.getIpAddr(request);

        OmsOrganization organization = new OmsOrganization();
        organization = organizationService.getById(id);

        JSONObject jsonObject = new JSONObject(organization);
        String json = jsonObject.toString();

        //Step : 用org_id去查欄位parent_id，有無下層組織，如果有則無法刪除
        Page<OmsOrganization> orgList = organizationService.list( id, 100, 1);
        log.info("====>"+orgList.getTotal()+","+orgList.getSize()+","+orgList.toString());
        boolean success = false;
        if(orgList.getTotal() > 0){
            log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "刪除組織單位：/org/delete" + "," + 0 + "," + "失敗" + "," + "有下層組織，無法刪除" +json+",0");
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "刪除組織單位：/org/delete", 0, "失敗", "有下層組織，無法刪除"+json,0);
            return CommonResult.failed("有下層組織，無法刪除");
        }else {
            success = organizationService.delete(id);
        }


        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms ";

        if (success) {
            log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "刪除組織單位：/org/delete" + "," + 1 + "," + "成功" + "," + memo + "," + json + "," + 0);
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "刪除組織單位：/org/delete", 1, "成功", memo + json, 0);
            return CommonResult.success(null);
        }

        log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "刪除組織單位：/org/delete" + "," + 0 + "," + "失敗" + "," + memo + "," + json + "," + 0);
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "刪除組織單位：/org/delete", 0, "失敗", memo + json, 0);

        return CommonResult.failed();
    }

    /**
     * 列舉該公司下的所有組織
     *
     * @return
     */
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsOrganization>> listAllOrganization(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent) {

        StopWatch sw = new StopWatch();
        sw.start("listAll organization Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String callerName = caller.getUsername();
        String ipAddress = IpUtil.getIpAddr(request);

        List<OmsOrganization> organizationList = organizationService.list();

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms ";

        log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "列舉全部組織：/org/listAll" + "," + 1 + "," + "成功" + "," + memo + "," + organizationList.toString());

        return CommonResult.success(organizationList);
    }

    /**
     * 更新組織狀態
     *
     * @param id
     * @param status
     * @return
     */
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateOrganizationStatus(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id,
            @RequestParam(value = "status") Integer status) {

        StopWatch sw = new StopWatch();
        sw.start("listAll organization Start");

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String callerName = caller.getUsername();
        String ipAddress = IpUtil.getIpAddr(request);

        OmsOrganization organization = new OmsOrganization();
        organization.setStatus(status);
        boolean success = organizationService.update(id, organization);

        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms ";

        JSONObject jsonObject = new JSONObject(organization);
        String json = jsonObject.toString();


        if (success) {
            log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "變更組織狀態：/org/updateStatus/" + status + "," + 1 + "," + "成功" + "," + memo + "," + json);
            insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "變更組織狀態：/org/updateStatus/"+status, 1, "成功", memo + json, 0);
            return CommonResult.success(null);
        }
        log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "變更組織狀態：/org/updateStatus/" + status + "," + 0 + "," + "失敗" + "," + memo + "," + json);
        insertEventLog(caller.getId(), caller.getUsername(), ipAddress, request.getMethod(), userAgent, "變更組織狀態：/org/updateStatus/"+status, 0, "失敗", memo + json, 0);

        return CommonResult.failed();
    }

    /**
     * Admin 畫面下，組織的下拉清單ComboBox
     *
     * @param bearer
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/listD", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsOrganization>> listOrganizationPage(
            @RequestHeader(value = "Authorization") String bearer,
            @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        //取得呼叫者的資訊，由組織iD取得企業iD
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);

        Page<OmsOrganization> orgList = organizationService.listCombox( pageSize, pageNum);
        log.info("listOrganizationPage count: {}", CommonPage.restPage(orgList).getTotal());
        return CommonResult.success(CommonPage.restPage(orgList));
    }

    @RequestMapping(value = "/treeList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsOrganizationNode>> treeList(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent) {

        StopWatch sw = new StopWatch();
        sw.start("treeList Start");

        //取得呼叫者的資訊
        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        String ipAddress = IpUtil.getIpAddr(request);

        List<OmsOrganizationNode> list = organizationService.treeList();
        sw.stop();
        String memo = "執行耗時：" + sw.getTotalTimeMillis() + "ms";
        log.info(caller.getId() + "," + caller.getUsername() + "," + ipAddress + "," + request.getMethod() + "," + userAgent + "," + "列舉樹狀組織：/org/treeList" + "," + 1 + "," + "成功" + "," + memo +","+list.toString());

        return CommonResult.success(list);
    }

    private int insertEventLog(
            Long userId, String username, String ipAddress, String method,
            String userAgent, String event, Integer status, String result,
            String memo, Integer logType) {
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
