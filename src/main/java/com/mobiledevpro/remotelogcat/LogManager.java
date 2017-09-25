package com.mobiledevpro.remotelogcat;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;


/**
 * Class for saving logs and sending them to server
 * <p>
 * Created by Dmitriy V. Chernysh on 23.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://fb.me/mobiledevpro/
 * <p>
 * #MobileDevPro
 */

class LogManager {

    private DBHelper mDBHelper;
    private UserInfoModel mUserInfo;
    private AppInfoModel mAppInfo;

    LogManager(Context appContext) {
        mDBHelper = DBHelper.getInstance(appContext);

        PackageManager manager = appContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(appContext.getPackageName(), 0);
            mAppInfo = new AppInfoModel(
                    info.packageName,
                    info.versionName,
                    info.versionCode
            );
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Constants.LOG_TAG, "RemoteLog.init: NameNotFoundException - " + e.getLocalizedMessage(), e);
        }
    }

    void setUserInfo(UserInfoModel userInfo) {
        mUserInfo = userInfo;
    }

    void send(int logLevel, String logTag, String logMessage, Throwable tr) {
        switch (logLevel) {
            case Constants.LOG_LEVEL_DEBUG:
                if (tr == null) {
                    Log.d(logTag, logMessage);
                } else {
                    Log.d(logTag, logMessage, tr);
                }
                break;
            case Constants.LOG_LEVEL_ERROR:
                if (tr == null) {
                    Log.e(logTag, logMessage);
                } else {
                    Log.e(logTag, logMessage, tr);
                }
                break;
        }

        //create model
        LogEntryModel logEntryModel = createLogEntry(logLevel, logTag, logMessage);
        //save into db
        ArrayList<LogEntryModel> logEntriesList = insertEntryIntoDb(logEntryModel);
        //send to server
        if (logEntriesList != null && !logEntriesList.isEmpty()) {
            sendEntriesToServer(logEntriesList);
        }

        // TODO: 23.09.17 save data to sqllite and send to server if there is network connection
    }

    private LogEntryModel createLogEntry(int logLevel, String logTag, String logMessage) {
        if (mUserInfo == null) mUserInfo = new UserInfoModel();
        return new LogEntryModel(
                new Date().getTime(),
                logLevel,
                logTag,
                logMessage,
                mAppInfo,
                mUserInfo
        );
    }

    private ArrayList<LogEntryModel> insertEntryIntoDb(LogEntryModel logEntryModel) {
        // TODO: 23.09.17 need to mplement asynctask for inserting
        mDBHelper.insertLogEntry(logEntryModel);
        return mDBHelper.selectLogEntriesList();
    }

    private static void sendEntriesToServer(ArrayList<LogEntryModel> logEntriesList) {
        // TODO: 23.09.17  
    }

}
