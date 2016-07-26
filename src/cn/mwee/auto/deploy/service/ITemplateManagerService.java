package cn.mwee.auto.deploy.service;

import java.util.List;

import cn.mwee.auto.deploy.contract.template.TemplateTaskContract;
import cn.mwee.auto.deploy.model.AutoTemplate;
import cn.mwee.auto.deploy.model.TemplateTask;

/**
 * Created by huming on 16/6/24.
 */
public interface ITemplateManagerService {

    boolean addTemplate(String templateName);

    boolean deleteTemplate(int templateId);

    boolean modifyTemplate(AutoTemplate template);

    /**
     * 获取模板
     * @return
     */
    TemplateTaskContract.QueryTemplatesResult getTemplates(TemplateTaskContract.QueryTemplatesRequest request);



    boolean addTask2Template(int templateId,TemplateTask task);

    boolean removeTemplateTask(int templateTaskId);

    boolean modifyTemplateTask(TemplateTask task);

	/**
	 * 获取模板任务信息
	 * @return
	 */
	List<TemplateTask> getTempleteTasks(TemplateTaskContract reqModel);

}
