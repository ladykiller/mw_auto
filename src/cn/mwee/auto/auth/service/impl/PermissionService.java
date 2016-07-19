/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.auth.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.mwee.auto.auth.dao.AuthPermissionMapper;
import cn.mwee.auto.auth.model.AuthPermission;
import cn.mwee.auto.auth.model.AuthPermissionExample;
import cn.mwee.auto.auth.service.IPermissionService;

/**
 * manage permission info
 * 
 * @author mengfanyuan
 * 2016年6月29日下午1:38:05
 */
public class PermissionService implements IPermissionService {

	@Autowired
	private AuthPermissionMapper authPermissionMapper;
	
	@Override
	public boolean add(AuthPermission authPermission) {
		// TODO Auto-generated method stub
		return authPermissionMapper.insertSelective(authPermission) > 0;
	}

	@Override
	public boolean update(AuthPermission authPermission) {
		// TODO Auto-generated method stub
		return authPermissionMapper.updateByPrimaryKeySelective(authPermission) > 0;
	}

	@Override
	public boolean del(Integer id) {
		// TODO Auto-generated method stub
		return authPermissionMapper.deleteByPrimaryKey(id) > 0;
	}

	@Override
	public AuthPermission select(Integer id) {
		// TODO Auto-generated method stub
		return authPermissionMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<AuthPermission> query(AuthPermission authPermission) {
		// TODO Auto-generated method stub
		AuthPermissionExample example = new AuthPermissionExample();
		example.createCriteria().andNameLike(authPermission.getName());
		return authPermissionMapper.selectByExample(example);
	}

}
