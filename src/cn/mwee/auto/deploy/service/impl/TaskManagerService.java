/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.deploy.service.impl;

import cn.mwee.auto.common.db.BaseModel;
import cn.mwee.auto.common.db.BaseQueryResult;
import cn.mwee.auto.deploy.contract.QueryTasksRequest;
import cn.mwee.auto.deploy.contract.QueryTasksResult;
import cn.mwee.auto.deploy.model.AutoTaskExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.mwee.auto.deploy.dao.AutoTaskMapper;
import cn.mwee.auto.deploy.model.AutoTask;
import cn.mwee.auto.deploy.service.ITaskManagerService;

import java.util.List;

/**
 * @author mengfanyuan
 * 2016年7月6日上午10:16:54
 */
@Service
public class TaskManagerService implements ITaskManagerService {

	@Autowired
	private AutoTaskMapper autoTaskMapper;
	
	
	/* (non-Javadoc)
	 * @see cn.mwee.auto.deploy.service.ITaskManagerService#addTask(cn.mwee.auto.deploy.model.AutoTask)
	 */
	@Override
	public boolean addTask(AutoTask task) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.mwee.auto.deploy.service.ITaskManagerService#getTask(java.lang.Integer)
	 */
	@Override
	public AutoTask getTask(Integer taskId) {
		// TODO Auto-generated method stub
		return autoTaskMapper.selectByPrimaryKey(taskId);
	}

	@Override
	public boolean deleteTask(Integer taskId)
	{

		return false;
	}

	@Override
	public QueryTasksResult getAutoTasks(QueryTasksRequest req)
	{
		AutoTaskExample e = new AutoTaskExample();
		AutoTaskExample.Criteria c = e.createCriteria();

		QueryTasksResult rs = new QueryTasksResult();
		BaseQueryResult<AutoTask> qrs = BaseModel.selectByPage(autoTaskMapper, e, req.getPage());

		rs.setList(qrs.getList());
		rs.setPage(qrs.getPage());

		return rs;
	}

}
