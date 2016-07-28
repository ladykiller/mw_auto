/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.auth.deploy;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.mwee.auto.deploy.model.Flow;
import cn.mwee.auto.deploy.model.FlowTask;
import cn.mwee.auto.deploy.service.IFlowManagerService;
import cn.mwee.auto.deploy.util.AutoConsts.TaskState;

/**
 * @author mengfanyuan
 * 2016年7月4日下午3:29:05
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/applicationContext.xml",})
public class FlowManagerServiceTest extends AbstractJUnit4SpringContextTests {

	@Resource
	private IFlowManagerService flowManagerService;
	
	/**
	 * Test method for {@link cn.mwee.auto.deploy.service.impl.FlowManagerService#initFlowTasks(java.util.List, java.util.HashMap, int)}.
	 */
	@Test
	public void testInitFlowTasks() {
		boolean flag = flowManagerService.initFlowTasks(4);
		assertTrue(flag);
	}

	/**
	 * Test method for {@link cn.mwee.auto.deploy.service.impl.FlowManagerService#updateFlowStatus(int)}.
	 */
	@Test
	public void testUpdateFlowStatus() {
		assertTrue(flowManagerService.updateFlowStatus(8));
	}

	/**
	 * Test method for {@link cn.mwee.auto.deploy.service.impl.FlowManagerService#getCurrentGroupNextTask(int, byte, int, int)}.
	 */
	@Test
	public void testGetCurrentGroupNextTask() {
		FlowTask task = flowManagerService.getCurrentGroupNextTask(12);
		assertNull(task);
	}

	/**
	 * Test method for {@link cn.mwee.auto.deploy.service.impl.FlowManagerService#checkConcurrentGroupsFinished(int, int)}.
	 */
	@Test
	public void testCheckConcurrentGroupsFinished() {
		assertFalse(flowManagerService.checkConcurrentGroupsFinished(12));
	}

	@Test
	public void testUpdateTaskStatus() {
		assertTrue(flowManagerService.updateTaskStatus(12, TaskState.ING.name()));
	}
	
	
	@Test
	public void testCreateFlow() throws Exception {
		Flow flow = new Flow();
		flow.setName("测试流程");
		flow.setTemplateId(1);
		flow.setZones("192.168.0.1,192.168.0.2,192.168.0.3,192.168.0.4");
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("1", "aa bb");
		boolean flag = flowManagerService.createFlow(flow, params) > 0;
		assertTrue(flag);
	}
	
	@Test
	public void testExecuteFlow() throws Exception {
		boolean flag = flowManagerService.executeFlow(11);
		try {
			Thread.currentThread().sleep(10*60*60*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(flag);
	}
	
	@Test
	public void testGetStartFlowTask() {
		List<FlowTask> list = flowManagerService.getStartFlowTask(5);
		assertTrue(list.size()>0);
	}
	
	@Test
	public void testGetNextGroupStartFlowTasks() {
		List<FlowTask> list = flowManagerService.getNextGroupStartFlowTasks(59);
		assertTrue(list.size()>0);
	}
}
