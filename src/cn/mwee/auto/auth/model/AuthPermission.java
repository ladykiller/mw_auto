package cn.mwee.auto.auth.model;

import java.util.Date;

public class AuthPermission {
    /**
     *  主键
     * auth_permission.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * 父级Id
     * auth_permission.parent_id
     *
     * @mbggenerated
     */
    private Integer parentId;

    /**
     * 权限编码
     * auth_permission.code
     *
     * @mbggenerated
     */
    private String code;

    /**
     * 权限名称
     * auth_permission.name
     *
     * @mbggenerated
     */
    private String name;

    /**
     * 类型：1，url； 2，button；3，其他
     * auth_permission.type
     *
     * @mbggenerated
     */
    private Byte type;

    /**
     * 描述
     * auth_permission.description
     *
     * @mbggenerated
     */
    private String description;

    /**
     * 状态，0：不可用，1：可用
     * auth_permission.status
     *
     * @mbggenerated
     */
    private Boolean status;

    /**
     * 创建时间
     * auth_permission.create_time
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * 更新时间
     * auth_permission.update_time
     *
     * @mbggenerated
     */
    private Date updateTime;

    /**
     *  主键
     * This method returns the value of the database column auth_permission.id
     *
     * @return the value of auth_permission.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     *  主键
     * This method sets the value of the database column auth_permission.id
     *
     * @param id the value for auth_permission.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 父级Id
     * This method returns the value of the database column auth_permission.parent_id
     *
     * @return the value of auth_permission.parent_id
     *
     * @mbggenerated
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 父级Id
     * This method sets the value of the database column auth_permission.parent_id
     *
     * @param parentId the value for auth_permission.parent_id
     *
     * @mbggenerated
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * 权限编码
     * This method returns the value of the database column auth_permission.code
     *
     * @return the value of auth_permission.code
     *
     * @mbggenerated
     */
    public String getCode() {
        return code;
    }

    /**
     * 权限编码
     * This method sets the value of the database column auth_permission.code
     *
     * @param code the value for auth_permission.code
     *
     * @mbggenerated
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 权限名称
     * This method returns the value of the database column auth_permission.name
     *
     * @return the value of auth_permission.name
     *
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * 权限名称
     * This method sets the value of the database column auth_permission.name
     *
     * @param name the value for auth_permission.name
     *
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 类型：1，url； 2，button；3，其他
     * This method returns the value of the database column auth_permission.type
     *
     * @return the value of auth_permission.type
     *
     * @mbggenerated
     */
    public Byte getType() {
        return type;
    }

    /**
     * 类型：1，url； 2，button；3，其他
     * This method sets the value of the database column auth_permission.type
     *
     * @param type the value for auth_permission.type
     *
     * @mbggenerated
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 描述
     * This method returns the value of the database column auth_permission.description
     *
     * @return the value of auth_permission.description
     *
     * @mbggenerated
     */
    public String getDescription() {
        return description;
    }

    /**
     * 描述
     * This method sets the value of the database column auth_permission.description
     *
     * @param description the value for auth_permission.description
     *
     * @mbggenerated
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 状态，0：不可用，1：可用
     * This method returns the value of the database column auth_permission.status
     *
     * @return the value of auth_permission.status
     *
     * @mbggenerated
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 状态，0：不可用，1：可用
     * This method sets the value of the database column auth_permission.status
     *
     * @param status the value for auth_permission.status
     *
     * @mbggenerated
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 创建时间
     * This method returns the value of the database column auth_permission.create_time
     *
     * @return the value of auth_permission.create_time
     *
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     * This method sets the value of the database column auth_permission.create_time
     *
     * @param createTime the value for auth_permission.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 更新时间
     * This method returns the value of the database column auth_permission.update_time
     *
     * @return the value of auth_permission.update_time
     *
     * @mbggenerated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新时间
     * This method sets the value of the database column auth_permission.update_time
     *
     * @param updateTime the value for auth_permission.update_time
     *
     * @mbggenerated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}