/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.auth.controller.impl;

import cn.mwee.auto.auth.contract.*;
import cn.mwee.auto.auth.model.AuthUser;
import cn.mwee.auto.auth.service.IUserService;
import cn.mwee.auto.deploy.contract.commom.BaseContract;
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

	@Override
	@RequiresPermissions(value = {"/auth/addUser"},logical = Logical.OR)
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
	@RequiresPermissions(value = {"/auth/userList"},logical = Logical.OR)
	@Contract(BaseContract.class)
	public NormalReturn userList(ServiceRequest request) {

		BaseContract req = request.getContract();
		try {
			userService.queryUsers(req);
			return  new NormalReturn("200","success",req);
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
	@RequiresPermissions(value = "/auth/userGrant",logical = Logical.OR)
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
	@RequiresPermissions(value = "/auth/userDel",logical = Logical.OR)
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

	@Override
	public NormalReturn addMenu(ServiceRequest request) {
		return null;
	}

	@Override
	public NormalReturn updateMenu(ServiceRequest request) {
		return null;
	}

	@Override
	public NormalReturn delMenu(ServiceRequest request) {
		return null;
	}

	@Override
	public NormalReturn queryMenu(ServiceRequest request) {
		return null;
	}
}
