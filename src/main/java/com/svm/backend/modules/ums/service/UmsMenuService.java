package com.svm.backend.modules.ums.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.ums.dto.UmsMenuNode;
import com.svm.backend.modules.ums.model.UmsMenu;

import java.util.List;


/**
 * 後台菜單管理Service
 *
 * @author : kevin Chang
 */
public interface UmsMenuService extends IService<UmsMenu> {

    /**
     * 創建後台菜單
     *
     * @param umsMenu
     * @return
     */
    boolean create(UmsMenu umsMenu);


    /**
     * 修改後台菜單
     *
     * @param id
     * @param umsMenu
     * @return
     */
    boolean update(Long id, UmsMenu umsMenu);


    /**
     * 分頁查詢後台菜單
     *
     * @param parentId
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<UmsMenu> list(Long parentId, Integer pageSize, Integer pageNum);

    /**
     * 樹狀結構返回所有菜單列表
     *
     * @return
     */
    List<UmsMenuNode> treeList();

    /**
     * 修改菜單顯示狀態
     *
     * @param id
     * @param hidden
     * @return
     */
    boolean updateHidden(Long id, Integer hidden);
}
