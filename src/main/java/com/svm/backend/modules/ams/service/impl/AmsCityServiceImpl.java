package com.svm.backend.modules.ams.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.svm.backend.modules.ams.mapper.AmsCityMapper;
import com.svm.backend.modules.ams.model.AmsCity;
import com.svm.backend.modules.ams.service.AmsCityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kevinchang
 */
@Service
@Slf4j
public class AmsCityServiceImpl extends ServiceImpl<AmsCityMapper, AmsCity> implements AmsCityService {

    @Autowired
    private AmsCityMapper amsCityMapper;

    @Override
    public AmsCity getCityById(Long id) {
        return amsCityMapper.selectById(id);
    }

    @Override
    public List<AmsCity> listAll() {
        QueryWrapper<AmsCity> wrapper = new QueryWrapper<>();
        wrapper.lambda().orderByAsc(AmsCity::getId);
        return list(wrapper);
    }

    @Override
    public Page<AmsCity> list(String keyword, Integer pageSize, Integer pageNum) {
        Page<AmsCity> page = new Page<>(pageNum, pageSize);
        QueryWrapper<AmsCity> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<AmsCity> lambda = wrapper.lambda();
        if (StrUtil.isNotEmpty(keyword)) {
            lambda.like(AmsCity::getName, keyword);
        }
        return page(page, wrapper);
    }
}
