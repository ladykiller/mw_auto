/**
 * 
 */
package cn.mwee.auto.auth.service;

import java.util.List;

import com.rabbitmq.client.AMQP.Confirm.Select;

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
	 * @param authPermission
	 * @return
	 */
	boolean update(AuthPermission authPermission);
	
	/**
	 * delete authPermission by Id
	 * 
	 * @param id
	 * @return
	 */
	boolean del(Integer id);
	
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
	List<AuthPermission> query(AuthPermission authPermission);
}
