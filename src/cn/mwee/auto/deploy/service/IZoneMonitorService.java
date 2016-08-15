package cn.mwee.auto.deploy.service;

/**
 * Created by Administrator on 2016/8/12.
 */
public interface IZoneMonitorService {
    void sendAllMonitorTask();
    void sendMonitorTask4Template(Integer templateId);
    void sendMonitorTask4Project(Integer projectId);
}
