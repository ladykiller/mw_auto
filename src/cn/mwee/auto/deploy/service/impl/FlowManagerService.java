package cn.mwee.auto.deploy.service.impl;

import cn.mwee.auto.deploy.dao.FlowMapper;
import cn.mwee.auto.deploy.dao.FlowTaskExtMapper;
import cn.mwee.auto.deploy.dao.FlowTaskMapper;
import cn.mwee.auto.deploy.dao.TemplateTaskExtMapper;
import cn.mwee.auto.deploy.dao.TemplateTaskMapper;
import cn.mwee.auto.deploy.model.Flow;
import cn.mwee.auto.deploy.model.FlowExample;
import cn.mwee.auto.deploy.model.FlowTask;
import cn.mwee.auto.deploy.model.FlowTaskExample;
import cn.mwee.auto.deploy.model.MwGroupData;
import cn.mwee.auto.deploy.model.TemplateTask;
import cn.mwee.auto.deploy.model.TemplateTaskExample;
import cn.mwee.auto.deploy.service.IFlowManagerService;
import cn.mwee.auto.deploy.service.execute.SimpleTaskExecutor;
import cn.mwee.auto.deploy.service.execute.TaskMsgSender;

import static cn.mwee.auto.deploy.util.AutoConsts.*;

import cn.mwee.auto.deploy.util.AutoUtils;
import cn.mwee.auto.misc.common.exception.ServiceException;
import cn.mwee.auto.misc.mq.MQSender;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huming on 16/6/24.
 */
@Service
public class FlowManagerService implements IFlowManagerService {
	private static final Logger logger = LoggerFactory.getLogger(SimpleTaskExecutor.class);
    @Autowired
    private FlowMapper flowMapper;

    @Autowired
    private FlowTaskExtMapper flowTaskExtMapper;
    
    @Resource
    private FlowTaskMapper flowTaskMapper;
    
    @Autowired
    private TemplateTaskExtMapper templateTaskExtMapper;
    
    @Autowired
    private TemplateTaskMapper templateTaskMapper;

    @Resource
	private TaskMsgSender taskMsgSender;

    @Override
    public int createFlow(Flow flow,Map<String,Object> params )
    {
    	/*
        FlowExample example = new FlowExample();

        FlowExample.Criteria criteria= example.createCriteria();

        criteria.andStateEqualTo("ing");

        List<Flow> list = flowMapper.selectByExample(example);
        */

        flow.setCreateTime(new Date());
        flow.setCreator(SecurityUtils.getSubject().getPrincipal()==null?"system":SecurityUtils.getSubject().getPrincipal().toString());
        flow.setOperater(flow.getCreator());
        flow.setParams(JSON.toJSONString(params));
        flow.setState(TaskState.INIT.name());
        int result = flowMapper.insertSelective(flow);
        return result > 0? flow.getId() : 0;
    }

    @Override
    public boolean executeFlow(int flowId) throws ServiceException
    {
    	if (initFlowTasks(flowId) && startFlow(flowId)) {
			Flow flow = flowMapper.selectByPrimaryKey(flowId);
            flow.setState(TaskState.ING.name());
            flow.setOperater(SecurityUtils.getSubject().getPrincipal()==null?"system":SecurityUtils.getSubject().getPrincipal().toString());
            flow.setUpdateTime(new Date());
            flowMapper.updateByPrimaryKeySelective(flow);
            return true;
    	} else {
    		return false;
    	}
    }

    @Override
    public boolean initFlowTasks(int flowId) {
    	//copy tasks from template_tasks to flow_tasks
    	//get flow
    	Flow flow = flowMapper.selectByPrimaryKey(flowId);
    	if (flow == null ) {
    		throw new NullPointerException("Cant find flow for id:"+flowId);
    	}
    	//get flowTask
    	TemplateTaskExample ttExample = new TemplateTaskExample();
    	ttExample.createCriteria().andTemplateIdEqualTo(flow.getTemplateId());
    	List<TemplateTask> tts = templateTaskMapper.selectByExample(ttExample);
    	//get zones
    	String zoneStr = flow.getZones();
    	String[] zones = zoneStr.split(",");
    	//get params
    	Map<Object, Object> paramsMap = JSON.parseObject(flow.getParams(), Map.class);
    	
    	//copy tasks
    	List<FlowTask> fts = new ArrayList<FlowTask>();
    	for (TemplateTask tt : tts) {
			for (String zone:zones ) {
				if (StringUtils.isBlank(zone)) continue;
				FlowTask ft = new FlowTask();
				ft.setGroup(tt.getGroup());
				ft.setPriority(tt.getPriority());
				ft.setTaskId(tt.getTaskId());
				ft.setTaskType(tt.getTaskType());
				ft.setFlowId(flowId);
				ft.setZone(zone);
				ft.setTaskParam(String.valueOf(paramsMap.get(ft.getTaskId()+"")));
				ft.setState(TaskState.INIT.name());
				ft.setCreateTime(new Date());
				fts.add(ft);
	    	}
		}
    	int result = flowTaskExtMapper.insertBatch(fts);
        return result > 0;
    }

