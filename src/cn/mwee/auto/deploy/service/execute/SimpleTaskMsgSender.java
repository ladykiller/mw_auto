/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.deploy.service.execute;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cn.mwee.auto.deploy.model.FlowTask;
import cn.mwee.auto.misc.mq.AppEvent;
import cn.mwee.auto.misc.mq.MQSender;

/**
 * @author mengfanyuan
 * 2016年7月6日上午11:29:14
 */
@Component
public class SimpleTaskMsgSender implements TaskMsgSender {

	@Resource
	private MQSender appEventMQSender;
	
	/* (non-Javadoc)
	 * @see cn.mwee.auto.deploy.service.execute.TaskMsgSender#sendTasks(java.util.List)
	 */
	@Override
	public void sendTasks(List<FlowTask> flowTasks) {
		// TODO Auto-generated method stub
		for (FlowTask flowTask : flowTasks) {
			sendTask(flowTask);
		}
	}

	@Override
	public void sendTask(FlowTask flowTask) {
		// TODO Auto-generated method stub
		AppEvent event = new AppEvent(0, "soa.notify.flow.create", flowTask);
		appEventMQSender.sendAsync(event.getEventType(), event);
	}

}
