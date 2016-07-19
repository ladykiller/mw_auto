/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.deploy.service.execute;

import java.util.List;

import javax.annotation.Resource;

import cn.mwee.auto.misc.common.util.SSHManager;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import cn.mwee.auto.deploy.model.AutoTask;
import cn.mwee.auto.deploy.model.FlowTask;
import cn.mwee.auto.deploy.service.IFlowManagerService;
import cn.mwee.auto.deploy.service.IFlowTaskLogService;
import cn.mwee.auto.deploy.service.ITaskManagerService;
import cn.mwee.auto.deploy.util.AutoConsts.TaskState;

/**
 * @author mengfanyuan
 * 2016年7月5日下午8:13:34
 */
@Component
public class SimpleTaskExecutor implements TaskExecutor {
	private Logger logger = LoggerFactory.getLogger(SimpleTaskExecutor.class);
	
	@Autowired
	private IFlowManagerService flowManagerService;
	
	@Autowired
	private ITaskManagerService taskManagerService;
	
	@Autowired
	private IFlowTaskLogService flowTaskLogService;
	
	@Resource
	private ThreadPoolTaskExecutor springTaskExecutor;
	
	@Resource
	private TaskMsgSender taskMsgSender;

	@Value("${auto.ssh.user}")
	private String autoSshUser;

	@Value("${auto.ssh.pri.addr}")
	private String autoSshPriAddr;

	@Override
	public void executeChain(FlowTask flowTask) {
		// TODO Auto-generated method stub
		try {
			flowManagerService.updateTaskStatus(flowTask.getId(), TaskState.ING.name());
			if (!execute(flowTask)) {
				throw new Exception();
			}
			flowManagerService.updateTaskStatus(flowTask.getId(), TaskState.SUCCESS.name());
			FlowTask nextFlowTask = flowManagerService.getCurrentGroupNextTask(flowTask.getId());
			if (nextFlowTask != null) {
				//执行下一任务
				executeChain(nextFlowTask);
			} else {
				//当前组任务执行结束
				System.out.println();
				if (flowManagerService.checkConcurrentGroupsFinished(flowTask.getId())) {
					List<FlowTask> nextGroupTasks = flowManagerService.getNextGroupStartFlowTasks(flowTask.getId());
					flowManagerService.updateFlowStatus(flowTask.getFlowId());
					//还有未执行的组任务
					if (CollectionUtils.isNotEmpty(nextGroupTasks)) {
						taskMsgSender.sendTasks(nextGroupTasks);
					} 
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Execute task error: FlowTask[flowTaskId={},flowId={},taskId={},params={}]", flowTask.getId(),flowTask.getFlowId(),flowTask.getTaskId(),flowTask.getTaskParam());
			logger.error("Execute task error", e);
			flowManagerService.updateTaskStatus(flowTask.getId(), TaskState.ERROR.name());
			flowManagerService.updateFlowStatus(flowTask.getFlowId());
		}
		
	}

	@Override
	public boolean execute(FlowTask flowTask)
	{

		AutoTask task = taskManagerService.getTask(flowTask.getTaskId());
		
		SSHManager instance = null;
		
		Integer logId = flowTaskLogService.addLog(flowTask,task);
		try
		{
			String command = task.getExec();

			instance = new SSHManager(autoSshUser,autoSshPriAddr, flowTask.getZone(), flowTaskLogService, springTaskExecutor,logId );

			String errorMessage = instance.connect();

			if(errorMessage != null)
			{
				logger.error(errorMessage);
				flowTaskLogService.addLineLog(logId, errorMessage);
				return false;
			}

			String result = instance.sendCommand(command,flowTask.getId());
			logger.info("----result----");
			logger.info(result);

			if(result.contains("MW_SUCCESS"))
			{
				logger.info("task[id={},name={}] execute success!!!!", task.getId(),task.getName());
				return true;
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
			flowTaskLogService.addLineLog(logId, e.getMessage());
		}
		finally
		{
			try
			{
				instance.close();
			}
			catch (Exception e) {}
		}
		return false;
	}
	
}
