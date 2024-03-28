package com.svm.backend.modules.ams.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.common.api.CommonPage;
import com.svm.backend.common.api.CommonResult;
import com.svm.backend.modules.ams.model.AmsArea;
import com.svm.backend.modules.ams.service.AmsAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author kevinchang
 */
@Controller
@RequestMapping("/area")
public class AmsAreaController {

    @Autowired
    private AmsAreaService areaService;

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<AmsArea>> listAll() {
        List<AmsArea> areaList = areaService.listAll();
        return CommonResult.success(areaList);
    }

    @RequestMapping(value = "/listCityArea/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<AmsArea>> listCityArea(
            @PathVariable Long id,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<AmsArea> areaList = areaService.listAreaByCityId(id, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(areaList));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<AmsArea>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<AmsArea> areaList = areaService.list(keyword, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(areaList));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<AmsArea> getItem(@PathVariable Long id) {

        AmsArea area = areaService.getById(id);
        return CommonResult.success(area);
    }

}
