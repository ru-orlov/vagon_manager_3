package com.example.wagonmanager3.models;

import java.util.Date;

public class ScanHistory {
    private String uuid;
    private String wagonUuid;
    private String wagonNumber;
    private String userUuid;
    private Date scanTime;

    public ScanHistory(String uuid, String wagonUuid, String wagonNumber, String userUuid, Date scanTime) {
        this.uuid = uuid;
        this.wagonUuid = wagonUuid;
        this.wagonNumber = wagonNumber;
        this.userUuid = userUuid;
        this.scanTime = scanTime;
    }

    // Геттеры и сеттеры
    public String getUuid() { return uuid; }
    public String getWagonUuid() { return wagonUuid; }
    public String getWagonNumber() { return wagonNumber; }
    public String getUserUuid() { return userUuid; }
    public Date getScanTime() { return scanTime; }
}