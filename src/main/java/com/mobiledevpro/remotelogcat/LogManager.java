package com.mobiledevpro.remotelogcat;

import android.util.Log;

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

    static final int LOG_DEBUG = 1;
    static final int LOG_ERROR = 2;

    private LogManager() {
    }

    static void send(int logLevel, String logTag, String logMessage, Throwable tr) {
        switch (logLevel) {
            case LOG_DEBUG:
                if (tr == null) {
                    Log.d(logTag, logMessage);
                } else {
                    Log.d(logTag, logMessage, tr);
                }
                break;
            case LOG_ERROR:
                if (tr == null) {
                    Log.e(logTag, logMessage);
                } else {
                    Log.e(logTag, logMessage, tr);
                }
                break;
        }

        // TODO: 23.09.17 save data to sqllite and send to server if there is network connection
    }

}
