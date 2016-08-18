package cn.mwee.auto.deploy.model;

import java.util.Date;

public class TemplateZonesMonitor {
    /**
     * template_zones_monitor.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * 模板Id
     * template_zones_monitor.templateId
     *
     * @mbggenerated
     */
    private Integer templateid;

    /**
     * 监控脚本
     * template_zones_monitor.monitorShell
     *
     * @mbggenerated
     */
    private String monitorshell;

    /**
     * 监控脚本执行用户
     * template_zones_monitor.monitorUser
     *
     * @mbggenerated
     */
    private String monitoruser;

    /**
     * 查询参数
     * template_zones_monitor.monitorReq
     *
     * @mbggenerated
     */
    private String monitorreq;

    /**
     * template_zones_monitor.createTime
     *
     * @mbggenerated
     */
    private Date createtime;

    /**
     * template_zones_monitor.updateTime
     *
     * @mbggenerated
     */
    private Date updatetime;

    /**
     * This method returns the value of the database column template_zones_monitor.id
     *
     * @return the value of template_zones_monitor.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method sets the value of the database column template_zones_monitor.id
     *
     * @param id the value for template_zones_monitor.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 模板Id
     * This method returns the value of the database column template_zones_monitor.templateId
     *
     * @return the value of template_zones_monitor.templateId
     *
     * @mbggenerated
     */
    public Integer getTemplateid() {
        return templateid;
    }

    /**
     * 模板Id
     * This method sets the value of the database column template_zones_monitor.templateId
     *
     * @param templateid the value for template_zones_monitor.templateId
     *
     * @mbggenerated
     */
    public void setTemplateid(Integer templateid) {
        this.templateid = templateid;
    }

    /**
     * 监控脚本
     * This method returns the value of the database column template_zones_monitor.monitorShell
     *
     * @return the value of template_zones_monitor.monitorShell
     *
     * @mbggenerated
     */
    public String getMonitorshell() {
        return monitorshell;
    }

    /**
     * 监控脚本
     * This method sets the value of the database column template_zones_monitor.monitorShell
     *
     * @param monitorshell the value for template_zones_monitor.monitorShell
     *
     * @mbggenerated
     */
    public void setMonitorshell(String monitorshell) {
        this.monitorshell = monitorshell;
    }

    /**
     * 监控脚本执行用户
     * This method returns the value of the database column template_zones_monitor.monitorUser
     *
     * @return the value of template_zones_monitor.monitorUser
     *
     * @mbggenerated
     */
    public String getMonitoruser() {
        return monitoruser;
    }

    /**
     * 监控脚本执行用户
     * This method sets the value of the database column template_zones_monitor.monitorUser
     *
     * @param monitoruser the value for template_zones_monitor.monitorUser
     *
     * @mbggenerated
     */
    public void setMonitoruser(String monitoruser) {
        this.monitoruser = monitoruser;
    }

    /**
     * 查询参数
     * This method returns the value of the database column template_zones_monitor.monitorReq
     *
     * @return the value of template_zones_monitor.monitorReq
     *
     * @mbggenerated
     */
    public String getMonitorreq() {
        return monitorreq;
    }

    /**
     * 查询参数
     * This method sets the value of the database column template_zones_monitor.monitorReq
     *
     * @param monitorreq the value for template_zones_monitor.monitorReq
     *
     * @mbggenerated
     */
    public void setMonitorreq(String monitorreq) {
        this.monitorreq = monitorreq;
    }

    /**
     * This method returns the value of the database column template_zones_monitor.createTime
     *
     * @return the value of template_zones_monitor.createTime
     *
     * @mbggenerated
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * This method sets the value of the database column template_zones_monitor.createTime
     *
     * @param createtime the value for template_zones_monitor.createTime
     *
     * @mbggenerated
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /**
     * This method returns the value of the database column template_zones_monitor.updateTime
     *
     * @return the value of template_zones_monitor.updateTime
     *
     * @mbggenerated
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * This method sets the value of the database column template_zones_monitor.updateTime
     *
     * @param updatetime the value for template_zones_monitor.updateTime
     *
     * @mbggenerated
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}