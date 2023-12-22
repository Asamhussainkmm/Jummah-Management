package com.sltj.jummah;

public class User {
    private int id;
    private String name;
    private String phone;
    private String profile;
    private String lastJummah;
    private String lastJummahDate;
    private String username;
    private String password;

    public User() {
    }

    public User(String name, String phone, String profile, String lastJummah, String lastJummahDate) {
        this.name = name;
        this.phone = phone;
        this.profile = profile;
        this.lastJummah = lastJummah;
        this.lastJummahDate = lastJummahDate;
    }

    public User(String name, String phone, String profile, String lastJummah, String lastJummahDate, String username, String password) {
        this.name = name;
        this.phone = phone;
        this.profile = profile;
        this.lastJummah = lastJummah;
        this.lastJummahDate = lastJummahDate;
        this.username = username;
        this.password = password;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLastJummah() {
        return lastJummah;
    }

    public void setLastJummah(String lastJummah) {
        this.lastJummah = lastJummah;
    }

    public String getLastJummahDate() {
        return lastJummahDate;
    }

    public void setLastJummahDate(String lastJummahDate) {
        this.lastJummahDate = lastJummahDate;
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
}
