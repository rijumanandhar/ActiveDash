package com.example.activedash.profile;

public class UserBadge {
    String badgename;
    String icon;

    public UserBadge() {
    }

    public UserBadge(String badgename, String icon) {
        this.badgename = badgename;
        this.icon = icon;
    }

    public String getBadgename() {
        return badgename;
    }

    public void setBadgename(String badgename) {
        this.badgename = badgename;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
