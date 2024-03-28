package com.svm.backend.modules.dms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.common.api.CommonPage;
import com.svm.backend.common.api.CommonResult;
import com.svm.backend.modules.dms.dto.DeviceMap;
import com.svm.backend.modules.dms.dto.DmsDeviceParam;
import com.svm.backend.modules.dms.model.DmsDevice;
import com.svm.backend.modules.dms.model.DmsDeviceCategory;
import com.svm.backend.modules.dms.service.DmsDeviceCategoryService;
import com.svm.backend.modules.dms.service.DmsDeviceService;
import com.svm.backend.modules.oms.model.OmsOrganization;
import com.svm.backend.modules.oms.service.OmsOrganizationService;
import com.svm.backend.modules.ums.mapper.UmsEventLogMapper;
import com.svm.backend.modules.ums.model.UmsAdmin;
import com.svm.backend.modules.ums.model.UmsEventLog;
import com.svm.backend.modules.ums.service.UmsAdminService;
import com.svm.backend.security.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * 設備管理
 *
 * @author kevinchang
 */
@Controller
@RequestMapping("/device")
@Slf4j
@Tag(name = "DmsDevice Controller")
public class DmsDeviceController {

    @Autowired
    private DmsDeviceService deviceService;

    @Autowired
    private DmsDeviceCategoryService deviceCateService;

    @Autowired
    private UmsAdminService adminService;

    @Autowired
    private OmsOrganizationService omsOrganizationService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UmsEventLogMapper umsEventLogMapper;


    /**
     * 列舉設備
     *
     * @param bearer
     * @param userAgent
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<DmsDevice>> list(
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);

        Page<DmsDevice> deviceList = deviceService.getDeviceList(null, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(deviceList));

    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<DmsDevice> register(
            @RequestHeader(value = "Authorization") String bearer,
            @Validated @RequestBody DmsDeviceParam dmsDeviceParam) {


        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);

        DmsDevice dmsDevice = deviceService.create(dmsDeviceParam);

        if (dmsDevice == null) {
            return CommonResult.failed();
        }
        return CommonResult.success(dmsDevice);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<DmsDevice> getItem(@PathVariable Long id) {
        log.info("deviceService.getById: {}", id);
        DmsDevice device = deviceService.getById(id);
        return CommonResult.success(device);
    }


    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody DmsDevice device) {

        //關聯到DeviceCate
        log.info("device info:{}", device.getDeviceType());
        DmsDeviceCategory dmsDeviceCategory = new DmsDeviceCategory();
        dmsDeviceCategory = deviceCateService.getCateInfoById(device.getDeviceType());

        log.info("dmsDeviceCategory:{}", dmsDeviceCategory.getName());

        //關聯到Organization
        log.info("device info:{}", device.getOrgId());
        OmsOrganization omsOrganization = new OmsOrganization();
        omsOrganization = omsOrganizationService.getById(device.getOrgId());

        log.info("omsOrganization info: {},{}", omsOrganization.getNameSn(), omsOrganization.getName());
        device.setOrgSn(omsOrganization.getNameSn());
        device.setOrgName(omsOrganization.getName());

        //更新設備資訊

        boolean success = deviceService.update(id, device);

        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id,
            @RequestParam(value = "status") Integer status) {

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        DmsDevice device = deviceService.getById(id);

        String statusMessage = "";
        if (status > 0) {
            statusMessage = "設備 Enabled";
        } else {
            statusMessage = "設備 Disabled";
        }


        device.setStatus(status);
        boolean success = deviceService.update(id, device);
        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), userAgent, "變更設備狀態", 1, "成功:", device.getDeviceSn() + statusMessage);
            return CommonResult.success(null);
        } else {
            insertEventLog(caller.getId(), caller.getUsername(), userAgent, "變更設備狀態", 0, "失敗", device.getDeviceSn() + statusMessage);
        }
        return CommonResult.failed();
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @PathVariable Long id) {

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);

        DmsDevice device = deviceService.getById(id);

        boolean success = deviceService.delete(id);

        if (success) {
            insertEventLog(caller.getId(), caller.getUsername(), userAgent, "刪除設備", 1, "成功", device.getDeviceSn());
            return CommonResult.success(null);
        }
        insertEventLog(caller.getId(), caller.getUsername(), userAgent, "刪除設備", 0, "失敗", device.getDeviceSn());
        return CommonResult.failed();
    }

    private int insertEventLog(Long userId, String username, String userAgent, String event, Integer status, String result, String memo) {
        //EventLog的初始化
        try {
            UmsEventLog eventLog = new UmsEventLog();
            eventLog.setUserId(userId);
            eventLog.setUsername(username);
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

    @RequestMapping(value = "/listmap", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<DeviceMap>> listmap(
            @RequestHeader(value = "Authorization") String bearer,
            @RequestHeader(value = "User-Agent") String userAgent,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {

        UmsAdmin caller = jwtTokenUtil.getCallerInfoFromToken(bearer);
        List<DmsDevice> deviceList = deviceService.list();

        List<DeviceMap> mapList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>(deviceList.size());

        for (DmsDevice data : deviceList) {
            Double[] local = new Double[2];
            local[0] = data.getLat();
            local[1] = data.getLng();

            DeviceMap deviceMap = new DeviceMap();
            deviceMap.setId(data.getId());
            deviceMap.setName(data.getAlisName());
            deviceMap.setLocal(local);
            mapList.add(deviceMap);
        }
        return CommonResult.success(mapList);
    }
}
