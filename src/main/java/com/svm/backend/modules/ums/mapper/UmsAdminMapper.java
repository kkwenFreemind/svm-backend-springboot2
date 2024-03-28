package com.svm.backend.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.svm.backend.modules.ums.model.UmsAdmin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 後台用户 Mapper 接口
 *
 * @author : kevin Chang
 */
public interface UmsAdminMapper extends BaseMapper<UmsAdmin> {

    /**
     * Fetch Account List By resourceId
     * @param resourceId
     * @return
     */
    List<Long> getAdminIdList(@Param("resourceId") Long resourceId);

    Page<UmsAdmin> getAllUser(Page<UmsAdmin> page);
    Page<UmsAdmin> getAdminUserByUsername(Page<UmsAdmin> page,String keyword);

    /**
     * Get Account By Username
     * @param username
     * @return
     */
    UmsAdmin getAdminByUsername(@Param("username") String username);

}
