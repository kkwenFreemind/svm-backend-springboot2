package com.svm.backend.modules.ums.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.svm.backend.modules.ums.mapper.UmsRoleResourceRelationMapper;
import com.svm.backend.modules.ums.model.UmsRoleResourceRelation;
import com.svm.backend.modules.ums.service.UmsRoleResourceRelationService;
import org.springframework.stereotype.Service;

/**
 * 角色資源關聯管理Service實現類
 *
 * @author : kevin Chang
 */
@Service
public class UmsRoleResourceRelationServiceImpl extends ServiceImpl<UmsRoleResourceRelationMapper, UmsRoleResourceRelation> implements UmsRoleResourceRelationService {
}
