package cn.mwee.auto.deploy.service;

import cn.mwee.auto.deploy.model.Flow;
import cn.mwee.auto.deploy.model.FlowTask;

import java.util.List;
import java.util.Map;

/**
 * Created by huming on 16/6/24.
 */
public interface IFlowManagerService {

    /**
     *
     * createFlow -> flow 新建状态 -> 执行流程 (1.初始化流程任务,2.执行任务)-> 任务流处理listener
     *
     * @param templateId
     * @param zones
     * @param params
     * @return
     */
    int createFlow(Flow flow,Map<String,Object> params);

    /**
     * 执行任务流
     * 
     * @param flowId
     * @param operater
     * @return
     */
    boolean executeFlow(int flowId);

    /**
     * 初始化流程任务
     * @param flowId
     * @return
     */
    boolean initFlowTasks(int flowId);

    /**
     * 计算流程状态
     *
     * 1.需更新flow表 flow.status
     *
     * @return
     */
    boolean updateFlowStatus(int flowId);


    /**
     * 查找当前组下一个任务
     * @param flowTaskId
     * @return
     */
    FlowTask getCurrentGroupNextTask(int flowTaskId);


    /**
     * 查找并发组是否完成
     * @return true if finished else false
     */
    boolean checkConcurrentGroupsFinished(int flowTaskId);

    /**
     * 更新流程任务状态
     * @return
     */
    boolean updateTaskStatus(int flowTaskId,String state);

    /**
     * 查找下个组的开始任务
     * 
     * @param flowTasId
     * @return
     */
    List<FlowTask> getNextGroupStartFlowTasks(Integer flowTasId);
    
	/**
	 * 查找流程开始任务
	 * 
	 * @param flowId
	 * @return
	 */
	List<FlowTask> getStartFlowTask(int flowId);
	
	/**
	 * 获取区域流程任务
	 * @param flowId
	 * @param zone
	 * @return
	 */
	Map<String, Object> getZoneFlowTaskInfo (Integer flowId, String zone);
	
	/**
	 * 获取区域流程任务
	 * @param flowId
	 * @param zone
	 * @return
	 */
	List<FlowTask> getZoneFlowTaskInfoSimple(Integer flowId, String zone);
	
	/**
	 * 获取各区域状态
	 * @param flowId
	 * @return
	 */
	Map<String, String> getZonesState (Integer flowId);
	
	/**
	 * 手动执行任务
	 * @param flowTaskId
	 * @return
	 */
	void executeFlowTask(Integer flowTaskId);
	
	/**
	 * 获取流程列表
	 * @return
	 */
	List<Flow> getFlows();

	

	
}
