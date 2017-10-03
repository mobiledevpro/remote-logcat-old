package com.mobiledevpro.remotelogcat;

/**
 * Class for storing constant values
 * <p>
 * Created by Dmitriy V. Chernysh on 23.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://fb.me/mobiledevpro/
 * <p>
 * #MobileDevPro
 */

class Constants {
    static final int LOG_LEVEL_DEBUG = 1;
    static final int LOG_LEVEL_ERROR = 2;
    static final String LOG_TAG = "remote-logcat";

    private static final String LOG_LEVEL_DEBUG_TXT = "debug";
    private static final String LOG_LEVEL_ERROR_TXT = "error";

    static String getLogLevelTxt(int logLevel) {
        switch (logLevel) {
            case LOG_LEVEL_DEBUG:
                return LOG_LEVEL_DEBUG_TXT;
            case LOG_LEVEL_ERROR:
                return LOG_LEVEL_ERROR_TXT;
            default:
                return String.valueOf(logLevel);
        }
    }
}
