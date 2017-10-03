package com.mobiledevpro.remotelogcat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class for data sending to server
 * <p>
 * Created by Dmitriy V. Chernysh on 23.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://fb.me/mobiledevpro/
 * <p>
 * #MobileDevPro
 */

class NetworkHelper extends AsyncTask<Void, Void, Boolean> {

    private static final String URL = "http://api.mobile-dev.pro/applog/api";
    private static final String METHOD = "POST";
    private static final int TIMEOUT = 5000; //ms

    private Context mContext;
    private String mToken;
    private ArrayList<LogEntryModel> mLogEntriesList;

    NetworkHelper(Context context, String token, ArrayList<LogEntryModel> logEntriesList) {
        mContext = context;
        mToken = token;
        mLogEntriesList = logEntriesList;
    }

    /**
     * Method for checking network connection
     *
     * @param context - application context
     * @return true - device online
     */
    private boolean isDeviceOnline(Context context) {
        ConnectivityManager connMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMngr.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d(Constants.LOG_TAG, "NetworkHelper.doInBackground(): ");
        if (!isDeviceOnline(mContext)) return false;
        if (mLogEntriesList == null || mLogEntriesList.isEmpty()) return false;

        //create json body
        JSONArray jsonArray = createRequestBody();
        if (jsonArray == null || jsonArray.length() == 0) return false;

        //send entries to server
        return sendRequest(jsonArray);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Log.d(Constants.LOG_TAG, "NetworkHelper.onPostExecute(): aBoolean - " + aBoolean);
        if (aBoolean) {
            //remove entries from the local database
            int[] ids = new int[mLogEntriesList.size()];
            for (int i = 0, j = mLogEntriesList.size(); i < j; i++) {
                ids[i] = mLogEntriesList.get(i).getId();
            }
            DBHelper.getInstance(mContext).deleteLogEntryList(ids);
        }
    }

    private JSONArray createRequestBody() {
        JSONArray jsonArray = new JSONArray();

        JSONObject jsonEntry;
        JSONObject jsonAppData;
        JSONObject jsonAppUserData;
        AppInfoModel appInfo;
        UserInfoModel appUserInfo;
        try {
            for (LogEntryModel logEntry : mLogEntriesList) {
                jsonEntry = new JSONObject();
                jsonEntry.put("datetime", logEntry.getDateTimeTxt());
                jsonEntry.put("loglevel", logEntry.getLogLevelTxt());
                jsonEntry.put("logtag", logEntry.getLogTag());
                jsonEntry.put("error", logEntry.getLogMsg());

                //app info
                jsonAppData = new JSONObject();
                appInfo = logEntry.getAppInfo();
                jsonAppData.put("name", appInfo != null ? appInfo.getName() : "");
                jsonAppData.put("version", appInfo != null ? appInfo.getVersion() + " (" + appInfo.getBuild() + ")" : "");
                jsonEntry.put("app", jsonAppData);

                //app user's info
                jsonAppUserData = new JSONObject();
                appUserInfo = logEntry.getAppUserInfo();
                jsonAppUserData.put("androidApi", appUserInfo.getAndroidApiTxt());
                jsonAppUserData.put("device", appUserInfo.getDeviceModel());
                jsonAppUserData.put("login", appUserInfo.getUserName());
                jsonEntry.put("appUser", jsonAppUserData);

                jsonArray.put(jsonEntry);
            }
        } catch (JSONException e) {
            Log.e(Constants.LOG_TAG, "NetworkHelper.createRequestBody: JSONException - " + e.getLocalizedMessage(), e);
        }

        return jsonArray;
    }

    private boolean sendRequest(JSONArray jsonArray) {
        Log.d(Constants.LOG_TAG, "NetworkHelper.sendRequest(): ");
        try {
            URL url = new URL(URL + "?token=" + mToken);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT);
            urlConnection.setReadTimeout(TIMEOUT);
            urlConnection.setRequestMethod(METHOD);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.connect();

            //write request
            OutputStreamWriter outputWriter = new OutputStreamWriter(urlConnection.getOutputStream());
            outputWriter.write(jsonArray.toString());
            Log.d(Constants.LOG_TAG, "NetworkHelper.sendRequest(): " + jsonArray.toString());
            outputWriter.close();

            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

            //get response
            int responseCode = urlConnection.getResponseCode();

            Log.d(Constants.LOG_TAG, "NetworkHelper.sendRequest(): resp code - " + responseCode);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

            Log.d(Constants.LOG_TAG, "NetworkHelper.sendRequest(): response: " + new String(baos.toByteArray()));

            urlConnection.disconnect();

            return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_ACCEPTED;
        } catch (MalformedURLException me) {
            Log.e(Constants.LOG_TAG, "NetworkHelper.sendRequest: MalformedInputException - " + me.getLocalizedMessage(), me);
        } catch (IOException ie) {
            Log.e(Constants.LOG_TAG, "NetworkHelper.sendRequest: IOException - " + ie.getLocalizedMessage(), ie);
        }
        return false;
    }
}
