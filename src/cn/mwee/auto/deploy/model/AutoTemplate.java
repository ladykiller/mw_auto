package cn.mwee.auto.deploy.model;

import java.util.Date;

public class AutoTemplate {
    /**
     * templates.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * 模板名
     * templates.name
     *
     * @mbggenerated
     */
    private String name;

    /**
     * 创建时间
     * templates.create_time
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * 更新时间
     * templates.update_time
     *
     * @mbggenerated
     */
    private Date updateTime;

    /**
     * This method returns the value of the database column templates.id
     *
     * @return the value of templates.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method sets the value of the database column templates.id
     *
     * @param id the value for templates.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 模板名
     * This method returns the value of the database column templates.name
     *
     * @return the value of templates.name
     *
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * 模板名
     * This method sets the value of the database column templates.name
     *
     * @param name the value for templates.name
     *
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 创建时间
     * This method returns the value of the database column templates.create_time
     *
     * @return the value of templates.create_time
     *
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     * This method sets the value of the database column templates.create_time
     *
     * @param createTime the value for templates.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 更新时间
     * This method returns the value of the database column templates.update_time
     *
     * @return the value of templates.update_time
     *
     * @mbggenerated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新时间
     * This method sets the value of the database column templates.update_time
     *
     * @param updateTime the value for templates.update_time
     *
     * @mbggenerated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}