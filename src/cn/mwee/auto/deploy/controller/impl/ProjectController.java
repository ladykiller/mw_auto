package cn.mwee.auto.deploy.controller.impl;

import cn.mwee.auto.deploy.contract.project.ProjectIdContract;
import cn.mwee.auto.deploy.controller.IProjectController;
import cn.mwee.auto.deploy.service.IProjectService;
import cn.mwee.auto.misc.aspect.contract.Contract;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;


/**
 * Created by Administrator on 2016/8/10.
 */
@Controller
public class ProjectController implements IProjectController {

    @Resource
    private IProjectService projectService;

    @Override
    @Contract(ProjectIdContract.class)
    public NormalReturn getProjectZonesStatus(ServiceRequest request) {
        ProjectIdContract req = request.getContract();
        return new NormalReturn(projectService.getProjectZonesStatus(req.getProjectId()));
    }

}
