package com.gyc.pojo;

import java.util.Date;

/**
 * Created by GYC
 * 2020/11/19 12:46
 */
public class User {
    private int id;
    private String userId;
    private String userName;
    private String phone;
    private int lanId;
    private int regionId;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getLanId() {
        return lanId;
    }

    public void setLanId(int lanId) {
        this.lanId = lanId;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", lanId=" + lanId +
                ", regionId=" + regionId +
                ", createTime=" + createTime +
                '}';
    }
}
