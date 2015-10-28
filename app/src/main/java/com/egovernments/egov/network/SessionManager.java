package com.egovernments.egov.network;


import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "CredentialsPref";

    public static final String IS_LOGGEDIN = "IsLoggedIn";

    public static final String KEY_PASSWORD = "password";

    public static final String KEY_USERNAME = "username";

    public static final String KEY_ACCESSTOKEN = "access_token";


    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
    }


    public void loginUser(String password, String email, String accessToken) {

        editor.putBoolean(IS_LOGGEDIN, true);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_USERNAME, email);
        editor.putString(KEY_ACCESSTOKEN, accessToken);

        editor.commit();
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGEDIN, false);
    }

    public String getPassword() {
        return pref.getString(KEY_PASSWORD, null);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getAccessToken() {
        String access_token = pref.getString(KEY_ACCESSTOKEN, null);
        if (access_token != null)
            return access_token.substring(1, access_token.length() - 1);
        return null;
    }

    public void invalidateAccessToken() {
        editor.putString(KEY_ACCESSTOKEN, null);
    }

}
