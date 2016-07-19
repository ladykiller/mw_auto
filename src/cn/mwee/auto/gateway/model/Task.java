package cn.mwee.auto.gateway.model;

import java.util.Date;

public class Task {
    /**
     * tasks.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * 任务名
     * tasks.name
     *
     * @mbggenerated
     */
    private String name;

    /**
     * 路径
     * tasks.pwd
     *
     * @mbggenerated
     */
    private String pwd;

    /**
     * 参数
     * tasks.params
     *
     * @mbggenerated
     */
    private String params;

    /**
     * tasks.create_time
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * tasks.update_time
     *
     * @mbggenerated
     */
    private Date updateTime;

    /**
     * This method returns the value of the database column tasks.id
     *
     * @return the value of tasks.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method sets the value of the database column tasks.id
     *
     * @param id the value for tasks.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 任务名
     * This method returns the value of the database column tasks.name
     *
     * @return the value of tasks.name
     *
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * 任务名
     * This method sets the value of the database column tasks.name
     *
     * @param name the value for tasks.name
     *
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 路径
     * This method returns the value of the database column tasks.pwd
     *
     * @return the value of tasks.pwd
     *
     * @mbggenerated
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * 路径
     * This method sets the value of the database column tasks.pwd
     *
     * @param pwd the value for tasks.pwd
     *
     * @mbggenerated
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * 参数
     * This method returns the value of the database column tasks.params
     *
     * @return the value of tasks.params
     *
     * @mbggenerated
     */
    public String getParams() {
        return params;
    }

    /**
     * 参数
     * This method sets the value of the database column tasks.params
     *
     * @param params the value for tasks.params
     *
     * @mbggenerated
     */
    public void setParams(String params) {
        this.params = params;
    }

    /**
     * This method returns the value of the database column tasks.create_time
     *
     * @return the value of tasks.create_time
     *
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method sets the value of the database column tasks.create_time
     *
     * @param createTime the value for tasks.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method returns the value of the database column tasks.update_time
     *
     * @return the value of tasks.update_time
     *
     * @mbggenerated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method sets the value of the database column tasks.update_time
     *
     * @param updateTime the value for tasks.update_time
     *
     * @mbggenerated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}