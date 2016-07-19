package cn.mwee.auto.deploy.controller;

import cn.mwee.auto.misc.controller.IController;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;

/**
 * Created by huming on 16/7/19.
 */
public interface IAutoTaskController extends IController
{
    NormalReturn getTasks(ServiceRequest request);
}
