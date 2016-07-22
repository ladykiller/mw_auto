/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.deploy.controller.impl;

import java.util.Collection;
import java.util.List;

import cn.mwee.auto.deploy.AutoAbstractController;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cn.mwee.auto.deploy.contract.ExecuteFlowTaskContract;
import cn.mwee.auto.deploy.contract.FlowAddContract;
import cn.mwee.auto.deploy.contract.FlowStartContract;
import cn.mwee.auto.deploy.contract.GitBrancheContract;
import cn.mwee.auto.deploy.contract.TemplateTaskContract;
import cn.mwee.auto.deploy.contract.ZoneStateContract;
import cn.mwee.auto.deploy.controller.IDeployController;
import cn.mwee.auto.deploy.model.Flow;
import cn.mwee.auto.deploy.model.TemplateTask;
import cn.mwee.auto.deploy.service.IFlowManagerService;
import cn.mwee.auto.deploy.service.IFlowTaskLogService;
import cn.mwee.auto.deploy.service.ITemplateManagerService;
import cn.mwee.auto.misc.aspect.contract.Contract;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;

/**
 * @author mengfanyuan
 * 2016年7月6日下午5:43:37
 */
@Controller
public class DeployController extends AutoAbstractController implements IDeployController {
	private static final Logger logger = LoggerFactory.getLogger(DeployController.class);
	@Autowired
	private IFlowManagerService flowManagerService;

	@Autowired
	private IFlowTaskLogService flowTaskLogService;
	
	@Autowired
	private ITemplateManagerService templateManagerService;
	
	@Override
	@Contract(FlowAddContract.class)
	public NormalReturn addFlow(ServiceRequest request) {
		FlowAddContract req = request.getContract();
		try {
			
			int flowId = flowManagerService.createFlow(createFlowTask(req), req.getParams());
			
			if (flowId > 0) {
				return new NormalReturn("200", "success",flowId);
			} else {
				return new NormalReturn("500", "error","error");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("addFlow error:", e);
			return new NormalReturn("500", "error",e.getMessage());
		}
	}

	@Override
	@Contract(FlowStartContract.class)
	public NormalReturn startFlow(ServiceRequest request) {
		FlowStartContract req = request.getContract();
		try {
			
			if (flowManagerService.executeFlow(req.getFlowId())) {
				return new NormalReturn("200", "success", "success");
			} else {
				return new NormalReturn("500", "error", "error");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("startFlow error:", e);
			return new NormalReturn("500",e.getMessage(), "error");
		}
		
	}

	@Override
	@Contract(GitBrancheContract.class)
	public NormalReturn getGitBranches(ServiceRequest request) {
		// TODO Auto-generated method stub
		GitBrancheContract req = request.getContract();
		try {
			CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("mengfanyuan","wt19892414");
			Collection<Ref> refs = Git.lsRemoteRepository()
					.setHeads(true)
                    .setTags(true)
                    .setRemote(req.getGitRepUrl())
					.setCredentialsProvider(credentialsProvider)
                    .call();
			return new NormalReturn("200","success", refs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("startFlow error:", e);
			return new NormalReturn("500",e.getMessage(), "error");
		}
	}
	
	private Flow createFlowTask(FlowAddContract req) {
		Flow flow = new Flow();
		flow.setName(req.getName());
		flow.setTemplateId(req.getTemplateId());
		flow.setZones(req.getZones());
		return flow;
	}

	@Override
	@Contract(FlowStartContract.class)
	public NormalReturn zonesState(ServiceRequest request) {
		FlowStartContract req = request.getContract();
		try {
			return new NormalReturn("200","success", flowManagerService.getZonesState(req.getFlowId()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("startFlow error:", e);
			return new NormalReturn("500",e.getMessage(), "error");
		}
	}

	@Override
	@Contract(ZoneStateContract.class)
	public NormalReturn zonesTasksInfo(ServiceRequest request) {
		ZoneStateContract req = request.getContract();
		try {
			return new NormalReturn("200","success", flowManagerService.getZoneFlowTaskInfo(req.getFlowId(), req.getZone()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("startFlow error:", e);
			return new NormalReturn("500",e.getMessage(), "error");
		}
	}

	@Override
	@Contract(ZoneStateContract.class)
	public NormalReturn zonesTasksInfoSimple(ServiceRequest request) {
		ZoneStateContract req = request.getContract();
		try {
			return new NormalReturn("200","success", flowManagerService.getZoneFlowTaskInfoSimple(req.getFlowId(), req.getZone()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("startFlow error:", e);
			return new NormalReturn("500",e.getMessage(), "error");
		}
	}
	
	@Override
	@Contract(ExecuteFlowTaskContract.class)
	public NormalReturn executeFlowTask(ServiceRequest request) {
		ExecuteFlowTaskContract req = request.getContract();
		try {
			flowManagerService.executeFlowTask(new Integer(req.getFlowTaskId()));
			return new NormalReturn("200","success", "success");
		} catch (Exception e) {
			logger.error("startFlow error:", e);
			return new NormalReturn("500",e.getMessage(), "error");
		}
	}

	@Override
	public NormalReturn getFlows(ServiceRequest request) {
		try {
			return new NormalReturn("200","success", flowManagerService.getFlows());
		} catch (Exception e) {
			logger.error("startFlow error:", e);
			return new NormalReturn("200",e.getMessage(), "error");
		}
	}

	@Override
	@Contract(ExecuteFlowTaskContract.class)
	public NormalReturn getFolwTaskLog(ServiceRequest request) {
		ExecuteFlowTaskContract req = request.getContract();
		try {
			return new NormalReturn("200","success", flowTaskLogService.getLog4FlowTask(new Integer(req.getFlowTaskId())));
		} catch (Exception e) {
			logger.error("startFlow error:", e);
			return new NormalReturn("500",e.getMessage(), "error");
		}
	}

	@Contract(ZoneStateContract.class)
	public NormalReturn getZoneLogs(ServiceRequest request) {
		ZoneStateContract req = request.getContract();
		try {
			return new NormalReturn("200","success", flowTaskLogService.getZoneLogs(req.getFlowId(), req.getZone()));
		} catch (Exception e) {
			logger.error("startFlow error:", e);
			return new NormalReturn("500",e.getMessage(), "error");
		}
	}

	@Override
	@Contract(TemplateTaskContract.class)
	public NormalReturn getTempleteTasks(ServiceRequest request) {
		TemplateTaskContract req = request.getContract();
		try {
			List<TemplateTask>  list = templateManagerService.getTempleteTasks(req);
			req.setData(list);
			req.setCount(1);
			return new NormalReturn("200","success", req);
		} catch (Exception e) {
			logger.error("startFlow error:", e);
			return new NormalReturn("500",e.getMessage(), "error");
		}
	}
	
}
