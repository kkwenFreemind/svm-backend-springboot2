package com.svm.backend.modules.ums.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.svm.backend.modules.ums.dto.UmsAdminParam;
import com.svm.backend.modules.ums.dto.UpdateAdminPasswordParam;
import com.svm.backend.modules.ums.model.UmsAdmin;
import com.svm.backend.modules.ums.model.UmsResource;
import com.svm.backend.modules.ums.model.UmsRole;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author kevinchang
 */
public interface UmsAdminService extends IService<UmsAdmin> {

//    UmsAdmin getAdminByUsernameAndInvoiceNumber(String username,Long invoiceNumber);

    /**
     * 根據用戶名稱獲得後台管理員
     *
     * @param username
     * @return
     */
    UmsAdmin getAdminByUsername(String username);

    /**
     * 新增帳號
     *
     * @param umsAdminParam
     * @return
     */
    UmsAdmin register(UmsAdminParam umsAdminParam);

    /**
     * 登入功能
     *
     * @param username 用戶名稱
     * @param password 密碼
     * @return 生成的JWT的token
     */
    String login(String username, String password);


    /**
     * 刷新token的功能
     *
     * @param oldToken
     * @return
     */
    String refreshToken(String oldToken);

    Page<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum);

    Page<UmsAdmin> listMyAccount(String keyword, Integer pageSize, Integer pageNum);

    /**
     * 修改指定用户信息
     *
     * @param id
     * @param admin
     * @return
     */
    boolean update(Long id, UmsAdmin admin);

    /**
     * 删除指定用户
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 修改用户角色關聯
     *
     * @param adminId
     * @param roleIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    int updateRole(Long adminId, List<Long> roleIds);


    /**
     * 獲得用户對應角色
     *
     * @param adminId
     * @return
     */
    List<UmsRole> getRoleList(Long adminId);


    /**
     * 獲得指定用户的可訪問資源
     *
     * @param adminId
     * @return
     */
    List<UmsResource> getResourceList(Long adminId);

    /**
     * 修改密碼
     *
     * @param updatePasswordParam
     * @return
     */
    int updatePassword(UpdateAdminPasswordParam updatePasswordParam,UmsAdmin caller);

    /**
     * 獲得用户信息
     *
     * @param username
     * @return
     */
    UserDetails loadUserByUsername(String username);


}
