package com.svm.backend.modules.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.svm.backend.modules.oms.dto.OmsOrganizationNode;
import com.svm.backend.modules.oms.mapper.OmsOrganizationMapper;
import com.svm.backend.modules.oms.model.OmsOrganization;
import com.svm.backend.modules.oms.service.OmsOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kevinchang
 */
@Service
@Slf4j
public class OmsOrganizationServiceImpl extends ServiceImpl<OmsOrganizationMapper, OmsOrganization> implements OmsOrganizationService {


    private OmsOrganizationMapper omsOrganizationMapper;

    @Override
    public boolean create(OmsOrganization organization) {
        organization.setCreateTime(new Date());
        updateLevel(organization);
        return save(organization);
    }

    @Override
    public boolean delete(List<Long> ids) {
        boolean success = removeByIds(ids);
        return success;
    }

    @Override
    public boolean delete(Long id) {

        boolean success = removeById(id);
        return success;
    }

    @Override
    public Page<OmsOrganization> list(Long parentId, Integer pageSize, Integer pageNum) {
        Page<OmsOrganization> page = new Page<>(pageNum, pageSize);
        QueryWrapper<OmsOrganization> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<OmsOrganization> lambda = wrapper.lambda();

        lambda.eq(OmsOrganization::getParentId, parentId)
                .orderByDesc(OmsOrganization::getSort);

        return page(page, wrapper);


    }

    @Override
    public Page<OmsOrganization> listLevel( Integer pageSize, Integer pageNum) {
        Page<OmsOrganization> page = new Page<>(pageNum, pageSize);
        QueryWrapper<OmsOrganization> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<OmsOrganization> lambda = wrapper.lambda();

        lambda.in(OmsOrganization::getLevel, 0, 1)
                .orderByDesc(OmsOrganization::getSort);

        return page(page, wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        OmsOrganization organization = new OmsOrganization();
        organization.setId(id);
        organization.setStatus(status);
        return updateById(organization);
    }

    @Override
    public boolean update(Long id, OmsOrganization organization) {
        organization.setId(id);
        boolean success = updateById(organization);
        return success;
    }

    @Override
    public OmsOrganization getOrganizationByName(String name) {
        return omsOrganizationMapper.getOrgByName(name);
    }


    /**
     * 修改單位層級
     */
    private void updateLevel(OmsOrganization omsOrganization) {
        if (omsOrganization.getParentId() == 0) {
            //没有父菜單時為一級菜單
            omsOrganization.setLevel(0);
        } else {
            //有父菜單時選擇根據父菜單level設置
            OmsOrganization parentOrganization = getById(omsOrganization.getParentId());
            if (parentOrganization != null) {
                omsOrganization.setLevel(parentOrganization.getLevel() + 1);
            } else {
                parentOrganization.setLevel(0);
            }
        }
    }

    @Override
    public List<OmsOrganization> organizationLowList(Long parentId) {
        log.info("organizationLowList.......{}", parentId);
        return omsOrganizationMapper.getOrgListByParentId(parentId);
    }

    @Override
    public Page<OmsOrganization> list(Integer pageSize, Integer pageNum) {

        Page<OmsOrganization> page = new Page<>(pageNum, pageSize);
        QueryWrapper<OmsOrganization> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<OmsOrganization> lambda = wrapper.lambda();

        return page(page, wrapper);
    }

    @Override
    public Page<OmsOrganization> listCombox(Integer pageSize, Integer pageNum) {

        Page<OmsOrganization> page = new Page<>(pageNum, pageSize);
        QueryWrapper<OmsOrganization> wrapper = new QueryWrapper<>();

        LambdaQueryWrapper<OmsOrganization> lambda = wrapper.lambda();
        lambda.orderByDesc(OmsOrganization::getSort);

        return page(page, wrapper);
    }

    @Override
    public Long getMaxId() {
        return omsOrganizationMapper.getOrgMax();
    }

    @Override
    public List<OmsOrganizationNode> treeList() {
        List<OmsOrganization> organizationList = list();
        List<OmsOrganizationNode> result = organizationList.stream()
                .filter(organization -> organization.getParentId().equals(0L))
                .map(organization -> covertOrgNode(organization, organizationList)).collect(Collectors.toList());
        return result;
    }

    private OmsOrganizationNode covertOrgNode(OmsOrganization organization, List<OmsOrganization> organizationList) {
        OmsOrganizationNode node = new OmsOrganizationNode();
        BeanUtils.copyProperties(organization, node);
        List<OmsOrganizationNode> children = organizationList.stream()
                .filter(subOrg -> subOrg.getParentId().equals(organization.getId()))
                .map(subOrg -> covertOrgNode(subOrg, organizationList)).collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }
}
