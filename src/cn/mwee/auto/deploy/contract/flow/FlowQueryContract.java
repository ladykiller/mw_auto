package cn.mwee.auto.deploy.contract.flow;

import cn.mwee.auto.deploy.contract.commom.BaseContract;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2016/7/29.
 */
@Data
public class FlowQueryContract extends BaseContract {

    /**
     * 项目Id
     */
    @NotNull(message = "未指定项目Id")
    private Integer projectId;
}
