/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.deploy.service.execute;

import cn.mwee.auto.deploy.model.AutoTask;
import cn.mwee.auto.deploy.model.FlowTask;

/**
 * @author mengfanyuan
 * 2016年7月5日下午8:08:58
 */
public interface TaskExecutor {
	void executeChain(FlowTask flowTask);
	boolean execute(FlowTask flowTask);
}
