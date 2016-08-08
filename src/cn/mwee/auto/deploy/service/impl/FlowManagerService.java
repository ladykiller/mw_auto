package cn.mwee.auto.deploy.service.impl;

import cn.mwee.auto.auth.util.AuthUtils;
import cn.mwee.auto.common.db.BaseModel;
import cn.mwee.auto.common.db.BaseQueryResult;
import cn.mwee.auto.deploy.contract.flow.FlowAddContract;
import cn.mwee.auto.deploy.contract.flow.FlowQueryContract;
import cn.mwee.auto.deploy.dao.*;
import cn.mwee.auto.deploy.model.*;
import cn.mwee.auto.deploy.service.IFlowManagerService;
import cn.mwee.auto.deploy.service.ITemplateManagerService;
import cn.mwee.auto.deploy.service.execute.SimpleTaskExecutor;
import cn.mwee.auto.deploy.service.execute.TaskMsgSender;

import static cn.mwee.auto.deploy.util.AutoConsts.*;

import cn.mwee.auto.deploy.util.AutoUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import javax.annotation.Resource;

import java.util.*;

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

    @Autowired
    private AutoTaskMapper autoTaskMapper;

    @Autowired
    private FlowStrategyMapper flowStrategyMapper;

    @Resource
    private ITemplateManagerService templateManagerService;

    @Resource
    private TaskMsgSender taskMsgSender;

    @Value(value = "${prepare.update.shell}")
    private String updateShell;

    @Value(value = "${prepare.build.shell}")
    private String buildShell;

    @Value(value = "${prepare.deploy.shell}")
    private String deployShell;

    @Value("${localhost.name}")
    private String localHost = "127.0.0.1";


    @Override
    public Integer createFlow(FlowAddContract req) {
        Flow flow = createFlowSimple(req);
        Map<String, Object> params = req.getParams() == null ? new HashMap<>() : req.getParams();
        AutoTemplate template = templateManagerService.getTemplate(flow.getTemplateId());
        if (template == null) {
            throw new NullPointerException("未找到相应模板");
        }
        flow.setIsreview(template.getReview() == 1 ? FlowReviewType.Unreviewed : FlowReviewType.Ignore);
        flow.setCreateTime(new Date());
        flow.setCreator(SecurityUtils.getSubject().getPrincipal() == null ? "system" : SecurityUtils.getSubject().getPrincipal().toString());
        flow.setOperater(flow.getCreator());
        flow.setParams(JSON.toJSONString(params));
        flow.setState(TaskState.INIT.name());
        int result = flowMapper.insertSelective(flow);
        if (result > 0 && req.getStrategyZoneSize() != null
                && req.getStrategyInterval() != null) {
            FlowStrategy flowStrategy = new FlowStrategy();
            flowStrategy.setFlowId(flow.getId());
            flowStrategy.setCreatetime(new Date());
            flowStrategy.setInterval(req.getStrategyInterval());
            flowStrategy.setZonesize(req.getStrategyZoneSize());
            flowStrategyMapper.insertSelective(flowStrategy);
        }
        return result > 0 ? flow.getId() : 0;
    }

    private Flow createFlowSimple(FlowAddContract req) {
        Flow flow = new Flow();
        flow.setName(req.getName());
        flow.setTemplateId(req.getTemplateId());
        flow.setProjectId(req.getProjectId());
        flow.setZones(req.getZones());
        flow.setVcsBranch(req.getVcsBranch());
        flow.setNeedbuild(req.getNeedBuild());
        return flow;
    }

    @Override
    public boolean executeFlow(int flowId) throws Exception {
        Flow flow = flowMapper.selectByPrimaryKey(flowId);
        //判断审核状态
        if (FlowReviewType.Unreviewed.equals(flow.getIsreview()) ||
                FlowReviewType.Unapproved.equals(flow.getIsreview())) {
            throw new Exception("未经批准的流程");
        }
        if (initFlowTasks(flowId) && startFlow(flowId)) {
            flow.setState(TaskState.ING.name());
            flow.setOperater(SecurityUtils.getSubject().getPrincipal() == null ? "system" : SecurityUtils.getSubject().getPrincipal().toString());
            flow.setUpdateTime(new Date());
            flowMapper.updateByPrimaryKeySelective(flow);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean initFlowTasks(int flowId) {
        /*
         * copy tasks from template_tasks to flow_tasks
         */

        //get flow
        Flow flow = flowMapper.selectByPrimaryKey(flowId);
        if (flow == null) {
            throw new NullPointerException("Cant find flow for id:" + flowId);
        }
        FlowStrategy flowStrategy = getFlowStrategy4Flow(flowId);
        //get template
        AutoTemplate template = templateManagerService.getTemplate(flow.getTemplateId());
        if (template == null) {
            throw new NullPointerException("Cant find template for id:" + flow.getTemplateId());
        }

        //获取模板任务
        List<TemplateTask> tts = templateManagerService.getTemplateTasks(flow.getTemplateId());

        //获取区域
        String zoneStr = StringUtils.isBlank(flow.getZones()) ? "" : flow.getZones();
        String[] zones = zoneStr.split(",");
        //用户定义变量
        Map<String, String> userParamsMap = new HashMap<>();
        String paramStr = flow.getParams();
        if (StringUtils.isNotBlank(paramStr)) {
            userParamsMap = JSON.parseObject(flow.getParams(), Map.class);
        }
        //流程变量
        Map<String, String> flowParamMap = initFlowParams(template, flow);
        //复制任务
        List<FlowTask> fts = new ArrayList<>();
        Map<String, FlowTask> zoneStartTaskMap = new HashMap<>();
        for (TemplateTask tt : tts) {
            //prepareGroup
            if (tt.getGroup().equals(GroupType.PrepareGroup)) {
                fts.add(buildFlowTask(tt, flowId, localHost, flowParamMap, userParamsMap));
                continue;
            }
            //区域组
            for (int i = 0; i < zones.length; i++) {
                if (StringUtils.isBlank(zones[i])) continue;
                FlowTask flowTask = buildFlowTask(tt, flowId, zones[i], flowParamMap, userParamsMap);
                if (flowStrategy != null) {
                    calStrategy(flowTask,i,zoneStartTaskMap,flowStrategy);
                }
                fts.add(flowTask);
            }
            /*
            for (String zone : zones) {
                if (StringUtils.isBlank(zone)) continue;
                fts.add(buildFlowTask(tt, flowId, zone, flowParamMap, userParamsMap));
            }
            */
        }
        int result = flowTaskExtMapper.insertBatch(fts);
        return result > 0;
    }

    private void calStrategy(FlowTask flowTask, int index, Map<String, FlowTask> zoneStartTaskMap, FlowStrategy flowStrategy) {
        String key = flowTask.getZone();
        if (zoneStartTaskMap.get(key)!= null) return;
        if (flowTask.getGroup() > GroupType.PreGroup
                && flowTask.getGroup()<GroupType.PostGroup){
            key +=flowTask.getGroup();
            if (zoneStartTaskMap.get(key)!= null) return;
        }
        zoneStartTaskMap.put(key,flowTask);
        flowTask.setDelay(((index)/flowStrategy.getZonesize()) * flowStrategy.getInterval());
    }

    /**
     * 构建flowTask
     *
     * @param tt
     * @param flowId
     * @param zone
     * @param flowParamMap
     * @param userParamsMap
     * @return
     */
    private FlowTask buildFlowTask(TemplateTask tt, Integer flowId, String zone,
                                   Map<String, String> flowParamMap, Map<String, String> userParamsMap) {
        FlowTask ft = new FlowTask();
        ft.setGroup(tt.getGroup());
        ft.setPriority(tt.getPriority());
        ft.setTaskId(tt.getTaskId());
        ft.setTaskType(tt.getTaskType());
        ft.setFlowId(flowId);
        ft.setZone(zone);
        ft.setDelay(0);
        flowParamMap.put("%zone%", zone);
        replaceFlowParams(ft, flowParamMap);
        replaceUserParams(ft, userParamsMap);
        ft.setState(TaskState.INIT.name());
        ft.setCreateTime(new Date());
        return ft;
    }

    /**
     * 参数替换
     *
     * @param ft       flowTask
     * @param paramMap paramMap
     */
    private void replaceUserParams(FlowTask ft, Map<String, String> paramMap) {

        AutoTask task = autoTaskMapper.selectByPrimaryKey(ft.getTaskId());
        if (task != null && StringUtils.isNotBlank(task.getParams())) {
            String paramStr = task.getParams();
            if (paramStr.indexOf('#') < 0) return;
            Set<String> keySet = paramMap.keySet();
            for (String key : keySet) {
                String paramKey = "#" + key + "#";
                paramStr = paramStr.replaceAll(paramKey, paramMap.get(key));
            }
            ft.setTaskParam(paramStr);
        }
    }

    /**
     * 构建prepare组的任务
     *
     * @param flow
     * @param template
     * @return
     */
    private List<FlowTask> buildPrepareTasks(Flow flow, AutoTemplate template) {
        List<FlowTask> list = new ArrayList<>();
        for (short i = 1; i < 3; i++) {
            if (i == 2 && flow.getNeedbuild() == 0) continue;
            FlowTask ft = new FlowTask();
            ft.setGroup(GroupType.PrepareGroup);
            ft.setPriority(i);
            ft.setTaskId(0);
            ft.setTaskType(TaskType.AUTO.name());
            ft.setFlowId(flow.getId());
            ft.setZone(localHost);
            switch (i) {
                case 1:
                    ft.setTaskParam(updateShell);
                    setPullShell(template, flow, ft);
                    break;
                case 2:
                    ft.setTaskParam(buildShell);
                    setBuildShell(template, ft);
                    break;
                /*case 3:
                    ft.setTaskParam(deployShell);
                    setDeployShell(template,flow,ft);
                    break;*/
            }
            ft.setState(TaskState.INIT.name());
            ft.setCreateTime(new Date());
            list.add(ft);
        }
        return list;
    }

    /**
     * 代码更新脚本
     *
     * @param template
     * @param flow
     * @param ft
     */
    private void setPullShell(AutoTemplate template, Flow flow, FlowTask ft) {
        String repUrl = template.getVcsRep();
        if (StringUtils.isNotBlank(repUrl)
                && StringUtils.isNotBlank(flow.getVcsBranch())) {
            String vcsType = template.getVcsType();
            String vcsRep = template.getVcsRep();
            String vcsBranch = flow.getVcsBranch();
            String projectName = repUrl.substring(repUrl.lastIndexOf('/') + 1, repUrl.lastIndexOf('.'));
            ft.setTaskParam(String.format(updateShell, vcsType, vcsRep, vcsBranch, projectName));
        }
    }

    /**
     * 构建脚本
     *
     * @param template
     * @param ft
     */
    private void setBuildShell(AutoTemplate template, FlowTask ft) {
        String repUrl = template.getVcsRep();
        if (StringUtils.isNotBlank(repUrl)) {
            String projectName = repUrl.substring(repUrl.lastIndexOf('/') + 1, repUrl.lastIndexOf('.'));
            ft.setTaskParam(String.format(buildShell, projectName));
        }
    }

    /**
     * 部署脚本
     *
     * @param template
     * @param flow
     * @param ft
     */
    private void setDeployShell(AutoTemplate template, Flow flow, FlowTask ft) {
        String repUrl = template.getVcsRep();
        if (StringUtils.isNotBlank(repUrl)
                && StringUtils.isNotBlank(flow.getVcsBranch())) {
            String projectName = repUrl.substring(repUrl.lastIndexOf('/') + 1, repUrl.lastIndexOf('.'));
            String zones = flow.getZones();
            ft.setTaskParam(String.format(deployShell, projectName, projectName, zones));
        }
    }

    private Map<String, String> initFlowParams(AutoTemplate template, Flow flow) {
        Map<String, String> flowParamMap = new HashMap<>();
        String repUrl = template.getVcsRep();
        if (StringUtils.isNotBlank(repUrl)
                && StringUtils.isNotBlank(flow.getVcsBranch())) {
            flowParamMap.put("%vcsType%", template.getVcsType());
            flowParamMap.put("%vcsRep%", template.getVcsRep());
            flowParamMap.put("%vcsBranch%", flow.getVcsBranch());
            flowParamMap.put("%projectName%", repUrl.substring(repUrl.lastIndexOf('/') + 1, repUrl.lastIndexOf('.')));
        }
        return flowParamMap;
    }

    private void replaceFlowParams(FlowTask ft, Map<String, String> flowParamMap) {
        AutoTask task = autoTaskMapper.selectByPrimaryKey(ft.getTaskId());
        if (task != null && StringUtils.isNotBlank(task.getParams())) {
            String paramStr = task.getParams();
            if (paramStr.indexOf('%') < 0) return;
            Set<String> keySet = flowParamMap.keySet();
            for (String key : keySet) {
                paramStr = paramStr.replaceAll(key, flowParamMap.get(key));
            }
            ft.setTaskParam(paramStr);
        }
    }


    @Override
    public boolean updateFlowStatus(int flowId) {
        Flow flow = flowMapper.selectByPrimaryKey(flowId);
        FlowTaskExample example = new FlowTaskExample();
        example.createCriteria().andFlowIdEqualTo(flowId);
        List<Map<String, Object>> list = flowTaskExtMapper.countState(example);
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
                .andZoneEqualTo(flowTask.getZone())        //区域
                .andStateNotEqualTo(TaskState.SUCCESS.name());        //不成功

        if (flowTask.getGroup() > GroupType.PreGroup
                && flowTask.getGroup() < GroupType.PostGroup) {
            criteria.andGroupBetween((byte) (GroupType.PreGroup + 1), (byte) (GroupType.PostGroup - 1));
        } else {
            criteria.andGroupEqualTo(flowTask.getGroup()); //准备组
        }
        int count = flowTaskExtMapper.countUnFinishedTasks(example);
        return !(count > 0);
    }

    @Override
    public boolean updateTaskStatusWithouTime(int flowTaskId, String state) {
        FlowTask flowTask = new FlowTask();
        flowTask.setId(flowTaskId);
        flowTask.setState(state);
        int result = flowTaskMapper.updateByPrimaryKeySelective(flowTask);
        return result > 0;
    }

    @Override
    public boolean updateTaskStatus(int flowTaskId, String state) {
        FlowTask flowTask = new FlowTask();
        flowTask.setId(flowTaskId);
        flowTask.setState(state);
        flowTask.setUpdateTime(new Date());
        return flowTaskMapper.updateByPrimaryKeySelective(flowTask) > 0;
    }

    @Override
    public List<FlowTask> getNextGroupStartFlowTasks(Integer flowTasId) {
        List<FlowTask> list = null;
        //获取当前任务
        FlowTask currentFlowTask = flowTaskMapper.selectByPrimaryKey(flowTasId);
        //当前组
        byte currentGroup = currentFlowTask.getGroup();
        //当前组为后置组 返回null
        if (currentGroup == GroupType.PostGroup) {
            return null;
        }
        //准备组
        if (currentGroup == GroupType.PrepareGroup) {
            list = getZoneStartFlowTask(currentFlowTask.getFlowId());
            if (CollectionUtils.isNotEmpty(list)) {
                return list;
            }
        }

        FlowTaskExample example = new FlowTaskExample();
        //当前组为前置组
        if (currentGroup == GroupType.PreGroup) {
            //获取并发组开始任务
            example.createCriteria()
                    .andFlowIdEqualTo(currentFlowTask.getFlowId())
                    .andGroupBetween((byte) (GroupType.PreGroup + 1), (byte) (GroupType.PostGroup - 1))
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
        if (currentGroup > GroupType.PreGroup
                && currentGroup < GroupType.PostGroup) {
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
    public List<FlowTask> getStartFlowTask(int flowId) {
        List<FlowTask> list;
        FlowTaskExample example = new FlowTaskExample();
        //获取准备组初始任务
        example.clear();
        example.createCriteria()
                .andFlowIdEqualTo(flowId)
                .andGroupEqualTo(GroupType.PrepareGroup);
        list = flowTaskExtMapper.getPreOrPostGroupStartTasks(example);
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        return getZoneStartFlowTask(flowId);
    }

    public List<FlowTask> getZoneStartFlowTask(int flowId) {

        Flow flow = flowMapper.selectByPrimaryKey(flowId);
        String[] zones = flow.getZones().split(",");

        List<FlowTask> list = null;
        FlowTaskExample example = new FlowTaskExample();
        //获取前置组初始任务
        example.clear();
        example.createCriteria()
                .andFlowIdEqualTo(flowId)
                .andGroupEqualTo(GroupType.PreGroup);
        list = flowTaskExtMapper.getPreOrPostGroupStartTasks(example);
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        //获取并发组
        for (String zone : zones) {
            example.clear();
            example.createCriteria()
                    .andFlowIdEqualTo(flowId)
                    .andZoneEqualTo(zone)
                    .andGroupBetween((byte) (GroupType.PreGroup + 1), (byte) (GroupType.PostGroup - 1));
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

    public FlowStrategy getFlowStrategy4Flow(Integer flowId) {
        FlowStrategyExample example = new FlowStrategyExample();
        example.createCriteria().andFlowIdEqualTo(flowId);
        List<FlowStrategy> list = flowStrategyMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    private boolean startFlow(int flowId) {
        try {
            List<FlowTask> flowTasks = getStartFlowTask(flowId);
            if (CollectionUtils.isEmpty(flowTasks)) {
                logger.error("StartFlow Error: none tasks for flow[id={}]", flowId);
                return false;
            }
            taskMsgSender.sendTasks(flowTasks);
            return true;
        } catch (Exception e) {
            logger.error("StartFlow Error:", e);
            return false;
        }
    }


    private boolean testInt(Integer value) {
        return (value != null && value > 0);
    }

    private MwGroupData getMwGroupData(int templateId) {
        List<Byte> groups = templateTaskExtMapper.distinctGroups(templateId);
        return AutoUtils.mwGroupData(groups);
    }

    @Override
    public Map<String, Object> getZoneFlowTaskInfo(Integer flowId, String zone) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        //区域流程状态
        FlowTaskExample example = new FlowTaskExample();
        example.createCriteria()
                .andFlowIdEqualTo(flowId)
                .andZoneEqualTo(zone);
        List<Map<String, Object>> list = flowTaskExtMapper.countState(example);
        String flowState = calcFlowStatus(list);
        returnMap.put("state", flowState);
        //流程任务信息
        example.clear();
        example.createCriteria()
                .andFlowIdEqualTo(flowId)
                .andZoneEqualTo(zone);
        example.setOrderByClause("`group` ASC,priority ASC");
        List<FlowTask> flowTasks = flowTaskMapper.selectByExample(example);
        List<FlowTask> preGroupFlowTasks = new ArrayList<>();
        List<FlowTask> concurrentGroupFlowTasks = new ArrayList<>();
        List<FlowTask> postGroupFlowTasks = new ArrayList<>();
        for (FlowTask flowTask : flowTasks) {
            if (GroupType.PreGroup.byteValue() == flowTask.getGroup()) {
                preGroupFlowTasks.add(flowTask);
            } else if (GroupType.PostGroup.byteValue() == flowTask.getGroup()) {
                postGroupFlowTasks.add(flowTask);
            } else {
                concurrentGroupFlowTasks.add(flowTask);
            }
        }
        returnMap.put("pre", preGroupFlowTasks);
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
    public Map<String, String> getZonesState(Integer flowId) {
        Map<String, String> returnMap = new HashMap<String, String>();
        Flow flow = flowMapper.selectByPrimaryKey(flowId);
        String[] zones = flow.getZones().split(",");
        FlowTaskExample example = new FlowTaskExample();
        for (String zone : zones) {
            if (StringUtils.isBlank(zone)) continue;
            example.clear();
            example.createCriteria()
                    .andFlowIdEqualTo(flowId)
                    .andZoneEqualTo(zone);
            List<Map<String, Object>> list = flowTaskExtMapper.countState(example);
            if (CollectionUtils.isEmpty(list)) continue;
            String flowState = calcFlowStatus(list);
            returnMap.put(zone, flowState);
        }
        String localZoneState = getZoneState(flowId, localHost);
        if (StringUtils.isNotBlank(localZoneState)) returnMap.put(localHost, localZoneState);
        return returnMap;
    }

    @Override
    public String getZoneState(Integer flowId, String zone) {
        FlowTaskExample example = new FlowTaskExample();
        example.createCriteria()
                .andFlowIdEqualTo(flowId)
                .andZoneEqualTo(zone);
        List<Map<String, Object>> list = flowTaskExtMapper.countState(example);
        if (CollectionUtils.isEmpty(list)) return null;
        return calcFlowStatus(list);
    }

    public void executeFlowTask(Integer flowTaskId) {
        FlowTask flowTask = flowTaskMapper.selectByPrimaryKey(flowTaskId);
        taskMsgSender.sendTask(flowTask);
    }


    private String calcFlowStatus(List<Map<String, Object>> list) {
        Map<String, Integer> mapTmp = new HashMap<String, Integer>();
        for (Map<String, Object> o : list) {
            String state = o.get("state").toString();
            int count = Integer.parseInt(o.get("count").toString());
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
    public BaseQueryResult<Flow> getFlows(FlowQueryContract req, Flow flow) {
        FlowExample example = new FlowExample();
        FlowExample.Criteria criteria = example.createCriteria();
        criteria.andProjectIdEqualTo(req.getProjectId());

        example.setOrderByClause("id DESC");

        if (req.getCreateDateS() != null) criteria.andCreateTimeGreaterThanOrEqualTo(req.getCreateDateS());
        if (req.getCreateDateE() != null) criteria.andCreateTimeLessThanOrEqualTo(req.getCreateDateE());
        if (req.getFlowId() != null) criteria.andIdEqualTo(req.getFlowId());
        if (StringUtils.isNotBlank(req.getZone())) criteria.andZonesLike(req.getZone());
        if (CollectionUtils.isNotEmpty(req.getState())) criteria.andStateIn(req.getState());
        return BaseModel.selectByPage(flowMapper, example, req.getPageInfo(), req.getPageInfo() == null);
    }

    @Override
    public Flow getFlow(Integer flowId) {
        return flowMapper.selectByPrimaryKey(flowId);
    }

    @Override
    public boolean reviewFlow(Integer flowId, Byte isReview) {
        Flow flow = new Flow();
        flow.setId(flowId);
        flow.setUpdateTime(new Date());
        flow.setIsreview(isReview);
        flow.setReviewdate(new Date());
        flow.setReviewer(AuthUtils.getCurrUserName());
        return flowMapper.updateByPrimaryKeySelective(flow) > 0;
    }

    public static void main(String[] args) {
        /*
        String str = "i am /#p#/#p#";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("#p#", "tmp");
        paramMap.put("#vcs#", "tmp");


        Set<String> keySet = paramMap.keySet();
        for (String key : keySet) {
            str = str.replaceAll(key, paramMap.get(key));
        }
        System.out.println(str);


        System.out.println(String.format("sh /opt/auto/local/pullcode.sh -v %s -u %s -b %s -p", "s222", "s333", "b", "p"));
        */
//        String[] strs = "".split(",");
        Integer i = null;
        System.out.println(i > 0);
    }
}
