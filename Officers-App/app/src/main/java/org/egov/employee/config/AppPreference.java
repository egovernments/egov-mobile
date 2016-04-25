package org.egov.employee.config;

/**
 * Created by egov on 21/1/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
    private static final String APP_PREFS = "APP_PREFS";
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    //This is hold current user logged city server url
    private String activeCityUrl="activeCityUrl";
    //This is hold current user logged city server url
    private String activeCityName="activeCityName";
    //if app has multicity support it will hold active city code for url update functionality
    private String activeCityCode="activeCityCode";
    //This is used to store time of when city url updated (refresh functionality)
    private String lastUrlUpdateTime="lasturlupdatetime";
    //This is used to store multiple cities details
    private String citiesList ="citiesList";
    //This is used to store API Access Token
    private String apiAccessToken = "apiAccessToken";
    //This is used to store current logged user name
    private String userName = "userName";
    //This is used to store current logged user pwd
    private String pwd="pwd";
    //This is used to store name of current logged user
    private String name="name";
    //This is used to save selected district text
    private String district="district";

    public AppPreference(Context context){
        this.appSharedPrefs = context.getSharedPreferences(APP_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public String getActiveCityUrl() {
        return appSharedPrefs.getString(activeCityUrl, "");
    }

    public void setActiveCityUrl(String _activeCityUrl) {
        prefsEditor.putString(activeCityUrl, _activeCityUrl).commit();
    }

    public String getActiveCityName() {
        return appSharedPrefs.getString(activeCityName, "");
    }

    public void setActiveCityName(String _activeCityName) {
        prefsEditor.putString(activeCityName, _activeCityName).commit();
    }

    public Integer getActiveCityCode() {
        return appSharedPrefs.getInt(activeCityCode, -1);
    }

    public void setActiveCityCode(Integer _activeCityCode) {
        prefsEditor.putInt(activeCityCode, _activeCityCode).commit();
    }

    public Long getLastUrlUpdateTime() {
        return appSharedPrefs.getLong(lastUrlUpdateTime, 0);
    }

    public void setLastUrlUpdateTime(Long _lastUrlUpdateTime) {
        prefsEditor.putLong(lastUrlUpdateTime, _lastUrlUpdateTime).commit();
    }

    public String getCitiesList() {
        return appSharedPrefs.getString(citiesList, "");
    }

    public void setCitiesList(String _citiesList) {
        prefsEditor.putString(citiesList, _citiesList).commit();
    }

    public String getApiAccessToken() {
        return appSharedPrefs.getString(apiAccessToken, "");
    }

    public void setApiAccessToken(String _apiToken) {
        prefsEditor.putString(apiAccessToken, _apiToken).commit();
    }

    public String getUserName() {
        return appSharedPrefs.getString(userName, "");
    }

    public void setUserName(String _userName) {
        prefsEditor.putString(userName, _userName).commit();
    }

    public String getPwd() {
        return appSharedPrefs.getString(pwd, "");
    }

    public void setPwd(String _pwd) {
        prefsEditor.putString(pwd, _pwd).commit();
    }

    public String getName() {
        return appSharedPrefs.getString(name, "");
    }

    public void setName(String _name) {
        prefsEditor.putString(name, _name).commit();
    }

    public String getDistrict() {
        return appSharedPrefs.getString(district, "");
    }

    public void setDistrict(String _district) {
        prefsEditor.putString(district, _district).commit();
    }
}
