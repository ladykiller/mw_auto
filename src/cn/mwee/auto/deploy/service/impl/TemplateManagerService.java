/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.deploy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.mwee.auto.deploy.contract.TemplateTaskContract;
import cn.mwee.auto.deploy.dao.AutoTemplateMapper;
import cn.mwee.auto.deploy.dao.TemplateTaskMapper;
import cn.mwee.auto.deploy.model.AutoTemplate;
import cn.mwee.auto.deploy.model.AutoTemplateExample;
import cn.mwee.auto.deploy.model.TemplateTask;
import cn.mwee.auto.deploy.model.TemplateTaskExample;
import cn.mwee.auto.deploy.service.ITemplateManagerService;

/**
 * @author mengfanyuan
 * 2016年7月8日下午5:32:47
 */
@Service
public class TemplateManagerService implements ITemplateManagerService {
	
	@Autowired
	AutoTemplateMapper autoTemplateMapper;
	
	@Autowired
	TemplateTaskMapper templateTaskMapper;
	
	@Override
	public boolean addTask2Template(int templateId, TemplateTask task) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeTemplateTask(int templateId, int templateTaskId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<AutoTemplate> getTemplates() {
		// TODO Auto-generated method stub
		AutoTemplateExample example = new AutoTemplateExample();
		example.setOrderByClause("id desc");
		return autoTemplateMapper.selectByExample(example);
	}
	
	@Override
	public List<TemplateTask> getTempleteTasks(TemplateTaskContract reqModel) {
		TemplateTaskExample example = new TemplateTaskExample();
		example.createCriteria()
			.andTemplateIdEqualTo(reqModel.getTemplateId());
		example.setOrderByClause("`group` ASC,priority ASC");
		example.setLimitStart(reqModel.getLimitStart());
		example.setLimitEnd(reqModel.getLimitEnd());
		return templateTaskMapper.selectByExample(example);
	}

}
