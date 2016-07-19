/**
 * 
 */
package cn.mwee.auto.auth.service;

import java.util.List;

import cn.mwee.auto.auth.model.AuthRole;

/**
 * 角色信息维护服务
 * 
 * @author mengfanyuan
 * 2016年6月28日下午7:45:05
 */
public interface IRoleService {
	boolean add(AuthRole authRole);
	boolean update(AuthRole authRole);
	boolean del(Integer id);
	AuthRole select(Integer id);
	List<AuthRole> query(AuthRole authRole);
}
