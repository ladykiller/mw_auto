/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.deploy.service.impl;

import java.util.*;

import cn.mwee.auto.common.db.BaseModel;
import cn.mwee.auto.common.db.BaseQueryResult;
import cn.mwee.auto.deploy.dao.TemplateZoneMapper;
import cn.mwee.auto.deploy.contract.template.QueryTemplatesRequest;
import cn.mwee.auto.deploy.contract.template.QueryTemplatesResult;
import cn.mwee.auto.deploy.dao.ZoneMapper;
import cn.mwee.auto.deploy.model.*;
import static cn.mwee.auto.deploy.util.AutoConsts.*;

import cn.mwee.auto.deploy.service.ITaskManagerService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private TemplateZoneMapper templateZoneMapper;

    @Autowired
    private ITaskManagerService taskManagerService;

    @Autowired
    private ZoneMapper zoneMapper;

	@Override
	public AutoTemplate getTemplate(int templateId) {
		return autoTemplateMapper.selectByPrimaryKey(templateId);
	}

	@Override
	public boolean addTemplate(AutoTemplate template)
	{
		template.setCreateTime(new Date());
		template.setCreator("root-creator");
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
		task.setCreator("root-creator");
		return templateTaskMapper.insertSelective(task) > 0;
	}

	@Override
	public boolean removeTemplateTask(int templateTaskId)
	{
		TemplateTask task = new TemplateTask();
		task.setInuse(InUseType.NOT_USE);
		task.setId(templateTaskId);
		task.setOperater("root-remove");
		return templateTaskMapper.updateByPrimaryKeySelective(task) > 0;
	}

	@Override
	public boolean modifyTemplateTask(TemplateTask task)
	{
		task.setCreateTime(null);
		task.setUpdateTime(new Date());
		task.setOperater("root-modify");
		return templateTaskMapper.updateByPrimaryKeySelective(task) > 0;
	}

	@Override
	public QueryTemplatesResult getTemplates(QueryTemplatesRequest req)
	{
		AutoTemplateExample e = new AutoTemplateExample();
		AutoTemplateExample.Criteria c = e.createCriteria();

		c.andInuseEqualTo(InUseType.IN_USE);
		e.setOrderByClause("id desc");

		QueryTemplatesResult rs = new QueryTemplatesResult();
		BaseQueryResult<AutoTask> qrs = BaseModel.selectByPage(autoTemplateMapper, e, req.getPage());

		rs.setList(qrs.getList());
		rs.setPage(qrs.getPage());

		return rs;
	}

	@Override
	public List<TemplateTask> getTemplateTasks(int templateId)
	{
		TemplateTaskExample example = new TemplateTaskExample();
		TemplateTaskExample.Criteria c = example.createCriteria();
		c.andTemplateIdEqualTo(templateId);
		c.andInuseEqualTo(InUseType.IN_USE);
		example.setOrderByClause("`group` ASC,priority ASC");
		return templateTaskMapper.selectByExample(example);
	}

    @Override
    public List<Zone> getTemplateZones(Integer templateId) {
        TemplateZoneExample example = new TemplateZoneExample();
        example.createCriteria()
                .andTemplateIdEqualTo(templateId);
        List<TemplateZone> tzs = templateZoneMapper.selectByExample(example);
        List<Integer> zoneIds = new ArrayList<>();
        tzs.forEach(item-> {
            zoneIds.add(item.getZoneId());
        });

        if (CollectionUtils.isEmpty(zoneIds)) return new ArrayList<>();

        ZoneExample zoneExample = new ZoneExample();
        zoneExample.createCriteria()
                .andIdIn(zoneIds);
        return zoneMapper.selectByExample(zoneExample);
    }

    @Override
    public List<AutoTask> getTemplateSimpleTasks(Integer templateId) {
        List<TemplateTask>  list = getTemplateTasks(templateId);
        Set<Integer> taskIdSet = new HashSet<>();
        for (TemplateTask tt : list) {
            taskIdSet.add(tt.getTaskId());
        }
        return taskManagerService.getAutoTasksByIds(taskIdSet);
    }

    @Override
    public List<String> getTemplateTaskParamKeys(Integer templateId) {
        List<AutoTask> tasks = getTemplateSimpleTasks(templateId);
        Set<String> paramKeySet = new HashSet<>();
        for (AutoTask task:tasks) {
            String paramStr = task.getParams();
            if (StringUtils.isEmpty(paramStr)) continue;
            String[] params = paramStr.split(" ");
            for (String param : params) {
                if (StringUtils.isEmpty(param)) continue;
                paramKeySet.add(param.replace("#",""));
            }
        }
        List<String> paramKeys = new ArrayList<>();
        paramKeys.addAll(paramKeySet);
        return paramKeys;
    }
}