    @Override
    public boolean updateFlowStatus(int flowId) {
    	Flow flow = flowMapper.selectByPrimaryKey(flowId);
    	FlowTaskExample example = new FlowTaskExample();
    	example.createCriteria().andFlowIdEqualTo(flowId);
    	List<Map<String, Object>> list =flowTaskExtMapper.countState(example);
    	String stateNew = calcFlowStatus(list);
    	int result = 1;
    	if (!stateNew.equals(flow.getState())) {
    		Flow flowTmp = new Flow();
    		flowTmp.setId(flow.getId());
    		flowTmp.setState(stateNew);
    		flowTmp.setUpdateTime(new Date());
    		result = flowMapper.updateByPrimaryKeySelective(flowTmp);
    	}
        return result > 0;
    }

    @Override
    public FlowTask getCurrentGroupNextTask(int flowTaskId) {
    	FlowTask flowTask = flowTaskMapper.selectByPrimaryKey(flowTaskId);
    	FlowTaskExample example = new FlowTaskExample();
    	example.createCriteria().andFlowIdEqualTo(flowTask.getFlowId())
    				.andZoneEqualTo(flowTask.getZone())
		    		.andGroupEqualTo(flowTask.getGroup())
		    		.andPriorityGreaterThan(flowTask.getPriority());
        return flowTaskExtMapper.getCurrentGroupNextTask(example);
    }

    @Override
    public boolean checkConcurrentGroupsFinished(int flowTaskId) {
    	FlowTask flowTask = flowTaskMapper.selectByPrimaryKey(flowTaskId);
    	FlowTaskExample example = new FlowTaskExample();
    	cn.mwee.auto.deploy.model.FlowTaskExample.Criteria criteria = example.createCriteria();
    	criteria.andFlowIdEqualTo(flowTask.getFlowId())
    		.andZoneEqualTo(flowTask.getZone())		//区域
    		.andStateNotEqualTo(TaskState.SUCCESS.name());		//不成功
    	if (flowTask.getGroup() == GroupType.PreGroup) {
    		criteria.andGroupEqualTo(GroupType.PreGroup);	//前置group
    	} else if (flowTask.getGroup() == GroupType.PostGroup) {
    		criteria.andGroupEqualTo(GroupType.PostGroup);	//后置group
    	} else {
    		criteria.andGroupNotEqualTo(GroupType.PreGroup)		//非前置group
    			.andGroupNotEqualTo(GroupType.PostGroup);		//非后置group
    	}
    	int count = flowTaskExtMapper.countUnFinishedTasks(example);
        return !(count > 0);
    }

    @Override
	public boolean updateTaskStatus(int flowTaskId,String state) {
    	FlowTask flowTask = new FlowTask();
    	flowTask.setId(flowTaskId);
    	flowTask.setState(state);
    	flowTask.setUpdateTime(new Date());
    	int result = flowTaskMapper.updateByPrimaryKeySelective(flowTask);
		return result > 0;
	}
    
