/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.auth.service.impl;

import java.util.List;

import cn.mwee.auto.auth.contract.role.RoleQueryContract;
import org.springframework.beans.factory.annotation.Autowired;

import cn.mwee.auto.auth.dao.AuthRoleMapper;
import cn.mwee.auto.auth.model.AuthRole;
import cn.mwee.auto.auth.model.AuthRoleExample;
import cn.mwee.auto.auth.service.IRoleService;

/**
 * manage role info
 * 
 * @author mengfanyuan
 * 2016年6月29日下午1:37:12
 */
public class RoleService implements IRoleService {

	@Autowired
	private AuthRoleMapper authRoleMapper;
	
	@Override
	public boolean add(AuthRole authRole) {
		// TODO Auto-generated method stub
		return authRoleMapper.insertSelective(authRole) > 0;
	}

	@Override
	public boolean update(AuthRole authRole) {
		// TODO Auto-generated method stub
		return authRoleMapper.updateByPrimaryKeySelective(authRole) > 0;
	}

	@Override
	public boolean del(Integer id) {
		// TODO Auto-generated method stub
		return authRoleMapper.deleteByPrimaryKey(id) > 0;
	}

	@Override
	public AuthRole select(Integer id) {
		// TODO Auto-generated method stub
		return authRoleMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<AuthRole> query(RoleQueryContract roleQueryContract) {
		// TODO Auto-generated method stub
		AuthRoleExample example = new AuthRoleExample();
		example.createCriteria().andRolenameLike(roleQueryContract.getRoleName());
		authRoleMapper.selectByExample(example);
		return null;
	}

}
