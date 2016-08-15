package cn.mwee.auto.auth.contract.user;

import cn.mwee.auto.deploy.contract.commom.BaseContract;
import cn.mwee.auto.misc.common.util.StringUtil;
import lombok.Data;
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

    private String password;

    /**
     *
     */
    private String name;


    private String email;

    private String phoneNo;
    private String department;
}
