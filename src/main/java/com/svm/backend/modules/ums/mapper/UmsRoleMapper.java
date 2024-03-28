package com.svm.backend.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.modules.ums.model.UmsRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 後台用户角色 Mapper 接口
 *
 * @author : kevin Chang
 */
public interface UmsRoleMapper extends BaseMapper<UmsRole> {

    List<UmsRole> getRoleList(@Param("adminId") Long adminId);

    Page<UmsRole> getAllRoleList(Page<UmsRole> page);

    Page<UmsRole> getRoleListByRoleName(Page<UmsRole> page,String keyword);
}
