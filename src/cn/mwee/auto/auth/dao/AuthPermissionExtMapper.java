package cn.mwee.auto.auth.dao;

import cn.mwee.auto.auth.model.AuthMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2016/7/21.
 */
public interface AuthPermissionExtMapper {
    /**
     * 获取所有菜单权限
     * @return
     */
    List<AuthMenu> selectParentMenu();

    /**
     * 获取子菜单权限
     * @param parentId
     * @return
     */
    List<AuthMenu> selectChildMenu(Integer parentId);

    /**
     * 获取用户菜单权限
     * @param userId
     * @return
     */
    List<AuthMenu> selectPrivateMenu(@Param("userId") Integer userId);

}
