package cn.mwee.auto.deploy.service.impl;

import cn.mwee.auto.common.util.MessageUtil;
import cn.mwee.auto.deploy.model.AutoTemplate;
import cn.mwee.auto.deploy.model.TemplateZonesMonitor;
import cn.mwee.auto.deploy.model.ZoneMonitorTask;
import cn.mwee.auto.deploy.model.ZoneStateModel;
import cn.mwee.auto.deploy.service.ITemplateManagerService;
import cn.mwee.auto.deploy.service.IZoneMonitorService;
import cn.mwee.auto.misc.mq.AppEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */
@Service
public class ZoneMonitorService implements IZoneMonitorService {

    @Resource
    private ITemplateManagerService templateManagerService;

    @Resource(name = "zoneMonitorMQSender")
    private MessageUtil zoneMonitorMQSender;

    @Value("${mw.monitor.shell}")
    private String defaultMonitorShell;

    @Value("${mw.monitor.user}")
    private String defaultMonitorUser;

    @Value("${localhost.name}")
    private String localHostName;

    @Override
    public void sendAllMonitorTask() {
        List<AutoTemplate> templates = templateManagerService.getAllInUseTemplate();
        templates.forEach(template -> sendMonitorTask4Template(template.getId()));
    }

    @Override
    public void sendMonitorTask4Template(Integer templateId) {
        List<TemplateZonesMonitor> monitors = templateManagerService.getTemplateZoneMonitor(templateId);
        if (CollectionUtils.isEmpty(monitors)) return;
        TemplateZonesMonitor monitor = monitors.get(0);
        List<ZoneStateModel> zoneStateModels =templateManagerService.getTemplateZoneStatus(templateId);
        zoneStateModels.forEach(zoneStateModel -> {
            ZoneMonitorTask monitorTask = new ZoneMonitorTask();
            monitorTask.setTemplateZoneId(zoneStateModel.getTemplateZoneId());
            String command = monitor.getMonitorshell() + " " + monitor.getMonitorreq();
            String targetHost = StringUtils.isNotBlank(zoneStateModel.getIp()) ? zoneStateModel.getIp() : zoneStateModel.getHost() ;
            command = command.replaceAll("%zone%",targetHost);
            monitorTask.setMonitorShell(command);
            monitorTask.setMonitorUser(StringUtils.isNotBlank(monitor.getMonitoruser()) ? monitor.getMonitoruser() : defaultMonitorUser);
            monitorTask.setExeTargetHost(localHostName);
            AppEvent evt = new AppEvent(0,"",monitorTask);
            zoneMonitorMQSender.convertAndSend(evt);
        });
    }

    @Override
    public void sendMonitorTask4Project(Integer projectId) {
        List<AutoTemplate> templates = templateManagerService.getTemplates4Project(projectId);
        templates.forEach(template -> {
            if (template.getInuse() == 1) {
                sendMonitorTask4Template(template.getId());
            }
        });
    }
}
