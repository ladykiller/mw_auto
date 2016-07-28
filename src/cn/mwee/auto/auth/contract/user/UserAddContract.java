package cn.mwee.auto.auth.contract.user;

import cn.mwee.auto.deploy.contract.commom.BaseContract;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Administrator on 2016/7/18.
 */
@Data
public class UserAddContract extends BaseContract {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String userName;
}
