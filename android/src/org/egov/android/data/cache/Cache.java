/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.data.cache;

import java.util.Calendar;
import java.util.Map;

import org.egov.android.AndroidLibrary;
import org.egov.android.api.ApiResponse;
import org.egov.android.data.SQLiteHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

public class Cache {

    private String url = "";

    private Map<String, String> params;

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Function used to get data from cache table. If the cache table has an entry for the
     * particular url then get the data from the cache table.
     * 
     * @return
     */
    public String getData() {
        String data = "";
        JSONObject jo = (JSONObject) get();
        try {
            data = jo.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * If any api call has cache, then save the data which comes in success response. Before saving the data
     * we have to check whether the cache is expired or not. If the cache is expired, remove the data
     * from cache and save the new data.
     * 
     * @param data
     * @return
     */
    public int add(Object data) {
        if (hasExpired()) {
            remove();
        }
        insert(data);
        return 0;
    }

    /**
     * Function used to add an entry in cache table for the particular url, data and timeStamp. The
     * data field holds the success response data. The timeStamp field holds the current
     * time in milliseconds. This field is used to check whether the cache is expired or not.
     * 
     * @param data
     */
    protected void insert(Object data) {
        ApiResponse response = (ApiResponse) data;
        ContentValues cv = new ContentValues();
        cv.put("url", this.url);
        cv.put("data", response.getResponse().toString());
        cv.put("timeStamp", Long.toString(Calendar.getInstance().getTimeInMillis()));
        SQLiteHelper.getInstance().insert("tbl_cache", cv);
    }

    /**
     * Function is used to remove an entry having specific url. This function is called when cache
     * is expired.
     */
    public void remove() {
        SQLiteHelper.getInstance().delete("tbl_cache", "url=?", new String[] { this.url });
    }

    /**
     * Function is used to get the entry having particular url. By using this entry, we get the
     * data saved in cache.
     * 
     * @return
     */
    public Object get() {
        JSONObject cacheObj = null;
        JSONArray arr = SQLiteHelper.getInstance().query(
                "SELECT * FROM tbl_cache WHERE url = '" + url + "'");
        if (arr != null) {
            try {
                JSONObject jo = arr.getJSONObject(0);
                cacheObj = jo;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return cacheObj;
    }

    /**
     * Function is used to check whether the cache is expired or not, by comparing the timeStamp and
     * current time. Cache expire time set to 24 hours. If the time difference between timeStamp and
     * current time is greater than 24 hours, then the cache data is expired.
     * 
     * @return
     */
    public boolean hasExpired() {
        JSONObject cacheObj = (JSONObject) get();
        if (cacheObj == null) {
            return true;
        }
        int limit = (int) AndroidLibrary.getInstance().getConfig().getCacheDuration();
        try {
            Long d = (Calendar.getInstance().getTimeInMillis() - Long.valueOf(cacheObj
                    .getLong("timeStamp"))) / 1000;
            return d > limit;
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }

    /**
     * Function used to check whether the particular url has data in cache table or not.
     * 
     * @return
     */
    public boolean hasData() {
        JSONObject cacheObj = (JSONObject) get();
        if (cacheObj == null || hasExpired())
            return false;
        return true;
    }
}
