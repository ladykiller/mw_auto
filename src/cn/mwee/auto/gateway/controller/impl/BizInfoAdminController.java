package cn.mwee.auto.gateway.controller.impl;

import cn.mwee.auto.gateway.contract.BizInfoReq;
import cn.mwee.auto.gateway.contract.FlowTask;
import cn.mwee.auto.gateway.controller.IBizInfoAdminController;
import cn.mwee.auto.gateway.service.IGatewayService;
import cn.mwee.auto.misc.aspect.contract.Contract;
import cn.mwee.auto.misc.mq.AppEvent;
import cn.mwee.auto.misc.mq.MQSender;
import cn.mwee.auto.misc.req.ServiceRequest;
import cn.mwee.auto.misc.resp.NormalReturn;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * Created by huming on 16/6/7.
 */
@Controller("bizInfoAdminController")
public class BizInfoAdminController implements IBizInfoAdminController {

    @Autowired
    private IGatewayService gatewayService;

    @Resource(name="appEventMQSender")
    MQSender appEventMQSender;

    /**
     * contact demo
     * @param request
     * @return
     */
    @Override
    @Contract(BizInfoReq.class)
    public NormalReturn query(ServiceRequest request)
    {
        BizInfoReq req = request.getContract();
        return new NormalReturn("200","ok",JSON.toJSONString(req));
    }

    /**
     * get demo
     * @param request
     * @return
     */
    @Override
    public NormalReturn resc(ServiceRequest request)
    {
        int shopId = request.getJson().getInteger("shopId");
        return new NormalReturn("200","ok",String.valueOf(shopId));
    }

    /**
     *
     * db access demo
     *
     */
    @Override
    public NormalReturn count(ServiceRequest request)
    {
        FlowTask task = new FlowTask();

        task.setFlow(123);
        task.setName("任务");
        task.setTask(1);

        AppEvent event = new AppEvent(0,"soa.notify.flow.create",task);
        appEventMQSender.sendAsync(event.getEventType(), event);

        return new NormalReturn(3);
    }
}
