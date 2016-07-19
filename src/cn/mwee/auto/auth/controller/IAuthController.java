/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.auth.controller;

import cn.mwee.auto.misc.controller.IController;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;

/**
 * @author mengfanyuan
 * 2016年6月30日上午9:49:55
 */
public interface IAuthController extends IController {
	/**
	 * 登录
	 * @param request
	 * @return
     */
	NormalReturn login(ServiceRequest request);

	/**
	 * 登出
	 * @param request
	 * @return
     */
	NormalReturn logout(ServiceRequest request);


	/**
	 * 新增用户
	 * @param request
	 * @return
     */
	NormalReturn userAdd(ServiceRequest request);

	/**
	 * 用户列表
	 * @param request
	 * @return
	 */
	NormalReturn userList(ServiceRequest request);


	/**
	 * 修改密码
	 * @param request
	 * @return
	 */
	NormalReturn updatePassword(ServiceRequest request);

	/**
	 * 用户授权
	 * @param request
	 * @return
	 */
	NormalReturn userGrant(ServiceRequest request);
	/**
	 * 删除用户
	 * @param request
	 * @return
	 */
	NormalReturn userDel(ServiceRequest request);

    /**
	 * 检查用户是否登录
	 * @param request
	 * @return
     */
	NormalReturn check(ServiceRequest request);

	/**
	 * 新增菜单
	 * @param request
	 * @return
	 */
	NormalReturn addMenu(ServiceRequest request);

	/**
	 * 更新菜单
	 * @param request
	 * @return
     */
	NormalReturn updateMenu(ServiceRequest request);

	/**
	 * 删除菜单
	 * @param request
	 * @return
     */
	NormalReturn delMenu(ServiceRequest request);

	/**
	 * 查询菜单
	 * @param request
	 * @return
     */
	NormalReturn queryMenu(ServiceRequest request);


}
