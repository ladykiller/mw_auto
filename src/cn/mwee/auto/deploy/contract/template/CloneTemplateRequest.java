package cn.mwee.auto.deploy.contract.template;

import lombok.Data;

/**
 * Created by huming on 16/8/12.
 */
@Data
public class CloneTemplateRequest {

    private Integer templateId;

    private Integer targetTemplateId;

}
