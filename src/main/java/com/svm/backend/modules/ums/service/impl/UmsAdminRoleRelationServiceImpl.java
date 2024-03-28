package com.svm.backend.modules.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.svm.backend.modules.ums.mapper.UmsAdminRoleRelationMapper;
import com.svm.backend.modules.ums.model.UmsAdminRoleRelation;
import com.svm.backend.modules.ums.service.UmsAdminRoleRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理員角色管理Service實現類
 *
 * @author : kevin Chang
 */
@Service
public class UmsAdminRoleRelationServiceImpl extends ServiceImpl<UmsAdminRoleRelationMapper, UmsAdminRoleRelation> implements UmsAdminRoleRelationService {
    @Override
    public List<UmsAdminRoleRelation> getUserList(Long roleId) {
        QueryWrapper<UmsAdminRoleRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdminRoleRelation::getRoleId,roleId);
        return list(wrapper);
    }
}
