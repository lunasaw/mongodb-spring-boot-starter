package com.luna.mongodb.entity;

import com.luna.mongodb.anno.AutoIncId;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * (UserDO)实体类
 *
 * @author luna
 * @since 2021-04-28 15:45:17
 */
public class UserDO implements Serializable {
    private static final long serialVersionUID = 164838049726864228L;
    @AutoIncId
    private Long              id;
    /** 用户名 */
    private String            username;
    /** 密码 */
    private String            password;
    /** 性别 */
    private String            gender;
    /** 手机 */
    private String            cellphone;
    /** 邮箱 */
    private String            email;
    /** 状态 */
    private Integer           status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserDO{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", gender='" + gender + '\'' +
            ", cellphone='" + cellphone + '\'' +
            ", email='" + email + '\'' +
            ", status=" + status +
            '}';
    }
}
