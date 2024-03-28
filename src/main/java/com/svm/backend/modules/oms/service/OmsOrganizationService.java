package com.svm.backend.modules.oms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.oms.dto.OmsOrganizationNode;
import com.svm.backend.modules.oms.model.OmsOrganization;

import java.util.List;

/**
 * @author kevinchang
 */
public interface OmsOrganizationService extends IService<OmsOrganization> {

    /**
     * 添加組織
     *
     * @param organization
     * @return
     */
    boolean create(OmsOrganization organization);

    /**
     * 批量删除組織
     *
     * @param ids
     * @return
     */
    boolean delete(List<Long> ids);

    /**
     * 刪除組織
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 分頁獲得組織列表
     *
     * @param parentId
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<OmsOrganization> list(Long parentId, Integer pageSize, Integer pageNum);

    /**
     * 分頁獲得組織上層列表
     *
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<OmsOrganization> listLevel( Integer pageSize, Integer pageNum);

    /**
     * 修改組織狀態
     *
     * @param id
     * @param status
     * @return
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 修改指定組織信息
     *
     * @param id
     * @param organization
     * @return
     */
    boolean update(Long id, OmsOrganization organization);

    /**
     * 依照組織名稱，取得指定的組織訊息
     *
     * @param name
     * @return
     */
    OmsOrganization getOrganizationByName(String name);

    /**
     * 取得該層級下的所有組織資訊
     *
     * @param parentId
     * @return
     */
    List<OmsOrganization> organizationLowList(Long parentId);

    /**
     * 分頁獲得組織列表
     *
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<OmsOrganization> list(Integer pageSize, Integer pageNum);

    /**
     * 列舉該公司下的組織
     *
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<OmsOrganization> listCombox( Integer pageSize, Integer pageNum);

    /**
     * 取得最大的組織Id
     *
     * @return
     */
    Long getMaxId();

    /**
     *取得最大的組織Id
     * @return
     */
    List<OmsOrganizationNode> treeList();
}
