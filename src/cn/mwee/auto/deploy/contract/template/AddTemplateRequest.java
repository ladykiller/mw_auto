package cn.mwee.auto.deploy.contract.template;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by huming on 16/7/21.
 */
@Data
public class AddTemplateRequest {

    @NotBlank(message="未指定模板名")
    private String templateName;

    private String vcsType;

    /**
     * 版本仓库地址
     * templates.vcs_rep
     *
     * @mbggenerated
     */
    private String vcsRep;
}
