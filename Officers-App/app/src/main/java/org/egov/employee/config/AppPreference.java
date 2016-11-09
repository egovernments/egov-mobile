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
    private final String activeCityUrl="activeCityUrl";
    //This is hold current user logged city server url
    private final String activeCityName="activeCityName";
    //if app has multicity support it will hold active city code for url update functionality
    private final String activeCityCode="activeCityCode";
    //This is used to store time of when city url updated (refresh functionality)
    private final String lastUrlUpdateTime="lasturlupdatetime";
    //This is used to store multiple cities details
    private final String citiesList ="citiesList";
    //This is used to store API Access Token
    private final String apiAccessToken = "apiAccessToken";
    //This is used to store current logged user name
    private final String userName = "userName";
    //This is used to store current logged user pwd
    private final String pwd="pwd";
    //This is used to store name of current logged user
    private final String name="name";
    //This is used to save selected district text
    private final String district="district";

    private final String activeCityLat="activeCityLat";
    //This is used to save selected district text
    private final String activeCityLng="activeCityLng";


    public AppPreference(Context context){
        this.appSharedPrefs = context.getSharedPreferences(APP_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public String getActiveCityUrl() {
        return appSharedPrefs.getString(activeCityUrl, "");
    }

    public void setActiveCityUrl(String _activeCityUrl) {
        //check whether url is properly ended or not
        if(!_activeCityUrl.endsWith("/"))
        {
            _activeCityUrl=_activeCityUrl+"/";
        }
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

    public Double getActiveCityLat() {
        return Double.valueOf(appSharedPrefs.getString(activeCityLat, "0"));
    }

    public void setActiveCityLat(Double _cityLat)
    {
        prefsEditor.putString(activeCityLat, String.valueOf(_cityLat)).commit();
    }

    public Double getActiveCityLng() {
        return Double.valueOf(appSharedPrefs.getString(activeCityLng, "0"));
    }

    public void setActiveCityLng(Double _cityLng)
    {
        prefsEditor.putString(activeCityLng, String.valueOf(_cityLng)).commit();
    }

}
