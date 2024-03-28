package com.svm.backend.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.svm.backend.modules.ums.model.UmsMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 後台菜單 Mapper 接口
 *
 * @author : kevin Chang
 */
public interface UmsMenuMapper extends BaseMapper<UmsMenu> {

    /**
     * 根據用户ID獲取菜單
     *
     * @param adminId
     * @return
     */
    List<UmsMenu> getMenuList(@Param("adminId") Long adminId);

    /**
     * 根據角色ID獲取菜單
     *
     * @param roleId
     * @return
     */
    List<UmsMenu> getMenuListByRoleId(@Param("roleId") Long roleId);

}
