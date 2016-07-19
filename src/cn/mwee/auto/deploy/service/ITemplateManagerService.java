package cn.mwee.auto.deploy.service;

import java.util.List;

import cn.mwee.auto.deploy.contract.TemplateTaskContract;
import cn.mwee.auto.deploy.model.AutoTemplate;
import cn.mwee.auto.deploy.model.TemplateTask;

/**
 * Created by huming on 16/6/24.
 */
public interface ITemplateManagerService {

    boolean addTask2Template(int templateId,TemplateTask task);

    boolean removeTemplateTask(int templateId,int templateTaskId);

    /**
	 * 获取模板
	 * @return
	 */
	List<AutoTemplate> getTemplates();

	/**
	 * 获取模板任务信息
	 * @param templateId
	 * @return
	 */
	List<TemplateTask> getTempleteTasks(TemplateTaskContract reqModel);

}
