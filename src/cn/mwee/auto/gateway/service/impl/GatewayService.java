package cn.mwee.auto.gateway.service.impl;

import cn.mwee.auto.gateway.dao.TaskMapper;
import cn.mwee.auto.gateway.model.TaskExample;
import cn.mwee.auto.gateway.service.IGatewayService;
import cn.mwee.auto.gateway.service.conn.MongoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by huming on 16/6/7.
 */
@Service
public class GatewayService implements IGatewayService {

    private Logger logger = LoggerFactory.getLogger(GatewayService.class);

    @Autowired
    private MongoService mongoService;

    @Autowired
    private TaskMapper taskMapper;


    @Override
    public int count()
    {
        TaskExample example = new TaskExample();

        return taskMapper.countByExample(example);
    }
}
