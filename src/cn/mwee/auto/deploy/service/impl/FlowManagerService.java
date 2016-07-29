package cn.mwee.auto.deploy.service.impl;

import cn.mwee.auto.auth.util.AuthUtils;
import cn.mwee.auto.common.db.BaseModel;
import cn.mwee.auto.common.db.BaseQueryResult;
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

    @Autowired
    private AutoTaskMapper autoTaskMapper;

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
    public int createFlow(Flow flow, Map<String, Object> params) throws Exception {
        /*
        FlowExample example = new FlowExample();

        FlowExample.Criteria criteria= example.createCriteria();

        criteria.andStateEqualTo("ing");

        List<Flow> list = flowMapper.selectByExample(example);
        */
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
        return result > 0 ? flow.getId() : 0;
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
        //get template
        AutoTemplate template = templateManagerService.getTemplate(flow.getTemplateId());
        if (template == null) {
            throw new NullPointerException("Cant find template for id:" + flow.getTemplateId());
        }

        //get flowTask
        TemplateTaskExample ttExample = new TemplateTaskExample();
        ttExample.createCriteria().andTemplateIdEqualTo(flow.getTemplateId());
        List<TemplateTask> tts = templateTaskMapper.selectByExample(ttExample);
        //parse zones
        String zoneStr = flow.getZones();
        String[] zones = zoneStr.split(",");
        //parse Task EXE params
        Map<String, String> paramsMap = new HashMap<>();
        String paramStr = flow.getParams();
        if (StringUtils.isNotBlank(paramStr)) {
            paramsMap = JSON.parseObject(flow.getParams(), Map.class);
        }

        List<FlowTask> fts = new ArrayList<>();
        //prepare组(根据模板中是否有配置git仓库添加prepare组)
        if (StringUtils.isNotBlank(template.getVcsRep())) {
            fts.addAll(buildPrepareTasks(flow,template));
        }
        //copy tasks
        for (TemplateTask tt : tts) {
            //区域组
            for (String zone : zones) {
                if (StringUtils.isBlank(zone)) continue;
                FlowTask ft = new FlowTask();
                ft.setGroup(tt.getGroup());
                ft.setPriority(tt.getPriority());
                ft.setTaskId(tt.getTaskId());
                ft.setTaskType(tt.getTaskType());
                ft.setFlowId(flowId);
                ft.setZone(zone);
                replaceParams(ft, paramsMap);
                ft.setState(TaskState.INIT.name());
                ft.setCreateTime(new Date());
                fts.add(ft);
            }
        }
        int result = flowTaskExtMapper.insertBatch(fts);
        return result > 0;
    }

    /**
     * 参数替换
     *
     * @param ft
     * @param paramMap
     */
    private void replaceParams(FlowTask ft, Map<String, String> paramMap) {

        AutoTask task = autoTaskMapper.selectByPrimaryKey(ft.getTaskId());
        if (task != null && StringUtils.isNotBlank(task.getParams())) {
            String paramStr = task.getParams();
            String[] paramKeys = paramStr.split(" ");
            for (String paramKey : paramKeys) {
                String paramValue = paramMap.get(paramKey.replace("#", ""));
                if (null != paramValue) {
                    paramStr = paramStr.replace(paramKey, paramValue);
                }
            }
            ft.setTaskParam(paramStr);
        }
    }

    /**
     * 构建prepare组的任务
     * @param flow
     * @param template
     * @return
     */
    private List<FlowTask> buildPrepareTasks(Flow flow, AutoTemplate template) {
        List<FlowTask> list = new ArrayList<>();
        for (short i = 1; i< 4; i++) {
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
                    setPullShell(template,flow,ft);
                    break;
                case 2:
                    ft.setTaskParam(buildShell);
                    setBuildShell(template,ft);
                    break;
                case 3:
                    ft.setTaskParam(deployShell);
                    setDeployShell(template,flow,ft);
                    break;
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
            String vcsBranch =flow.getVcsBranch();
            String projectName = repUrl.substring(repUrl.lastIndexOf('/')+1,repUrl.lastIndexOf('.'));
            String.format(updateShell,vcsType,vcsRep,vcsBranch,projectName);
            ft.setTaskParam(String.format(updateShell,vcsType,vcsRep,vcsBranch,projectName));
        }
    }

    /**
     * 构建脚本
     * @param template
     * @param ft
     */
    private void setBuildShell(AutoTemplate template, FlowTask ft){
        String repUrl = template.getVcsRep();
        if (StringUtils.isNotBlank(repUrl)) {
            String projectName = repUrl.substring(repUrl.lastIndexOf('/')+1,repUrl.lastIndexOf('.'));
            ft.setTaskParam(String.format(buildShell,projectName));
        }
    }

    /**
     * 部署脚本
     * @param template
     * @param flow
     * @param ft
     */
    private void setDeployShell(AutoTemplate template,Flow flow, FlowTask ft) {
        String repUrl = template.getVcsRep();
        if (StringUtils.isNotBlank(repUrl)
                && StringUtils.isNotBlank(flow.getVcsBranch())) {
            String projectName = repUrl.substring(repUrl.lastIndexOf('/')+1,repUrl.lastIndexOf('.'));
            String zones = flow.getZones();
            ft.setTaskParam(String.format(deployShell,projectName,projectName,zones));
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
    public boolean updateTaskStatus(int flowTaskId, String state) {
        FlowTask flowTask = new FlowTask();
        flowTask.setId(flowTaskId);
        flowTask.setState(state);
        flowTask.setUpdateTime(new Date());
        int result = flowTaskMapper.updateByPrimaryKeySelective(flowTask);
        return result > 0;
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
        Flow flow = flowMapper.selectByPrimaryKey(flowId);
        String[] zones = flow.getZones().split(",");
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
        String localZoneState = getZoneState(flowId,localHost);
        if (StringUtils.isNotBlank(localZoneState)) returnMap.put(localHost,localZoneState);
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
    public BaseQueryResult<Flow> getFlows(FlowQueryContract req,Flow flow) {
        FlowExample example = new FlowExample();
        example.setOrderByClause("id desc");
        example.setLimitStart(0);
        example.setLimitEnd(20);
        example.createCriteria()
                .andProjectIdEqualTo(req.getProjectId());
        return BaseModel.selectByPage(flowMapper,example,req.getPageInfo(),req.getPageInfo() == null);
    }

    @Override
    public Flow getFlow(Integer flowId) {
        return  flowMapper.selectByPrimaryKey(flowId);
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
        System.out.println(String.format("sh /opt/auto/local/pullcode.sh -v %s -u %s -b %s -p","s222","s333","b","p"));
    }
}
