package cn.mwee.auto.deploy.contract;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by huming on 16/7/21.
 */
@Data
public class AddTemplateRequest {

    @NotBlank(message="未指定模板名")
    private String templateName;

}
