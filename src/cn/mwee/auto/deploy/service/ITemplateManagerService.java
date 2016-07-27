package cn.mwee.auto.deploy.service;

import java.util.List;

import cn.mwee.auto.deploy.contract.template.QueryTemplatesRequest;
import cn.mwee.auto.deploy.contract.template.QueryTemplatesResult;
import cn.mwee.auto.deploy.contract.template.TemplateTaskContract;
import cn.mwee.auto.deploy.model.AutoTask;
import cn.mwee.auto.deploy.model.AutoTemplate;
import cn.mwee.auto.deploy.model.TemplateTask;
import cn.mwee.auto.deploy.model.TemplateZone;

/**
 * Created by huming on 16/6/24.
 */
public interface ITemplateManagerService {

    AutoTemplate getTemplate(int templateId);

    boolean addTemplate(AutoTemplate template);

    boolean deleteTemplate(int templateId);

    boolean modifyTemplate(AutoTemplate template);

    /**
     * 获取模板
     * @return
     */
    QueryTemplatesResult getTemplates(QueryTemplatesRequest request);



    boolean addTask2Template(int templateId,TemplateTask task);

    boolean removeTemplateTask(int templateTaskId);

    boolean modifyTemplateTask(TemplateTask task);

	/**
	 * 获取模板任务信息
	 * @return
	 */
	List<TemplateTask> getTemplateTasks(int templateId);

    /**
     * 获取模板区域信息
     * @param templateId
     * @return
     */
	List<TemplateZone> getTemplateZones(Integer templateId);

    /**
     * 获取模板中的任务
     * @param templateId
     * @return
     */
    List<AutoTask> getTemplateSimpleTasks(Integer templateId);

    /**
     * 获取模板任务中的参数可以列表
     * @param templateId
     * @return
     */
    List<String> getTemplateTaskParamKeys(Integer templateId);
}
