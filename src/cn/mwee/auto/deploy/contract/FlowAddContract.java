/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.deploy.contract;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

/**
 * @author mengfanyuan
 * 2016年7月6日下午5:27:18
 */
@Data
public class FlowAddContract {
	/** 名称 **/
	@NotBlank(message="流程名称不能为空")
	private String name;
	/** 模板 **/
	@NotNull(message="未指定模板")
	private Integer templateId;
    /**
     * 项目Id
     */
    @NotNull(message="未指定项目Id")
	private Integer projectId;

	/** 区域 **/
	@NotBlank(message="未指定区域")
	private String zones;
	/** 执行参数 **/
	private Map<String, Object> params;
}
