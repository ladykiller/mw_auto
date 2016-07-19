package cn.mwee.auto.gateway.handler;

import cn.mwee.auto.gateway.controller.IBizInfoAdminController;
import cn.mwee.auto.misc.controller.IController;
import cn.mwee.auto.misc.handler.BaseHandler;
import cn.mwee.auto.misc.server.MyApplicationContext;

/**
 * Created by huming on 16/6/7.
 */
public class BizInfoHandler extends BaseHandler {

    public BizInfoHandler() {
        this.adaptorController = (IController) MyApplicationContext.getInstance()
                .getBean(IBizInfoAdminController.class);
    }

}