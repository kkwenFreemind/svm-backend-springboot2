package com.svm.backend.modules.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.ums.model.UmsAdminRoleRelation;

import java.util.List;

/**
 * 管理員角色關聯管理Service
 *
 * @author : kevin Chang
 */
public interface UmsAdminRoleRelationService extends IService<UmsAdminRoleRelation> {

    List<UmsAdminRoleRelation> getUserList(Long roleId);
}
