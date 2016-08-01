/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egovernments.egoverp.network;


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

    public static final String KEY_NAME = "name";

    public static final String KEY_MOBILE = "mobile";

    public static final String KEY_EMAIL = "email";

    public static final String KEY_ACCESS_TOKEN = "access_token";

    public static final String KEY_DEMO_MODE = "demoMode";

    public static final String KEY_CITY_LAT = "cityLatitude";
    public static final String KEY_CITY_LNG = "cityLongitude";

    public static final String KEY_DEBUG_LOG="isLogEnabled";

    public static final String KEY_TERMS_AGREED="isTermsAgreed";

    public static final String KEY_PROFILE_NOTIFY_DIMISSED="isProfileDismissed";

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

    public void loginUser(String username, String password, String name, String mobileno, String email, String accessToken, double cityLat, double cityLng) {

        editor = pref.edit();
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_MOBILE, mobileno);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_CITY_LAT, String.valueOf(cityLat));
        editor.putString(KEY_CITY_LNG, String.valueOf(cityLng));
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
        return access_token;
    }

    public void invalidateAccessToken() {
        editor = pref.edit();
        editor.putString(KEY_ACCESS_TOKEN, null);
        editor.apply();
    }

    public void setBaseURL(String url, String location, int code) {

        if(url!=null)
        {
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

    public double getCityLatitude() {
        return Double.parseDouble(pref.getString(KEY_CITY_LAT, "0"));
    }

    public double getCityLongitude() {
        return Double.parseDouble(pref.getString(KEY_CITY_LNG, "0"));
    }

    public void setDemoMode(boolean isEnabled)
    {
        editor = pref.edit();
        editor.putBoolean(KEY_DEMO_MODE, isEnabled);
        editor.apply();
    }

    public boolean isDemoMode() {
        return pref.getBoolean(KEY_DEMO_MODE, false);
    }

    public void setProfileNotifyDimissed(boolean isAgreed)
    {
        editor = pref.edit();
        editor.putBoolean(KEY_PROFILE_NOTIFY_DIMISSED, isAgreed);
        editor.apply();
    }

    public boolean isProfileNotifyDismissed()
    {
        return pref.getBoolean(KEY_PROFILE_NOTIFY_DIMISSED, false);
    }

    public void setTermsAgreed(boolean isAgreed)
    {
        editor = pref.edit();
        editor.putBoolean(KEY_TERMS_AGREED, isAgreed);
        editor.apply();
    }

    public boolean isTermsAgreed()
    {
        return pref.getBoolean(KEY_TERMS_AGREED, false);
    }


    public void setKeyDebugLog(boolean isEnabled)
    {
        editor = pref.edit();
        editor.putBoolean(KEY_DEBUG_LOG, isEnabled);
        editor.apply();
    }

    public boolean getKeyDebugLog() {
        return pref.getBoolean(KEY_DEBUG_LOG, false);
    }

    public void setName(String name)
    {
        editor = pref.edit();
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    public String getName() {
        return pref.getString(KEY_NAME, "UNKOWN");
    }

    public void setMobileNo(String mobileNo)
    {
        editor = pref.edit();
        editor.putString(KEY_MOBILE, mobileNo);
        editor.apply();
    }

    public String getMobile() {
        return pref.getString(KEY_MOBILE, "----");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "----");
    }
}
