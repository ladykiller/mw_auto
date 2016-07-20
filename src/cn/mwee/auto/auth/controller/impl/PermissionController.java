package cn.mwee.auto.auth.controller.impl;

import cn.mwee.auto.auth.contract.permission.PermissionAddContract;
import cn.mwee.auto.auth.contract.permission.PermissionContract;
import cn.mwee.auto.auth.contract.permission.PermissionQueryContract;
import cn.mwee.auto.auth.controller.IPermissionController;
import cn.mwee.auto.auth.model.AuthPermission;
import cn.mwee.auto.auth.service.IPermissionService;
import cn.mwee.auto.misc.aspect.contract.Contract;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by Administrator on 2016/7/20.
 */
@Controller
public class PermissionController implements IPermissionController {
    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private IPermissionService permissionService;

    @Override
    @RequiresPermissions(value = "/permission/addPermission",logical = Logical.OR)
    @Contract(PermissionAddContract.class)
    public NormalReturn addPermission(ServiceRequest request) {
        PermissionAddContract req = request.getContract();
        try{
            if (permissionService.selectByCode(req.getCode()) != null) {
                return new NormalReturn("500","error","Code already used");
            }
            AuthPermission authPermission = new AuthPermission();
            authPermission.setParentId(req.getParentId());
            authPermission.setCode(req.getCode());
            authPermission.setName(req.getName());
            authPermission.setType(req.getType());
            authPermission.setDescription(req.getDescription());
            permissionService.add(authPermission);
            return new NormalReturn("200","success","success");
        } catch (Exception e) {
            logger.error("",e);
            return new NormalReturn("500","error",e.getMessage());
        }
    }

    @Override
    @RequiresPermissions(value = "/permission/updatePermission",logical = Logical.OR)
    @Contract(PermissionContract.class)
    public NormalReturn updatePermission(ServiceRequest request) {
        PermissionContract req = request.getContract();
        try {
            AuthPermission authPermission = new AuthPermission();
            authPermission.setId(req.getId());
            if (req.getParentId()!=null) authPermission.setParentId(req.getParentId());
            if (req.getType() != null) authPermission.setType(req.getType());
            if (StringUtils.isNotBlank(req.getCode())) authPermission.setCode(req.getCode());
            if (StringUtils.isNotBlank(req.getName())) authPermission.setName(req.getName());
            if (StringUtils.isNotBlank(req.getDescription())) authPermission.setDescription(req.getDescription());
            permissionService.update(authPermission);
            return new NormalReturn("200","success","success");
        } catch (Exception e) {
            logger.error("",e);
            return new NormalReturn("500","error",e.getMessage());
        }
    }

    @Override
    @RequiresPermissions(value = "/permission/delPermission",logical = Logical.OR)
    @Contract(PermissionContract.class)
    public NormalReturn delPermission(ServiceRequest request) {
        PermissionContract req = request.getContract();
        try {
            permissionService.delete(req.getId());
            return new NormalReturn("200","success","success");
        } catch (Exception e) {
            logger.error("",e);
            return new NormalReturn("500","error",e.getMessage());
        }
    }

    @Override
    @RequiresPermissions(value = "/permission/queryPermission",logical = Logical.OR)
    @Contract(PermissionQueryContract.class)
    public NormalReturn queryPermission(ServiceRequest request) {
        PermissionQueryContract req = request.getContract();
        try {
            return new NormalReturn("200","success",permissionService.query(req));
        } catch (Exception e) {
            logger.error("",e);
            return new NormalReturn("500","error",e.getMessage());
        }
    }
}
