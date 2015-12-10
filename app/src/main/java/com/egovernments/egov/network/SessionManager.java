package com.egovernments.egov.network;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Stores session data to enable persistent logged in status and to enable seamless renewal of session in background without user input
 **/

public class SessionManager {

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private static final String BASE_URL = "Base URL";

    private static final String URL_CREATED_TIME = "Url timeout";

    private static final String URL_LOCATION = "Url location";

    private static final String URL_LOCATION_CODE = "Url location code";

    private static final String PREF_NAME = "CredentialsPref";

    public static final String IS_LOGGED_IN = "IsLoggedIn";

    public static final String KEY_PASSWORD = "password";

    public static final String KEY_USERNAME = "username";

    public static final String KEY_ACCESS_TOKEN = "access_token";


    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, 0);
    }


    //When the user is logged in, store data in shared preferences to be used across sessions
    public void loginUser(String password, String email, String accessToken) {

        editor = pref.edit();

        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_USERNAME, email);
        editor.putString(KEY_ACCESS_TOKEN, accessToken);

        editor.apply();
    }

    //Only when user explicitly logs out, clear all data from storage
    public void logoutUser() {

        editor = pref.edit();

        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_ACCESS_TOKEN);
        editor.putBoolean(IS_LOGGED_IN, false);

        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public String getPassword() {
        return pref.getString(KEY_PASSWORD, null);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getAccessToken() {
        String access_token = pref.getString(KEY_ACCESS_TOKEN, null);
        if (access_token != null)
            //To remove quotes from string
            return access_token.substring(1, access_token.length() - 1);
        return null;
    }

    public void invalidateAccessToken() {
        editor = pref.edit();
        editor.putString(KEY_ACCESS_TOKEN, null);
        editor.apply();
    }

    public void setBaseURL(String url, String location, int code) {

        editor = pref.edit();

        if (url.substring(url.length() - 1).equals("/"))
            editor.putString(BASE_URL, url.substring(0, url.length() - 1));
        else
            editor.putString(BASE_URL, url);

        editor.putInt(URL_CREATED_TIME, Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        editor.putString(URL_LOCATION, location);
        editor.putInt(URL_LOCATION_CODE, code);

        editor.apply();
    }

    public String getBaseURL() {
        return pref.getString(BASE_URL, null);
    }

    public int getUrlAge() {
        return Math.abs(Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - pref.getInt(URL_CREATED_TIME, 100));
    }

    public String getUrlLocation() {
        return pref.getString(URL_LOCATION, null);
    }

    public int getUrlLocationCode() {
        return pref.getInt(URL_LOCATION_CODE, 0);
    }


}
