package com.mobiletrain.manga.model;

/**
 * Created by qf on 2016/11/11.
 */
public class UserBean {


    private String name;
    private String password;
    private int id;
    private  String loveId;

    public UserBean(String name, String password, int id, String loveId) {
        this.name = name;
        this.password = password;
        this.id = id;
        this.loveId = loveId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoveId() {
        return loveId;
    }

    public void setLoveId(String loveId) {
        this.loveId = loveId;
    }
}
