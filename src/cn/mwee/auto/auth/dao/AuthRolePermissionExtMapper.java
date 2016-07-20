package cn.mwee.auto.auth.dao;

import cn.mwee.auto.auth.model.AuthRolePermission;

import java.util.List;

public interface AuthRolePermissionExtMapper {
    /**
     * batch insert
     * @param list
     * @return
     */
    int insertBatch(List<AuthRolePermission> list);
}