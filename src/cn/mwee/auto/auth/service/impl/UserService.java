/**
 * 
 */
package cn.mwee.auto.auth.service.impl;

import java.util.*;

import javax.annotation.Resource;

import cn.mwee.auto.auth.contract.user.UserQueryContract;
import cn.mwee.auto.auth.dao.AuthUserRoleExtMapper;
import cn.mwee.auto.auth.dao.AuthUserRoleMapper;
import cn.mwee.auto.auth.model.*;
import cn.mwee.auto.auth.service.IUserRoleService;
import cn.mwee.auto.auth.util.SqlUtils;
import cn.mwee.auto.common.db.BaseModel;
import cn.mwee.auto.common.db.BaseQueryResult;
import cn.mwee.auto.deploy.contract.commom.BaseContract;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.mwee.auto.auth.dao.AuthUserMapper;
import cn.mwee.auto.auth.model.AuthUserExample.Criteria;
import cn.mwee.auto.auth.service.IUserService;
import cn.mwee.auto.auth.util.PasswordHelper;

/**
 * manager user info
 * 
 * @author mengfanyuan
 * 2016年6月29日上午9:11:27
 */
@Service
public class UserService implements IUserService {
	
	@Autowired
	private AuthUserMapper authUserMapper;

    @Autowired
    private AuthUserRoleExtMapper authUserRoleExtMapper;

	@Resource
	private PasswordHelper passwordHelper;

    @Autowired
    private IUserRoleService userRoleService;

	@Override
	public boolean addUser(AuthUser authUser) throws Exception {
		//不能增加超级用户
		if ("admin".equals(authUser.getUsername())) throw new Exception("You can not add admin user");
		//用户已存在
		if (queryByUserName(authUser.getUsername()) != null) throw new Exception("user exists");
		encryptPassword(authUser);
		authUser.setStatus(true);
		authUser.setCreateTime(new Date());
		authUser.setCreator(SecurityUtils.getSubject().getPrincipal().toString());
		int result = authUserMapper.insertSelective(authUser);
		return result > 0;
	}

	@Override
	public boolean updateUser(AuthUser authUser) {
		int result = authUserMapper.updateByPrimaryKeySelective(authUser);
		return result > 0;
	}

	@Override
	public boolean delUser(Integer authUserId) {
		int result = authUserMapper.deleteByPrimaryKey(authUserId);
		return result > 0;
	}

	@Override
	public AuthUser select(Integer authUserId) {
		AuthUser authUser = authUserMapper.selectByPrimaryKey(authUserId);
		if (authUser != null && !authUser.getStatus()) {
			return  authUser;
		}
		return null;
	}

	@Override
	public AuthUser queryByUserName(String userName) {
		AuthUserExample example = ctreteExample();
		example.createCriteria()
				.andUsernameEqualTo(userName)
				.andStatusEqualTo(true);
		List<AuthUser> users = authUserMapper.selectByExample(example);
		return users.size() > 0 ? users.get(0) : null;
	}
	
	@Override
	public List<AuthUser> query(AuthUser authUser) {
		AuthUserExample example = new AuthUserExample();
		Criteria criteria = example.createCriteria();
		criteria
				.andUsernameLike(authUser.getUsername())
				.andStatusEqualTo(true);
		return authUserMapper.selectByExample(example);
	}

	@Override
	public Set<String> queryRoles(String username) {
		AuthUser authUser = queryByUserName(username);
        if (authUser != null) {
            return authUserRoleExtMapper.queryRoles4User(authUser.getId());
        }
		return null;
	}

	@Override
	public Set<String> queryPermissions(String username) {
        //获取用户
        AuthUser authUser = queryByUserName(username);
        if (authUser != null) {
            return authUserRoleExtMapper.queryPerms4User(authUser.getId());
        }
        return null;
	}
	
	@Override
	public boolean updatePassword(AuthUser authUser) {
		AuthUser record = new AuthUser();
		record.setPassword(authUser.getPassword());
		record.setUpdateTime(new Date());
		encryptPassword(record);
        AuthUserExample example = ctreteExample();
        example.createCriteria()
                .andUsernameEqualTo(authUser.getUsername());
		return authUserMapper.updateByExampleSelective(record,example) > 0;
	}

	@Override
	public BaseQueryResult<AuthUser> queryUsers(UserQueryContract userQueryContract) {
		AuthUserExample example = new AuthUserExample();
        AuthUserExample.Criteria criteria =  example.createCriteria();
        criteria.andStatusEqualTo(true);
        if (StringUtils.isNotBlank(userQueryContract.getUserName()))
            criteria.andUsernameLike(SqlUtils.wrapLike(userQueryContract.getUserName()));
        BaseQueryResult<AuthUser> result = BaseModel.selectByPage(authUserMapper,example
                ,userQueryContract.getPageInfo(),userQueryContract.getPageInfo()==null);
        return result;
	}

    @Override
    public int updateUserGrant(AuthUser authUser, String roles) {
        String currentUser = SecurityUtils.getSubject().getPrincipal().toString();
        List<AuthUserRole> userRoles = new ArrayList<>();
        String[] roleStrs = roles.split(",");
        for (String role : roleStrs) {
			if (StringUtils.isEmpty(role)){
				continue;
			}
            AuthUserRole userRole = new AuthUserRole();
            userRole.setUserId(authUser.getId());
            userRole.setRoleId(new Integer(role));
            userRole.setCreateTime(new Date());
            userRole.setCreator(currentUser);
            userRoles.add(userRole);
        }
		userRoleService.delByUserId(authUser.getId());
		if (CollectionUtils.isNotEmpty(userRoles)) {
			return userRoleService.insertBatch(userRoles);
		} else {
			return 1;
		}


    }

	@Override
	public int delUserLogic(Integer authUserId) {
		AuthUser authUser = new AuthUser();
		authUser.setId(authUserId);
		authUser.setUpdateTime(new Date());
		authUser.setStatus(false);
		return authUserMapper.updateByPrimaryKeySelective(authUser);
	}

	/**
     * 用户密码加密
     * @param authUser
     */
	private void encryptPassword(AuthUser authUser) {
		Map<String, String> map = passwordHelper.encryptPassword(authUser.getPassword());
		authUser.setSalt(map.get("salt"));
		authUser.setPassword(map.get("password"));
	}

	private AuthUserExample ctreteExample() {
		return new AuthUserExample();
	}

	private AuthUserExample ctreteExample(BaseContract contract) {
		AuthUserExample example = new AuthUserExample();
		example.setLimitStart(contract.getLimitStart());
		example.setLimitEnd(contract.getLimitEnd());
		if (StringUtils.isNotBlank(contract.getSortInfo())) example.setOrderByClause(contract.getSortInfo());
		return example;
	}
	
}
