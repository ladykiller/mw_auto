package cn.mwee.auto.auth.controller.impl;

import cn.mwee.auto.auth.contract.user.*;
import cn.mwee.auto.auth.controller.IUserController;
import cn.mwee.auto.auth.model.AuthUser;
import cn.mwee.auto.auth.service.IUserService;
import cn.mwee.auto.deploy.contract.commom.BaseContract;
import cn.mwee.auto.misc.aspect.contract.Contract;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by Administrator on 2016/7/20.
 */
@Controller
public class UserController implements IUserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService userService;


    @Override
    @RequiresPermissions(value = {"/user/addUser"},logical = Logical.OR)
    @Contract(UserAddContract.class)
    public NormalReturn userAdd(ServiceRequest request) {
        UserAddContract req = request.getContract();
        try {
            AuthUser authUser = new AuthUser();
            authUser.setUsername(req.getUserName());
            authUser.setPassword(req.getPassword());
            userService.addUser(authUser);
            return  new NormalReturn("200","success","success");
        } catch (Exception e) {
            logger.error("",e);
            return  new NormalReturn("500","error",e.getMessage());
        }
    }

    @Override
    @RequiresPermissions(value = {"/user/userList"},logical = Logical.OR)
    @Contract(UserQueryContract.class)
    public NormalReturn userList(ServiceRequest request) {
        UserQueryContract req = request.getContract();
        try {
            return  new NormalReturn("200","success",userService.queryUsers(req));
        } catch (Exception e) {
            logger.error("",e);
            return  new NormalReturn("500","error",e.getMessage());
        }
    }

    @Override
    @RequiresAuthentication
    @Contract(PassWordContract.class)
    public NormalReturn updatePassword(ServiceRequest request) {
        PassWordContract req = request.getContract();
        try {
            String currentUserName = SecurityUtils.getSubject().getPrincipal().toString();
            AuthUser authUser = new AuthUser();
            authUser.setUsername(currentUserName);
            authUser.setPassword(req.getNewPassword());
            if (userService.updatePassword(authUser)) {
                return  new NormalReturn("200","success","success");
            } else {
                return  new NormalReturn("500","error","Can not find User");
            }

        } catch (Exception e) {
            logger.error("updatePassword error",e);
            return  new NormalReturn("500","error",e.getMessage());
        }
    }

    @Override
    @RequiresPermissions(value = "/user/userGrant",logical = Logical.OR)
    @Contract(UserGrantContract.class)
    public NormalReturn userGrant(ServiceRequest request) {
        UserGrantContract req = request.getContract();
        try {
            AuthUser authUser = userService.queryByUserName(req.getUserName());
            if (authUser == null) {
                return  new NormalReturn("500","error","Can not find User:"+req.getUserName());
            }
            userService.updateUserGrant(authUser,req.getRoles());
            return  new NormalReturn("200","success","success");
        } catch (Exception e) {
            logger.error("userGrant error",e);
            return  new NormalReturn("500","error",e.getMessage());
        }
    }

    @Override
    @RequiresPermissions(value = "/user/userDel",logical = Logical.OR)
    @Contract(UserDelContract.class)
    public NormalReturn userDel(ServiceRequest request) {
        UserDelContract req = request.getContract();
        try {
            userService.delUserLogic(req.getUserId());
            return  new NormalReturn("200","success","success");
        } catch (Exception e) {
            logger.error("userGrant error",e);
            return  new NormalReturn("500","error",e.getMessage());
        }
    }
}