    @Override
    public List<FlowTask> getNextGroupStartFlowTasks(Integer flowTasId ) {
    	List<FlowTask> list = null;
    	//获取当前任务
    	FlowTask currentFlowTask = flowTaskMapper.selectByPrimaryKey(flowTasId);
    	//当前组
    	byte currentGroup = currentFlowTask.getGroup().byteValue();
    	//当前组为后置组 返回null
    	if (currentGroup == GroupType.PostGroup.byteValue() ) {
    		return null ;
    	}
    	FlowTaskExample example = new FlowTaskExample();
    	//当前组为前置组 
    	if (currentGroup == GroupType.PreGroup.byteValue()) {
    		//获取并发组开始任务
    		example.createCriteria()
	    		.andFlowIdEqualTo(currentFlowTask.getFlowId())
	    		.andGroupNotEqualTo(GroupType.PreGroup)
	    		.andGroupNotEqualTo(GroupType.PostGroup)
	    		.andZoneEqualTo(currentFlowTask.getZone());
    		list = flowTaskExtMapper.getConcurrentGroupStartTasks(example);
    		if (CollectionUtils.isNotEmpty(list)) {
    			return list;
    		}
    		//获取后置组开始任务
    		example.clear();
    		example.createCriteria()
    			.andFlowIdEqualTo(currentFlowTask.getFlowId())
    			.andGroupEqualTo(GroupType.PostGroup)
    			.andZoneEqualTo(currentFlowTask.getZone());
    		example.setOrderByClause("");
    		example.setLimitStart(0);
    		example.setLimitEnd(1);
    		list = flowTaskMapper.selectByExample(example);
    		return list;
    	}
    	//当前组为并发
    	if (currentGroup > GroupType.PreGroup.byteValue() 
    			&& currentGroup < GroupType.PostGroup.byteValue()) {
    		example.clear();
    		example.createCriteria()
    			.andFlowIdEqualTo(currentFlowTask.getFlowId())
    			.andGroupEqualTo(GroupType.PostGroup)
    			.andZoneEqualTo(currentFlowTask.getZone());
    		example.setOrderByClause("priority");
    		example.setLimitStart(0);
    		example.setLimitEnd(1);
    		list = flowTaskMapper.selectByExample(example);
    		return list;
    	}
    	return list;
    }
    
    @Override
    public List<FlowTask> getStartFlowTask(int flowId){
    	List<FlowTask> list = null;
    	//获取前置组初始任务
    	FlowTaskExample example = new FlowTaskExample();
    	example.createCriteria()
    		.andFlowIdEqualTo(flowId)
    		.andGroupEqualTo(GroupType.PreGroup);
    	list = flowTaskExtMapper.getPreOrPostGroupStartTasks(example);
    	if (CollectionUtils.isNotEmpty(list)) {
    		return list;
    	}
    	//获取并发组
    	Flow flow = flowMapper.selectByPrimaryKey(flowId);
    	String[] zones = flow.getZones().split(",");
    	for (String zone : zones) {
    		example.clear();
    		example.createCriteria().andFlowIdEqualTo(flowId).andZoneEqualTo(zone);
    		List<FlowTask> tmpList = flowTaskExtMapper.getConcurrentGroupStartTasks(example);
    		if (CollectionUtils.isNotEmpty(tmpList)) {
    			list.addAll(tmpList);
    		}
    	}
    	if (CollectionUtils.isNotEmpty(list)) {
    		return list;
    	}
    	//获取后置组
    	example.clear();
    	example.createCriteria()
			.andFlowIdEqualTo(flowId)
			.andGroupEqualTo(GroupType.PostGroup);
    	list = flowTaskExtMapper.getPreOrPostGroupStartTasks(example);
    	return list;
    }
    
    private boolean startFlow (int flowId) {
    	try {
			List<FlowTask> flowTasks = getStartFlowTask(flowId);
			if (CollectionUtils.isEmpty(flowTasks)) {
				logger.error("StartFlow Error: There task for flow[id={}]", flowId);
				return false;
			}
			taskMsgSender.sendTasks(flowTasks);
			return true;
		} catch (Exception e) {
			logger.error("StartFlow Error:", e);
			return false;
		}
    }
    
    
    private boolean testInt(Integer value){
    	return (value != null && value>0);
    }
    
    private MwGroupData getMwGroupData(int templateId)
    {
        List<Byte> groups = templateTaskExtMapper.distinctGroups(templateId);
        return AutoUtils.mwGroupData(groups);
    }

