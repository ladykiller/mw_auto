package cn.mwee.auto.auth.dao;


import cn.mwee.auto.auth.model.AuthUserRole;

import java.util.List;

public interface AuthUserRoleExtMapper {

    /**
     * 批量插入
     * @return
     */
    int insertBatch(List<AuthUserRole> list);

}