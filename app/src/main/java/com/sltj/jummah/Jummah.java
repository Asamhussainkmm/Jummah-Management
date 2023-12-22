package com.sltj.jummah;

public class Jummah {

    private String id;
    private User user;
    private Branch branch;
    private String date;

    public Jummah() {
    }

    public Jummah(String id, User user, Branch branch, String date) {
        this.id = id;
        this.user = user;
        this.branch = branch;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
