package com.svm.backend.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.svm.backend.modules.ums.model.UmsResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 後台資源 Mapper 接口
 *
 * @author : kevin Chang
 */
public interface UmsResourceMapper extends BaseMapper<UmsResource> {

    /**
     * 獲取用户所有可訪問資源
     *
     * @param adminId
     * @return
     */
    List<UmsResource> getResourceList(@Param("adminId") Long adminId);

    /**
     * 根據角色ID獲取資源
     *
     * @param roleId
     * @return
     */
    List<UmsResource> getResourceListByRoleId(@Param("roleId") Long roleId);

}
