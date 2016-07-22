package cn.mwee.auto.deploy.contract;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * Created by huming on 16/7/21.
 */
@Data
public class TemplateTaskIdQuery {

    @Range(min = 1,message = "invalid templateTaskId value")
    private Integer templateTaskId;

}
