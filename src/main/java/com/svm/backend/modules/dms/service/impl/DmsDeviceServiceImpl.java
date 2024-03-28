package com.svm.backend.modules.dms.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.svm.backend.modules.dms.dto.DmsDeviceParam;
import com.svm.backend.modules.dms.mapper.DmsDeviceMapper;
import com.svm.backend.modules.dms.model.DmsDevice;
import com.svm.backend.modules.dms.service.DmsDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author kevinchang
 */
@Service
@Slf4j
public class DmsDeviceServiceImpl extends ServiceImpl<DmsDeviceMapper, DmsDevice> implements DmsDeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DmsDeviceServiceImpl.class);

    @Autowired
    private DmsDeviceMapper dmsDeviceMapper;


    @Override
    public Page<DmsDevice> getDeviceList( String keyword, Integer pageSize, Integer pageNum) {

        log.debug("Keyword:" + keyword);
        Page<DmsDevice> page = new Page<>(pageNum, pageSize);
        if (StrUtil.isNotEmpty(keyword)) {
        } else {
            Page<DmsDevice> umsAdminList = dmsDeviceMapper.getDeviceList(page);
        }
        return page;

    }

    @Override
    public DmsDevice create(DmsDeviceParam dmsDeviceParam) {
        DmsDevice dmsDevice = new DmsDevice();
        BeanUtils.copyProperties(dmsDeviceParam, dmsDevice);
        dmsDevice.setCreateTime(new Date());
        dmsDevice.setStatus(1);

        //查詢是否有相同編號的設備
        QueryWrapper<DmsDevice> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(DmsDevice::getDeviceSn, dmsDevice.getDeviceSn());
        List<DmsDevice> deviceList = list(wrapper);
        if (deviceList.size() > 0) {
            return null;
        }
        baseMapper.insert(dmsDevice);
        return dmsDevice;
    }

    @Override
    public boolean update(Long id, DmsDevice device) {
        device.setId(id);
        DmsDevice rawDevice = getById(id);
        if (id.equals(device.getId())) {
            boolean success = updateById(device);
            return success;
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        boolean success = removeById(id);
        return success;
    }

}
