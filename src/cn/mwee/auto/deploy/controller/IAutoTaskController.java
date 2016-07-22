package cn.mwee.auto.deploy.controller;

import cn.mwee.auto.deploy.contract.QueryTasksRequest;
import cn.mwee.auto.deploy.contract.QueryTasksResult;
import cn.mwee.auto.deploy.model.AutoTask;
import cn.mwee.auto.misc.controller.IController;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;

/**
 * Created by huming on 16/7/19.
 */
public interface IAutoTaskController extends IController
{
    NormalReturn queryTasks(ServiceRequest request);

    NormalReturn addTask(ServiceRequest request);

    NormalReturn getTask(ServiceRequest request);

    NormalReturn deleteTask(ServiceRequest request);

    NormalReturn modifyTask(ServiceRequest request);
}
