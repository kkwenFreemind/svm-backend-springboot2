package com.svm.backend.modules.ums.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.ums.model.UmsResource;

/**
 * 後台資源管理Service
 *
 * @author : kevin Chang
 */
public interface UmsResourceService extends IService<UmsResource> {

    /**
     * 添加資源
     *
     * @param umsResource
     * @return
     */
    boolean create(UmsResource umsResource);

    /**
     * 修改資源
     *
     * @param id
     * @param umsResource
     * @return
     */
    boolean update(Long id, UmsResource umsResource);

    /**
     * 删除資源
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 分頁查詢資源
     *
     * @param categoryId
     * @param nameKeyword
     * @param urlKeyword
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<UmsResource> list(Long categoryId, String nameKeyword, String urlKeyword, Integer pageSize, Integer pageNum);
}
