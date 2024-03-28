package com.svm.backend.modules.ums.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.ums.model.UmsMenu;
import com.svm.backend.modules.ums.model.UmsResource;
import com.svm.backend.modules.ums.model.UmsRole;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 後台角色管理Service
 *
 * @author : kevin Chang
 */
public interface UmsRoleService extends IService<UmsRole> {

    /**
     * 添加角色
     *
     * @param role
     * @return
     */
    boolean create(UmsRole role);

    /**
     * 批量删除角色
     *
     * @param ids
     * @return
     */
    boolean delete(List<Long> ids);

//    /**
//     * 分頁獲得角色列表
//     *
//     * @param keyword
//     * @param pageSize
//     * @param pageNum
//     * @return
//     */
//    Page<UmsRole> list(String keyword, Integer pageSize, Integer pageNum);

//    /**
//     * @param keyword
//     * @param pageSize
//     * @param pageNum
//     * @return
//     */
//    Page<UmsRole> listRole( String keyword, Integer pageSize, Integer pageNum);

    Page<UmsRole> listAllRole( String keyword, Integer pageSize, Integer pageNum);

    /**
     * @return
     */
    List<UmsRole> listAllRole();

    /**
     * 根據管理員ID獲得对應菜單
     *
     * @param adminId
     * @return
     */
    List<UmsMenu> getMenuList(Long adminId);

    /**
     * 獲得角色相關菜單
     *
     * @param roleId
     * @return
     */
    List<UmsMenu> listMenu(Long roleId);

    /**
     * 獲得角色相關資源
     *
     * @param roleId
     * @return
     */
    List<UmsResource> listResource(Long roleId);


    /**
     * 給角色分配菜單
     *
     * @param roleId
     * @param menuIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    int allocMenu(Long roleId, List<Long> menuIds);


    /**
     * 給角色分配資源
     *
     * @param roleId
     * @param resourceIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    int allocResource(Long roleId, List<Long> resourceIds);

    @Transactional(rollbackFor = Exception.class)
    int deleteRoleMenu(Long roleId, List<Long> menuIds);

    @Transactional(rollbackFor = Exception.class)
    int deleteRoleResource(Long roleId, List<Long> resourceIds);
}
