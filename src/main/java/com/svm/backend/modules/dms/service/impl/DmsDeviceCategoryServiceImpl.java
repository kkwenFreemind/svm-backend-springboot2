package com.svm.backend.modules.dms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.svm.backend.modules.dms.mapper.DmsDeviceCategoryMapper;
import com.svm.backend.modules.dms.model.DmsDeviceCategory;
import com.svm.backend.modules.dms.service.DmsDeviceCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 設備分類管理Service實現類
 *
 * @author Kevin Chang
 */
@Service
@Slf4j
public class DmsDeviceCategoryServiceImpl extends ServiceImpl<DmsDeviceCategoryMapper, DmsDeviceCategory> implements DmsDeviceCategoryService {

    @Override
    public List<DmsDeviceCategory> listAll() {
        QueryWrapper<DmsDeviceCategory> wrapper = new QueryWrapper<>();
        wrapper.lambda().orderByDesc(DmsDeviceCategory::getSort);
        return list(wrapper);
    }

    @Override
    public boolean create(DmsDeviceCategory dmsDeviceCategory) {
        dmsDeviceCategory.setCreateTime(new Date());
        return save(dmsDeviceCategory);
    }

    @Override
    public Page<DmsDeviceCategory> list(String keyword, Integer pageSize, Integer pageNum) {
        log.info("keyword: {}", keyword);
        Page<DmsDeviceCategory> page = new Page<>(pageNum, pageSize);
        QueryWrapper<DmsDeviceCategory> wrapper = new QueryWrapper<>();
        wrapper.lambda().orderByDesc(DmsDeviceCategory::getSort);
        LambdaQueryWrapper<DmsDeviceCategory> lambda = wrapper.lambda();
        if (StrUtil.isNotEmpty(keyword)) {
            lambda.like(DmsDeviceCategory::getName, keyword);
            lambda.or().like(DmsDeviceCategory::getNote, keyword);
        }
        return page(page, wrapper);
    }

    @Override
    public DmsDeviceCategory getCateInfoById(Long deviceType) {

        QueryWrapper<DmsDeviceCategory> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(DmsDeviceCategory::getId, deviceType);
        List<DmsDeviceCategory> deviceCateList = list(wrapper);
        if (deviceCateList != null && deviceCateList.size() > 0) {
            DmsDeviceCategory deviceCate = deviceCateList.get(0);
            log.info("Device Cate: {} , {}", deviceType, deviceCate.getName());
            return deviceCate;
        }
        return null;
    }
}
