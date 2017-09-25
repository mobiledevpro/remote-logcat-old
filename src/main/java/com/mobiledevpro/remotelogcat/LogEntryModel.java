package com.mobiledevpro.remotelogcat;

/**
 * Model for log entry
 * <p>
 * Created by Dmitriy V. Chernysh on 23.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://fb.me/mobiledevpro/
 * <p>
 * #MobileDevPro
 */

class LogEntryModel {

    private int id;
    private long dateTime; //in ms
    private int logLevel;
    private String logTag;
    private String logMsg;
    private AppInfoModel appInfo;
    private UserInfoModel appUserInfo;

    /**
     * Constructor for getting entry from DB
     */
    LogEntryModel(int id, long dateTime, int logLevel, String logTag, String logMsg, AppInfoModel appInfo, UserInfoModel appUserInfo) {
        this.id = id;
        this.dateTime = dateTime;
        this.logLevel = logLevel;
        this.logTag = logTag;
        this.logMsg = logMsg;
        this.appInfo = appInfo;
        this.appUserInfo = appUserInfo;
    }

    /**
     * Constructor for creating a new entry
     */
    LogEntryModel(long dateTime, int logLevel, String logTag, String logMsg, AppInfoModel appInfo, UserInfoModel appUserInfo) {
        this.dateTime = dateTime;
        this.logLevel = logLevel;
        this.logTag = logTag;
        this.logMsg = logMsg;
        this.appInfo = appInfo;
        this.appUserInfo = appUserInfo;
    }

    public int getId() {
        return id;
    }

    long getDateTime() {
        return dateTime;
    }

    int getLogLevel() {
        return logLevel;
    }

    String getLogTag() {
        return logTag;
    }

    String getLogMsg() {
        return logMsg;
    }

    AppInfoModel getAppInfo() {
        return appInfo;
    }

    UserInfoModel getAppUserInfo() {
        return appUserInfo;
    }

}
