package com.svm.backend.modules.ums.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.svm.backend.common.exception.Asserts;
import com.svm.backend.domain.AdminUserDetails;
import com.svm.backend.modules.ums.dto.UmsAdminParam;
import com.svm.backend.modules.ums.dto.UpdateAdminPasswordParam;
import com.svm.backend.modules.ums.mapper.UmsAdminMapper;
import com.svm.backend.modules.ums.mapper.UmsResourceMapper;
import com.svm.backend.modules.ums.mapper.UmsRoleMapper;
import com.svm.backend.modules.ums.model.UmsAdmin;
import com.svm.backend.modules.ums.model.UmsAdminRoleRelation;
import com.svm.backend.modules.ums.model.UmsResource;
import com.svm.backend.modules.ums.model.UmsRole;
import com.svm.backend.modules.ums.service.UmsAdminRoleRelationService;
import com.svm.backend.modules.ums.service.UmsAdminService;
import com.svm.backend.security.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author kevinchang
 */

@Service
@Slf4j
public class UmsAdminServiceImpl extends ServiceImpl<UmsAdminMapper, UmsAdmin> implements UmsAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsAdminServiceImpl.class);
    private int maxPasswordLength = 13;
    private int minPasswordLength = 8;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UmsAdminRoleRelationService adminRoleRelationService;
    @Autowired
    private UmsRoleMapper roleMapper;
    @Autowired
    private UmsResourceMapper resourceMapper;

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    @Override
    public UmsAdmin getAdminByUsername(String username) {

        return umsAdminMapper.getAdminByUsername(username);

    }

    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = new UmsAdmin();
        try {
            BeanUtils.copyProperties(umsAdminParam, umsAdmin);
            umsAdmin.setCreateTime(new Date());

            //将密碼进行加密操作
            String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
            umsAdmin.setPassword(encodePassword);
            baseMapper.insert(umsAdmin);
        } catch (Exception ex) {
            log.info(ex.toString());
        }
        return umsAdmin;
    }

    @Override
    public String login(String username, String password) {

        String token = null;

        //密碼需要客户端加密後傳送
        try {
            //testing
            UserDetails userDetails = loadUserByUsername(username);

            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                Asserts.fail("密碼不正確");
            }
            if (!userDetails.isEnabled()) {
                Asserts.fail("帳號已被禁用");
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateTokenByUserDetails(userDetails);
            log.debug("jwtTokenUtil.generateToken:" + token);

        } catch (AuthenticationException e) {
            log.warn("登入異常:{}", e.getMessage());
        }
        return token;
    }


    /**
     * 根據用户名修改登入時间
     */
    private void updateLoginTimeByUsername(String username) {
        UmsAdmin record = new UmsAdmin();
        record.setLoginTime(new Date());
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUsername, username);
        update(record, wrapper);
    }

    @Override
    public String refreshToken(String oldToken) {
        return jwtTokenUtil.refreshHeadToken(oldToken);
    }

    @Override
    public Page<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum) {

        log.debug("Keyword:" + keyword);
        Page<UmsAdmin> page = new Page<>(pageNum, pageSize);
        if (StrUtil.isNotEmpty(keyword)) {
            Page<UmsAdmin> umsAdminList = umsAdminMapper.getAdminUserByUsername(page, keyword);
        } else {
            Page<UmsAdmin> umsAdminList = umsAdminMapper.getAllUser(page);
        }
        return page;
    }

    @Override
    public Page<UmsAdmin> listMyAccount(String keyword, Integer pageSize, Integer pageNum) {
        log.debug("Keyword:" + keyword);
        Page<UmsAdmin> page = new Page<>(pageNum, pageSize);
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(keyword)) {

            wrapper.lambda().eq(UmsAdmin::getUsername, keyword).orderByDesc(UmsAdmin::getId);

        } else {

            wrapper.lambda().orderByDesc(UmsAdmin::getId);

        }
        return page(page, wrapper);
    }

    @Override
    public boolean update(Long id, UmsAdmin admin) {

        admin.setId(id);
        UmsAdmin rawAdmin = getById(id);
        if (rawAdmin.getPassword().equals(admin.getPassword())) {
            //與原加密密碼相同的不需要修改
            admin.setPassword(null);
        } else {
            //與原加密密碼不同的需要加密修改
            if (StrUtil.isEmpty(admin.getPassword())) {
                admin.setPassword(null);
            } else {
                admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            }
        }
        boolean success = updateById(admin);
        return success;
    }

    @Override
    public boolean delete(Long id) {
        boolean success = removeById(id);
        return success;
    }

    @Override
    public int updateRole(Long adminId, List<Long> roleIds) {
        int count = roleIds == null ? 0 : roleIds.size();
        //先删除原来的關聯
        QueryWrapper<UmsAdminRoleRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdminRoleRelation::getAdminId, adminId);
        adminRoleRelationService.remove(wrapper);
        //建立新關聯
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<UmsAdminRoleRelation> list = new ArrayList<>();
            for (Long roleId : roleIds) {
                UmsAdminRoleRelation roleRelation = new UmsAdminRoleRelation();
                roleRelation.setAdminId(adminId);
                roleRelation.setRoleId(roleId);
                list.add(roleRelation);
            }
            adminRoleRelationService.saveBatch(list);
        }
        return count;
    }

    @Override
    public List<UmsRole> getRoleList(Long adminId) {
        return roleMapper.getRoleList(adminId);
    }


    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        List<UmsResource> resourceList = resourceMapper.getResourceList(adminId);
        return resourceList;
    }

    @Override
    public int updatePassword(UpdateAdminPasswordParam param,UmsAdmin caller) {
        if (StrUtil.isEmpty(param.getUsername())
                || StrUtil.isEmpty(param.getOldPassword())
                || StrUtil.isEmpty(param.getNewPassword())
        ) {
            return -1;
        }
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUsername, param.getUsername());

        List<UmsAdmin> adminList = list(wrapper);
        if (CollUtil.isEmpty(adminList)) {
            return -2;
        }
        UmsAdmin umsAdmin = adminList.get(0);
        if (!passwordEncoder.matches(param.getOldPassword(), umsAdmin.getPassword())) {
            return -3;
        }

        /**
         * 新增密碼強度檢查
         */
        if (param.getNewPassword().length() < minPasswordLength || param.getNewPassword().length() > maxPasswordLength) {
            log.info("new Password :{}", param.getNewPassword());
            return -4;
        }

        umsAdmin.setPassword(passwordEncoder.encode(param.getNewPassword()));
        umsAdmin.setUpdateTime(new Date());
        umsAdmin.setUpdateBy(caller.getId());
        umsAdmin.setUpdateName(caller.getUsername());
        updateById(umsAdmin);
        return 1;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        //從資料庫獲得用户信息
        UmsAdmin admin = getAdminByUsername(username);
        if (admin != null) {
            List<UmsResource> resourceList = getResourceList(admin.getId());
            return new AdminUserDetails(admin, resourceList);
        }
        throw new UsernameNotFoundException("用户名或密碼錯誤");
    }


}
