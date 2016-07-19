package cn.mwee.auto.auth.contract.role;

import cn.mwee.auto.deploy.contract.commom.BaseContract;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2016/7/19.
 */
@Data
public class RoleGrantContract extends BaseContract {

    /**
     * 角色Id
     */
    @NotNull(message = "未指定角色")
    private Integer roleId;

    /**
     * 权限集
     */
    @NotBlank(message = "未指定权限集")
    private String permissionStr;

}
