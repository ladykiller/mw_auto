package cn.mwee.auto.deploy.service.execute;

import cn.mwee.auto.deploy.service.IZoneMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2016/8/15.
 */
@Component
public class ZoneMonitorScheduleExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ZoneMonitorScheduleExecutor.class);
    @Resource
    private IZoneMonitorService zoneMonitorService;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void execute() {
        try {
            logger.info(">>>>>>>>>>>>>>>>>>>> start send zone status monitor task");
            zoneMonitorService.sendAllMonitorTask();
            logger.info("<<<<<<<<<<<<<<<<<<<< end send zone status monitor task");
        } catch (Exception e) {
            logger.error("<<<<<<<<<<<<<<<<<<<< end send zone status monitor task with error:",e);
        }


    }
}
