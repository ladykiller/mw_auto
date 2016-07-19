/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.deploy.service.execute;

import java.util.List;import cn.mwee.auto.deploy.model.Flow;
import cn.mwee.auto.deploy.model.FlowTask;

/**
 * @author mengfanyuan
 * 2016年7月6日上午11:27:54
 */
public interface TaskMsgSender {
	void sendTasks(List<FlowTask> flowTasks);
	void sendTask(FlowTask flowTask);
}
