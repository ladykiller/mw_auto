package cn.mwee.auto.deploy.model;

import java.util.Date;

public class AutoProject {
    /**
     * 项目Id，主键
     * projects.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * 项目名称
     * projects.projectName
     *
     * @mbggenerated
     */
    private String projectname;

    /**
     * 项目地址url
     * projects.url
     *
     * @mbggenerated
     */
    private String url;

    /**
     * 项目简介
     * projects.description
     *
     * @mbggenerated
     */
    private String description;

    /**
     * 项目状态
     * projects.state
     *
     * @mbggenerated
     */
    private Byte state;

    /**
     * 创建者
     * projects.creator
     *
     * @mbggenerated
     */
    private String creator;

    /**
     * 创建时间
     * projects.createTime
     *
     * @mbggenerated
     */
    private Date createtime;

    /**
     * 项目Id，主键
     * This method returns the value of the database column projects.id
     *
     * @return the value of projects.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * 项目Id，主键
     * This method sets the value of the database column projects.id
     *
     * @param id the value for projects.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 项目名称
     * This method returns the value of the database column projects.projectName
     *
     * @return the value of projects.projectName
     *
     * @mbggenerated
     */
    public String getProjectname() {
        return projectname;
    }

    /**
     * 项目名称
     * This method sets the value of the database column projects.projectName
     *
     * @param projectname the value for projects.projectName
     *
     * @mbggenerated
     */
    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    /**
     * 项目地址url
     * This method returns the value of the database column projects.url
     *
     * @return the value of projects.url
     *
     * @mbggenerated
     */
    public String getUrl() {
        return url;
    }

    /**
     * 项目地址url
     * This method sets the value of the database column projects.url
     *
     * @param url the value for projects.url
     *
     * @mbggenerated
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 项目简介
     * This method returns the value of the database column projects.description
     *
     * @return the value of projects.description
     *
     * @mbggenerated
     */
    public String getDescription() {
        return description;
    }

    /**
     * 项目简介
     * This method sets the value of the database column projects.description
     *
     * @param description the value for projects.description
     *
     * @mbggenerated
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 项目状态
     * This method returns the value of the database column projects.state
     *
     * @return the value of projects.state
     *
     * @mbggenerated
     */
    public Byte getState() {
        return state;
    }

    /**
     * 项目状态
     * This method sets the value of the database column projects.state
     *
     * @param state the value for projects.state
     *
     * @mbggenerated
     */
    public void setState(Byte state) {
        this.state = state;
    }

    /**
     * 创建者
     * This method returns the value of the database column projects.creator
     *
     * @return the value of projects.creator
     *
     * @mbggenerated
     */
    public String getCreator() {
        return creator;
    }

    /**
     * 创建者
     * This method sets the value of the database column projects.creator
     *
     * @param creator the value for projects.creator
     *
     * @mbggenerated
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * 创建时间
     * This method returns the value of the database column projects.createTime
     *
     * @return the value of projects.createTime
     *
     * @mbggenerated
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * 创建时间
     * This method sets the value of the database column projects.createTime
     *
     * @param createtime the value for projects.createTime
     *
     * @mbggenerated
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}