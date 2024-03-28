package com.svm.backend.modules.ams.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.svm.backend.modules.ams.mapper.AmsAreaMapper;
import com.svm.backend.modules.ams.model.AmsArea;
import com.svm.backend.modules.ams.service.AmsAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kevinchang
 */
@Service
@Slf4j
public class AmsAreaServiceImpl extends ServiceImpl<AmsAreaMapper, AmsArea> implements AmsAreaService {

    @Autowired
    private AmsAreaMapper amsAreaMapper;

    @Override
    public AmsArea getAreaById(Long id) {
        return amsAreaMapper.selectById(id);
    }

    @Override
    public List<AmsArea> listAll() {
        QueryWrapper<AmsArea> wrapper = new QueryWrapper<>();
        wrapper.lambda().orderByAsc(AmsArea::getId);
        return list(wrapper);
    }

    @Override
    public List<AmsArea> listAreaByCityId(Long cityId) {
        QueryWrapper<AmsArea> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AmsArea::getCityId, cityId).orderByAsc(AmsArea::getId);
        return list(wrapper);
    }

    @Override
    public Page<AmsArea> list(String keyword, Integer pageSize, Integer pageNum) {
        Page<AmsArea> page = new Page<>(pageNum, pageSize);
        QueryWrapper<AmsArea> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<AmsArea> lambda = wrapper.lambda();
        if (StrUtil.isNotEmpty(keyword)) {
            lambda.like(AmsArea::getName, keyword);
        }
        return page(page, wrapper);
    }

    @Override
    public Page<AmsArea> listAreaByCityId(Long cityId, Integer pageSize, Integer pageNum) {

        Page<AmsArea> page = new Page<>(pageNum, pageSize);
        QueryWrapper<AmsArea> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<AmsArea> lambda = wrapper.lambda();
        wrapper.lambda().eq(AmsArea::getCityId, cityId).orderByAsc(AmsArea::getId);
        return page(page, wrapper);
    }
}
