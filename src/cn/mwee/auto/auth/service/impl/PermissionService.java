/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.auth.service.impl;

import java.util.List;

import cn.mwee.auto.auth.contract.permission.PermissionContract;
import cn.mwee.auto.auth.contract.permission.PermissionQueryContract;
import org.springframework.beans.factory.annotation.Autowired;

import cn.mwee.auto.auth.dao.AuthPermissionMapper;
import cn.mwee.auto.auth.model.AuthPermission;
import cn.mwee.auto.auth.model.AuthPermissionExample;
import cn.mwee.auto.auth.service.IPermissionService;
import org.springframework.stereotype.Service;

/**
 * manage permission info
 * 
 * @author mengfanyuan
 * 2016年6月29日下午1:38:05
 */
@Service
public class PermissionService implements IPermissionService {

	@Autowired
	private AuthPermissionMapper authPermissionMapper;
	
	@Override
	public boolean add(AuthPermission authPermission) {

		return authPermissionMapper.insertSelective(authPermission) > 0;
	}

	@Override
	public boolean update(PermissionContract permissionContract) {
		// TODO Auto-generated method stub\
		AuthPermission authPermission = new AuthPermission();
		authPermission.setId(permissionContract.getId());
		return authPermissionMapper.updateByPrimaryKeySelective(authPermission) > 0;
	}

	@Override
	public boolean delete(Integer id) {
		// TODO Auto-generated method stub
		return authPermissionMapper.deleteByPrimaryKey(id) > 0;
	}

	@Override
	public AuthPermission select(Integer id) {
		// TODO Auto-generated method stub
		return authPermissionMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<AuthPermission> query(PermissionQueryContract permissionQuery) {
		// TODO Auto-generated method stub
		AuthPermissionExample example = new AuthPermissionExample();
		example.createCriteria().andNameLike(permissionQuery.getName());
		return authPermissionMapper.selectByExample(example);
	}

	@Override
	public int updateRoleAuth(Integer roleId, String permissionStr) {
		return 0;
	}
}
