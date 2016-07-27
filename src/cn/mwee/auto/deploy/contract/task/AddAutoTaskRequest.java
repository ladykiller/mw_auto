package cn.mwee.auto.deploy.contract.task;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by huming on 16/7/19.
 */
@Data
public class AddAutoTaskRequest
{
    @NotBlank(message="任务名称不能为空")
    private String name;

    @NotBlank(message="exec不能为空")
    private String exec;

    @NotBlank(message="任务描述不能为空")
    private String desc;

    private String execUser;

    private String params;
}
