package com.example.sinior.gpsrecorderv3.Beans;

import java.util.Date;

public class Point {

    private int id;
    private String atitude;
    private String longtude;
    private String createDate;
    private String stat;
    private boolean removed;

    public Point() {
    }

    public Point(int id, String atitude, String longtude, String createDate, String stat) {
        this.id = id;
        this.atitude = atitude;
        this.longtude = longtude;
        this.createDate = createDate;
        this.stat = stat;
    }

    public Point(String atitude, String longtude, String createDate, String stat) {
        this.atitude = atitude;
        this.longtude = longtude;
        this.createDate = createDate;
        this.stat = stat;
    }

    public int getId() {
        return id;
    }

    public String getAtitude() {
        return atitude;
    }

    public String getLongtude() {
        return longtude;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getStat() {
        return stat;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAtitude(String atitude) {
        this.atitude = atitude;
    }

    public void setLongtude(String longtude) {
        this.longtude = longtude;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
}
