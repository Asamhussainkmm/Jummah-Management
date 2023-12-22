package com.sltj.jummah;

public class Branch {

    private int id;
    private String name;
    private String phone;
    private String lastJummah;
    private String lastJummahDate;
    private String username;
    private String password;

    public Branch() {
    }

    public Branch(String name, String phone, String lastJummah, String lastJummahDate, String username, String password) {
        this.name = name;
        this.phone = phone;
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

    public String getLastJummah() {
        return lastJummah;
    }

    public void setLastJummah(String lastJummah) {
        this.lastJummah = lastJummah;
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

    public String getLastJummahDate() {
        return lastJummahDate;
    }

    public void setLastJummahDate(String lastJummahDate) {
        this.lastJummahDate = lastJummahDate;
    }
}
