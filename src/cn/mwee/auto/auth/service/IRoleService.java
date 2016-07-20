/**
 * 
 */
package cn.mwee.auto.auth.service;

import java.util.List;

import cn.mwee.auto.auth.contract.role.RoleQueryContract;
import cn.mwee.auto.auth.model.AuthRole;
import cn.mwee.auto.auth.model.AuthRolePermission;
import cn.mwee.auto.common.db.BaseQueryResult;

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
     * 根据名称查找
     * @param roleName
     * @return
     */
    AuthRole selectByName(String roleName);

	/**
	 * 查询（含分页）
	 * @param roleQueryContract
	 * @return
     */
	BaseQueryResult<AuthRole> query(RoleQueryContract roleQueryContract);

	/**
	 * 角色赋权
	 * @return
	 */
	int updateRoleAuth(Integer roleId,String permissionStr);

    /**
     * 查询角色权限
     * @param roleId
     * @return
     */
    List<AuthRolePermission> queryRoleAuths(Integer roleId);
}
