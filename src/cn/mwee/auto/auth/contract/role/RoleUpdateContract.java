package cn.mwee.auto.auth.contract.role;

import cn.mwee.auto.deploy.contract.commom.BaseContract;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2016/7/19.
 */
@Data
public class RoleUpdateContract extends BaseContract {

    @NotNull(message = "未指定角色")
    private Integer roleId;

    private String roleName;

    private String desc;


}
