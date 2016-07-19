package cn.mwee.auto.deploy.controller.impl;

import cn.mwee.auto.deploy.contract.FlowAddContract;
import cn.mwee.auto.deploy.contract.QueryTasksRequest;
import cn.mwee.auto.deploy.contract.QueryTasksResult;
import cn.mwee.auto.deploy.controller.IAutoTaskController;
import cn.mwee.auto.deploy.service.IFlowTaskLogService;
import cn.mwee.auto.deploy.service.ITaskManagerService;
import cn.mwee.auto.misc.aspect.contract.Contract;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by huming on 16/7/19.
 */
@Controller
public class AutoTaskController implements IAutoTaskController
{
    @Autowired
    private ITaskManagerService taskManagerService;

    @Override
    @Contract(QueryTasksRequest.class)
    public NormalReturn getTasks(ServiceRequest request)
    {
        QueryTasksRequest req = request.getContract();
        QueryTasksResult result = taskManagerService.getAutoTasks(req);
        return new NormalReturn(result);
    }
}