	@Override
	public Map<String, Object> getZoneFlowTaskInfo(Integer flowId, String zone) {
		Map<String , Object> returnMap = new HashMap<String,Object>(); 
		//区域流程状态
		FlowTaskExample example = new FlowTaskExample();
    	example.createCriteria()
    		.andFlowIdEqualTo(flowId)
    		.andZoneEqualTo(zone);
    	List<Map<String, Object>> list =flowTaskExtMapper.countState(example);
		String flowState = calcFlowStatus(list);
		returnMap.put("state", flowState);
		//流程任务信息
    	example.clear();
		example.createCriteria()
			.andFlowIdEqualTo(flowId)
			.andZoneEqualTo(zone);
		example.setOrderByClause("`group` ASC,priority ASC");
		List<FlowTask> flowTasks =  flowTaskMapper.selectByExample(example);
		List<FlowTask> preGroupFlowTasks =  new ArrayList<>();
		List<FlowTask> concurrentGroupFlowTasks =  new ArrayList<>();
		List<FlowTask> postGroupFlowTasks =  new ArrayList<>();
		for (FlowTask flowTask : flowTasks) {
			if (GroupType.PreGroup.byteValue() == flowTask.getGroup()) {
				preGroupFlowTasks.add(flowTask);
			} else if (GroupType.PostGroup.byteValue() == flowTask.getGroup()) {
				postGroupFlowTasks.add(flowTask);
			} else {
				concurrentGroupFlowTasks.add(flowTask);
			}
		}
		returnMap.put("pre",preGroupFlowTasks );
		returnMap.put("concurrent", concurrentGroupFlowTasks);
		returnMap.put("post", postGroupFlowTasks);
		return returnMap;
	}
	
	@Override
	public List<FlowTask> getZoneFlowTaskInfoSimple(Integer flowId, String zone) {
		FlowTaskExample example = new FlowTaskExample();
		example.createCriteria()
			.andFlowIdEqualTo(flowId)
			.andZoneEqualTo(zone);
		example.setOrderByClause("`group` ASC,priority ASC");
		return flowTaskMapper.selectByExample(example);
	}
	
	@Override
	public Map<String, String> getZonesState (Integer flowId) {
		Map<String , String> returnMap = new HashMap<String,String>(); 
		Flow flow = flowMapper.selectByPrimaryKey(flowId);
		String[] zones = flow.getZones().split(",");
		FlowTaskExample example = new FlowTaskExample();
		for (String zone : zones) {
			if (StringUtils.isBlank(zone)) continue;
			example.clear();
	    	example.createCriteria()
	    		.andFlowIdEqualTo(flowId)
	    		.andZoneEqualTo(zone);
	    	List<Map<String, Object>> list =flowTaskExtMapper.countState(example);
			String flowState = calcFlowStatus(list);
			returnMap.put(zone, flowState);
		}
		return returnMap;
	}
	
	public void executeFlowTask(Integer flowTaskId) {
			FlowTask flowTask = flowTaskMapper.selectByPrimaryKey(flowTaskId);
			taskMsgSender.sendTask(flowTask);
	}
	
	
	private String calcFlowStatus (List<Map<String, Object>> list) {
		Map<String, Integer> mapTmp = new HashMap<String, Integer>();
    	for (Map<String, Object> o:list) {
    		String state = o.get("state").toString();
    		int count = Integer.parseInt(o.get("count").toString()) ;
    		if (count > 0) {
    			mapTmp.put(state, count);
    		}
    	}
    	String stateNew = TaskState.INIT.name();
    	//成功
    	if (testInt(mapTmp.get(TaskState.SUCCESS.name()))) {
    		stateNew = TaskState.SUCCESS.name();
    	}
    	//初始化
    	if (testInt(mapTmp.get(TaskState.INIT.name()))) {
    		stateNew = TaskState.INIT.name();
    	}
    	if (testInt(mapTmp.get(TaskState.INIT.name())) && testInt(mapTmp.get(TaskState.SUCCESS.name()))) {
    		stateNew = TaskState.ING.name();
    	}
    	//运行中
    	if (testInt(mapTmp.get(TaskState.ING.name()))) {
    		stateNew = TaskState.ING.name();
    	}
    	//定时
    	if (testInt(mapTmp.get(TaskState.TIMER.name()))) {
    		stateNew = TaskState.TIMER.name();
    	}
    	//手动
    	if (testInt(mapTmp.get(TaskState.MANUAL.name()))) {
    		stateNew = TaskState.MANUAL.name();
    	}
    	//失败
    	if (testInt(mapTmp.get(TaskState.ERROR.name()))) {
    		stateNew = TaskState.ERROR.name();
    	}
		return stateNew;
	}

	@Override
	public List<Flow> getFlows() {
		FlowExample example = new FlowExample();
		example.setOrderByClause("id desc");
		example.setLimitStart(0);
		example.setLimitEnd(20);
		return flowMapper.selectByExample(example);
	}

}
