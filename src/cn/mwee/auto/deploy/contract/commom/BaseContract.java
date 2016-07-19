/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.deploy.contract.commom;


import javax.validation.constraints.Min;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import lombok.Data;

/**
 * @author mengfanyuan
 * 2016年7月6日下午5:38:58
 */
@Data
public class BaseContract {

	/**
	 * 用户token
	 */
	private String token;

	/** 每页记录，默认20 **/
	@Min(value=1)
	private Integer pageSize = 20;
	
	/** 页码，默认1 **/
	@Min(value=1)
	private Integer pageIndex = 1;
	
	/** 排序列，默认Id **/
	private String sortInfo = "id desc";
	
	/** 总页数 **/
	private Integer pageSum;
	
	/** 总条数 **/
	private Integer count = 0;
	
	/** 返回的数据 **/
	private Object data;
	
	@JSONField(serialize=false)
	public Integer getLimitStart() {
		return (pageIndex - 1) * pageSize;
	}
	
	@JSONField(serialize=false)
	public Integer getLimitEnd(){
		return pageSize;
	}
	
	public Integer getPageSum () {
		pageSum = (count + pageSize -1 ) / pageSize;
		return pageSum;
	}
	
	
}
