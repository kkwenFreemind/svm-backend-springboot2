package com.svm.backend.modules.ums.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.ums.model.UmsResourceCategory;

import java.util.List;

/**
 * 後台資源分類管理Service
 *
 * @author : kevin Chang
 */
public interface UmsResourceCategoryService extends IService<UmsResourceCategory> {


    /**
     * 獲得所有資源分類
     *
     * @return
     */
    List<UmsResourceCategory> listAll();

    /**
     * 創建資源分類
     *
     * @param umsResourceCategory
     * @return
     */
    boolean create(UmsResourceCategory umsResourceCategory);
}
