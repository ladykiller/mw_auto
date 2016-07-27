package cn.mwee.auto.deploy.controller.impl;

import cn.mwee.auto.deploy.AutoAbstractController;
import cn.mwee.auto.deploy.contract.template.*;
import cn.mwee.auto.deploy.controller.ITemplateController;
import cn.mwee.auto.deploy.model.AutoTemplate;
import cn.mwee.auto.deploy.model.TemplateTask;
import cn.mwee.auto.deploy.service.ITemplateManagerService;
import cn.mwee.auto.misc.aspect.contract.Contract;
import cn.mwee.auto.misc.aspect.contract.Model;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Created by huming on 16/7/21.
 */
@Controller
public class TemplateController extends AutoAbstractController implements ITemplateController
{
    @Autowired
    private ITemplateManagerService templateManagerService;

    @Override
    @Contract(QueryTemplatesRequest.class)
    public NormalReturn queryTemplates(ServiceRequest request)
    {
        QueryTemplatesRequest req = request.getContract();
        QueryTemplatesResult result = templateManagerService.getTemplates(req);
        return new NormalReturn(result);
    }

    @Override
    @Contract(AddTemplateRequest.class)
    public NormalReturn addTemplate(ServiceRequest request)
    {
        AddTemplateRequest contract = request.getContract();
        templateManagerService.addTemplate(contract.getTemplateName());
        return new NormalReturn("success");
    }

    @Override
    @Contract(TemplateIdQuery.class)
    public NormalReturn getTemplate(ServiceRequest request)
    {
        TemplateIdQuery contract = request.getContract();
        AutoTemplate template = templateManagerService.getTemplate(contract.getTemplateId());
        return new NormalReturn(template);
    }

    @Override
    @Contract(TemplateIdQuery.class)
    public NormalReturn deleteTemplate(ServiceRequest request)
    {
        TemplateIdQuery contract = request.getContract();
        templateManagerService.deleteTemplate(contract.getTemplateId());
        return new NormalReturn("success");
    }

    @Override
    @Model(contract = ModifyTemplateRequest.class, model = AutoTemplate.class)
    public NormalReturn modifyTemplate(ServiceRequest request)
    {
        AutoTemplate template = request.getModel();
        templateManagerService.modifyTemplate(template);
        return new NormalReturn("success");
    }

    @Override
    @Model(contract = AddTemplateTaskRequest.class, model = TemplateTask.class)
    public NormalReturn addTask2Template(ServiceRequest request)
    {
        TemplateTask task = request.getModel();
        templateManagerService.addTask2Template(task.getTemplateId(),task);
        return new NormalReturn("success");
    }

    @Override
    @Contract(TemplateTaskIdQuery.class)
    public NormalReturn removeTemplateTask(ServiceRequest request)
    {
        TemplateTaskIdQuery contract = request.getContract();
        boolean rmSuccess = templateManagerService.removeTemplateTask(contract.getTemplateTaskId());
        return new NormalReturn(rmSuccess);
    }

    @Override
    @Contract(TemplateIdQuery.class)
    public NormalReturn getTempleteTasks(ServiceRequest request)
    {
        TemplateIdQuery contract = request.getContract();
        List<TemplateTask> tasks = templateManagerService.getTempleteTasks(contract.getTemplateId());
        return new NormalReturn(tasks);
    }
}
