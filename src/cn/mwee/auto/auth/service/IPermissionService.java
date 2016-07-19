/**
 * 
 */
package cn.mwee.auto.auth.service;

import java.util.List;

import cn.mwee.auto.auth.contract.permission.PermissionContract;

import cn.mwee.auto.auth.contract.permission.PermissionQueryContract;
import cn.mwee.auto.auth.model.AuthPermission;

/**
 * 权限信息维护服务
 * 
 * @author mengfanyuan
 * 2016年6月28日下午7:45:35
 */
public interface IPermissionService {
	/**
	 * create AutePermission
	 * 
	 * @param authPermission
	 * @return
	 */
	boolean add(AuthPermission authPermission);
	
	/**
	 * update authPermission by Id
	 * 
	 * @param permissionContract
	 * @return
	 */
	boolean update(PermissionContract permissionContract);
	
	/**
	 * delete authPermission by Id
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Integer id);
	
	/**
	 * query authPermission by Id
	 * @param Id
	 * 
	 * @return
	 */
	AuthPermission select(Integer Id);
	
	/**
	 * query authPermission by condition
	 * 
	 * @param authPermission
	 * @return
	 */
	List<AuthPermission> query(PermissionQueryContract permissionQuery);

	/**
	 * 角色赋权
	 * @return
     */
	int updateRoleAuth(Integer roleId,String permissionStr);

}
