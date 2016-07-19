package cn.mwee.auto.deploy.model;

import java.util.Date;

public class TemplateTask {
    /**
     * template_tasks.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * 模板id
     * template_tasks.template_id
     *
     * @mbggenerated
     */
    private Integer templateId;

    /**
     * template_tasks.group
     *
     * @mbggenerated
     */
    private Byte group;

    /**
     * 优先级
     * template_tasks.priority
     *
     * @mbggenerated
     */
    private Short priority;

    /**
     * 任务id
     * template_tasks.task_id
     *
     * @mbggenerated
     */
    private Integer taskId;

    /**
     * 任务类型 [手动，自动，定时]
     * template_tasks.task_type
     *
     * @mbggenerated
     */
    private String taskType;

    /**
     * template_tasks.create_time
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * This method returns the value of the database column template_tasks.id
     *
     * @return the value of template_tasks.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method sets the value of the database column template_tasks.id
     *
     * @param id the value for template_tasks.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 模板id
     * This method returns the value of the database column template_tasks.template_id
     *
     * @return the value of template_tasks.template_id
     *
     * @mbggenerated
     */
    public Integer getTemplateId() {
        return templateId;
    }

    /**
     * 模板id
     * This method sets the value of the database column template_tasks.template_id
     *
     * @param templateId the value for template_tasks.template_id
     *
     * @mbggenerated
     */
    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    /**
     * This method returns the value of the database column template_tasks.group
     *
     * @return the value of template_tasks.group
     *
     * @mbggenerated
     */
    public Byte getGroup() {
        return group;
    }

    /**
     * This method sets the value of the database column template_tasks.group
     *
     * @param group the value for template_tasks.group
     *
     * @mbggenerated
     */
    public void setGroup(Byte group) {
        this.group = group;
    }

    /**
     * 优先级
     * This method returns the value of the database column template_tasks.priority
     *
     * @return the value of template_tasks.priority
     *
     * @mbggenerated
     */
    public Short getPriority() {
        return priority;
    }

    /**
     * 优先级
     * This method sets the value of the database column template_tasks.priority
     *
     * @param priority the value for template_tasks.priority
     *
     * @mbggenerated
     */
    public void setPriority(Short priority) {
        this.priority = priority;
    }

    /**
     * 任务id
     * This method returns the value of the database column template_tasks.task_id
     *
     * @return the value of template_tasks.task_id
     *
     * @mbggenerated
     */
    public Integer getTaskId() {
        return taskId;
    }

    /**
     * 任务id
     * This method sets the value of the database column template_tasks.task_id
     *
     * @param taskId the value for template_tasks.task_id
     *
     * @mbggenerated
     */
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    /**
     * 任务类型 [手动，自动，定时]
     * This method returns the value of the database column template_tasks.task_type
     *
     * @return the value of template_tasks.task_type
     *
     * @mbggenerated
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * 任务类型 [手动，自动，定时]
     * This method sets the value of the database column template_tasks.task_type
     *
     * @param taskType the value for template_tasks.task_type
     *
     * @mbggenerated
     */
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    /**
     * This method returns the value of the database column template_tasks.create_time
     *
     * @return the value of template_tasks.create_time
     *
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method sets the value of the database column template_tasks.create_time
     *
     * @param createTime the value for template_tasks.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}