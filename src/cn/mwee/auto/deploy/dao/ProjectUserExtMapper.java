package cn.mwee.auto.deploy.dao;

import cn.mwee.auto.auth.model.AuthUser;
import cn.mwee.auto.deploy.model.ProjectUser;
import cn.mwee.auto.deploy.model.ProjectUserExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProjectUserExtMapper {

    @Select("SELECT u.* FROM auth_user u LEFT JOIN project_user pu ON u.id = pu.userId WHERE pu.projectId = #{projectId}")
    @ResultMap("cn.mwee.auto.auth.dao.AuthUserMapper.BaseResultMap")
    List<AuthUser> selectUsers4Project(@Param("projectId") Integer projectId);
}