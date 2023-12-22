package com.sltj.jummah;

public class Dhaayi {

    private String name, branch, updatedDate;
    private int profile;

    public Dhaayi(){

    }
    public Dhaayi(String name, String branch, String updatedDate, int profile) {
        this.name = name;
        this.branch = branch;
        this.updatedDate = updatedDate;
        this.profile = profile;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }
}
