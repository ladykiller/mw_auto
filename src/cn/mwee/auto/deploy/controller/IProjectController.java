package cn.mwee.auto.deploy.controller;

import cn.mwee.auto.deploy.contract.template.QueryTemplatesRequest;
import cn.mwee.auto.misc.aspect.contract.Contract;
import cn.mwee.auto.misc.controller.IController;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/10.
 */
public interface IProjectController extends IController {

    /**
     * 获取项目所有模板中的区域服务状态
     * @return
     */
    NormalReturn getProjectZonesStatus(ServiceRequest request);
}
