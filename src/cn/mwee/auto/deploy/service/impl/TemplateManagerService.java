/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.deploy.service.impl;

import java.util.Date;
import java.util.List;
import cn.mwee.auto.common.db.BaseModel;
import cn.mwee.auto.common.db.BaseQueryResult;
import cn.mwee.auto.deploy.model.*;
import static cn.mwee.auto.deploy.util.AutoConsts.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.mwee.auto.deploy.contract.template.TemplateTaskContract;
import cn.mwee.auto.deploy.dao.AutoTemplateMapper;
import cn.mwee.auto.deploy.dao.TemplateTaskMapper;
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
	public boolean addTemplate(String templateName)
	{
		AutoTemplate template = new AutoTemplate();
		template.setName(templateName);
		template.setCreateTime(new Date());
		template.setCreator("root");
		return autoTemplateMapper.insertSelective(template) > 0;
	}

	@Override
	public boolean deleteTemplate(int templateId) {
		AutoTemplate template = new AutoTemplate();
		template.setUpdateTime(new Date());
		template.setId(templateId);
		template.setInuse(InUseType.NOT_USE);
		template.setOperater("root-del");
		return autoTemplateMapper.updateByPrimaryKeySelective(template) > 0;
	}

	@Override
	public boolean modifyTemplate(AutoTemplate template)
	{
		template.setUpdateTime(new Date());
		template.setOperater("root-update");
		return autoTemplateMapper.updateByPrimaryKeySelective(template) > 0;
	}

	@Override
	public boolean addTask2Template(int templateId, TemplateTask task)
	{
		task.setTemplateId(templateId);
		task.setCreateTime(new Date());
		return templateTaskMapper.insert(task) > 0;
	}

	@Override
	public boolean removeTemplateTask(int templateTaskId)
	{
		TemplateTask task = new TemplateTask();
		task.setInuse(InUseType.NOT_USE);
		task.setId(templateTaskId);
		return templateTaskMapper.updateByPrimaryKeySelective(task) > 0;
	}

	@Override
	public boolean modifyTemplateTask(TemplateTask task)
	{
		task.setCreateTime(null);
		task.setUpdateTime(new Date());
		return templateTaskMapper.updateByPrimaryKeySelective(task) > 0;
	}

	@Override
	public TemplateTaskContract.QueryTemplatesResult getTemplates(TemplateTaskContract.QueryTemplatesRequest req)
	{
		AutoTemplateExample e = new AutoTemplateExample();
		AutoTemplateExample.Criteria c = e.createCriteria();

		c.andInuseEqualTo(InUseType.IN_USE);
		e.setOrderByClause("id desc");

		TemplateTaskContract.QueryTemplatesResult rs = new TemplateTaskContract.QueryTemplatesResult();
		BaseQueryResult<AutoTask> qrs = BaseModel.selectByPage(autoTemplateMapper, e, req.getPage());

		rs.setList(qrs.getList());
		rs.setPage(qrs.getPage());

		return rs;
	}

	@Override
	public List<TemplateTask> getTempleteTasks(TemplateTaskContract reqModel)
	{
		TemplateTaskExample example = new TemplateTaskExample();
		TemplateTaskExample.Criteria c = example.createCriteria();

		c.andTemplateIdEqualTo(reqModel.getTemplateId());
		c.andInuseEqualTo(InUseType.IN_USE);

		example.setOrderByClause("`group` ASC,priority ASC");
		example.setLimitStart(reqModel.getLimitStart());
		example.setLimitEnd(reqModel.getLimitEnd());
		return templateTaskMapper.selectByExample(example);
	}

}
