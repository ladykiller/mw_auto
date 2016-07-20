package cn.mwee.auto.auth.contract.user;

import cn.mwee.auto.deploy.contract.commom.BaseContract;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Administrator on 2016/7/19.
 */
@Data
public class PassWordContract extends BaseContract {

    /**
     * 新密码
     */
    @NotBlank(message = "密码不能为空")
    private String newPassword;

}
