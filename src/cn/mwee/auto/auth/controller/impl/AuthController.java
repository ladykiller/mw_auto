/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.auth.controller.impl;

import cn.mwee.auto.auth.contract.*;
import cn.mwee.auto.auth.contract.permission.PermissionAddContract;
import cn.mwee.auto.auth.contract.permission.PermissionContract;
import cn.mwee.auto.auth.contract.permission.PermissionQueryContract;
import cn.mwee.auto.auth.contract.role.RoleAddContract;
import cn.mwee.auto.auth.contract.role.RoleGrantContract;
import cn.mwee.auto.auth.contract.role.RoleQueryContract;
import cn.mwee.auto.auth.contract.role.RoleUpdateContract;
import cn.mwee.auto.auth.model.AuthPermission;
import cn.mwee.auto.auth.model.AuthRole;
import cn.mwee.auto.auth.model.AuthUser;
import cn.mwee.auto.auth.service.IPermissionService;
import cn.mwee.auto.auth.service.IRoleService;
import cn.mwee.auto.auth.service.IUserService;
import cn.mwee.auto.deploy.contract.commom.BaseContract;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cn.mwee.auto.auth.controller.IAuthController;
import cn.mwee.auto.misc.aspect.contract.Contract;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;

/**
 * @author mengfanyuan
 * 2016年6月30日上午9:51:32
 */
@Controller("authController")
public class AuthController implements IAuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private IUserService userService;

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private IRoleService roleService;

	@Override
	@Contract(LoginReq.class)
	public NormalReturn login(ServiceRequest request) {
		LoginReq req = request.getContract();
		try {
			Subject subject = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(req.getUserName(), req.getPassword());
			subject.login(token);
			Session session = subject.getSession(false);
			session.setTimeout(24*60*60*1000L);
			session.setAttribute("subject", subject);
			LoginResp resp = new LoginResp();
			resp.setToken(subject.getSession().getId().toString());
			return new NormalReturn("200","success",resp);
		} catch (AuthenticationException e) {
			return new NormalReturn("502","error","用户名/密码错误");
		} catch (Exception e) {
			logger.error("login error", e);
			return new NormalReturn("500","error",e.getMessage());
		}
	}

	@Override
	@Contract(LoginResp.class)
	public NormalReturn logout(ServiceRequest request) {
		LoginResp req = request.getContract();
		try {
			Session session = SecurityUtils.getSecurityManager().getSession(new DefaultSessionKey(req.getToken()));
			if (session != null) {
                Subject subject = (Subject)session.getAttribute("subject");
                subject.logout();
            }
		} catch (Exception e) {
			logger.error("",e);
		}
		return new NormalReturn("200","success","Logout success!!");
	}

	@Override
	@Contract(LoginResp.class)
	public NormalReturn check(ServiceRequest request) {
		int result = -1;
		try {
			LoginResp req = request.getContract();
			Session session = SecurityUtils.getSecurityManager().getSession(new DefaultSessionKey(req.getToken()));
			result = session==null ? -1 : 1;
		}  catch (Exception e) {
			logger.error("",e);
		}
		return new NormalReturn("200","ok",result);
	}






}
