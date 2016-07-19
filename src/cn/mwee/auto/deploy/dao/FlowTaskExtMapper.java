package cn.mwee.auto.deploy.dao;

import java.util.List;
import java.util.Map;

import cn.mwee.auto.deploy.model.FlowTask;
import cn.mwee.auto.deploy.model.FlowTaskExample;

public interface FlowTaskExtMapper {
    
	FlowTask getCurrentGroupNextTask(FlowTaskExample example);
	
	int countUnFinishedTasks(FlowTaskExample example);
	
	List<Map<String, Object>> countState(FlowTaskExample example);
	
	int insertBatch(List<FlowTask> list);
	
	List<FlowTask> getPreOrPostGroupStartTasks(FlowTaskExample example);
	
	List<FlowTask> getConcurrentGroupStartTasks(FlowTaskExample example);
}