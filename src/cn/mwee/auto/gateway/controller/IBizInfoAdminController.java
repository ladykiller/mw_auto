package cn.mwee.auto.gateway.controller;

import cn.mwee.auto.misc.controller.IController;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;

/**
 * Created by huming on 16/6/7.
 */
public interface IBizInfoAdminController extends IController {

    NormalReturn query(ServiceRequest request);

    NormalReturn resc(ServiceRequest request);

    NormalReturn count(ServiceRequest request);

}
