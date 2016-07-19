/**
 * 
 */
package cn.mwee.auto.auth.service;

import java.util.List;

import cn.mwee.auto.auth.contract.role.RoleQueryContract;
import cn.mwee.auto.auth.model.AuthRole;

/**
 * 角色信息维护服务
 * 
 * @author mengfanyuan
 * 2016年6月28日下午7:45:05
 */
public interface IRoleService {
	/**
	 * 天剑角色
	 * @param authRole
	 * @return
     */
	boolean add(AuthRole authRole);

	/**
	 * 更新角色
	 * @param authRole
	 * @return
     */
	boolean update(AuthRole authRole);

	/**
	 * 删除角色
	 * @param id
	 * @return
     */
	boolean del(Integer id);

	/**
	 * 主键查找
	 * @param id
	 * @return
     */
	AuthRole select(Integer id);

	/**
	 * 查询（含分页）
	 * @param roleQueryContract
	 * @return
     */
	List<AuthRole> query(RoleQueryContract roleQueryContract);
}
