package com.svm.backend.modules.dms.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.common.api.CommonPage;
import com.svm.backend.common.api.CommonResult;
import com.svm.backend.modules.dms.model.DmsDeviceCategory;
import com.svm.backend.modules.dms.service.DmsDeviceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 設備分類管理Controller
 *
 * @author Kevin Chang
 */
@Controller
@RequestMapping("/deviceCategory")
public class DmsDeviceCategoryController {

    @Autowired
    private DmsDeviceCategoryService dmsDeviceCategoryService;


    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<DmsDeviceCategory>> listAll() {
        List<DmsDeviceCategory> deviceCategoryList = dmsDeviceCategoryService.listAll();
        return CommonResult.success(deviceCategoryList);
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody DmsDeviceCategory dmsDeviceCategory) {
        boolean success = dmsDeviceCategoryService.create(dmsDeviceCategory);
        if (success) {
            return CommonResult.success(null);
        } else {
            return CommonResult.failed();
        }
    }


    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id,
                               @RequestBody DmsDeviceCategory dmsDeviceCategory) {
        dmsDeviceCategory.setId(id);
        boolean success = dmsDeviceCategoryService.updateById(dmsDeviceCategory);
        if (success) {
            return CommonResult.success(null);
        } else {
            return CommonResult.failed();
        }
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = dmsDeviceCategoryService.removeById(id);
        if (success) {
            return CommonResult.success(null);
        } else {
            return CommonResult.failed();
        }
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<DmsDeviceCategory>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<DmsDeviceCategory> deviceCateList = dmsDeviceCategoryService.list(keyword, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(deviceCateList));
    }
}
