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

package org.egov.android.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

public class ApiResponse {

    private static final String TAG = ApiResponse.class.getName();

    private ApiMethod apiMethod = null;
    private ApiStatus apiStatus = null;
    private Object response = null;
    private boolean hasData = true;

    /**
     * Constructor used to create api response send to the activity
     * 
     * @param response
     * @param apiMethod
     * @param source
     */
    public ApiResponse(String response, ApiMethod apiMethod, String source) {

        this.apiMethod = apiMethod;
        this.apiStatus = new ApiStatus();
        this.response = response;

        if (source.equals("cache")) {
            this.response = response.toString();
            this.apiStatus = new ApiStatus("success", "", "");
            return;
        }

        if (ApiStatus.isError) {
            try {
                new JSONObject(response.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                this.response = response.toString();
                this.apiStatus = new ApiStatus("error",
                        "Internet connection problem/Server problem", "");
                return;
            }
        }

        if (TextUtils.isEmpty(response)) {
            this.hasData = false;
            return;
        }

        try {
            JSONObject content = new JSONObject(response.toString());
            if (content.has("status")) {
                String pagination = "";
                JSONObject status = content.optJSONObject("status");
                if (status.getString("type").equals(ApiStatus.SUCCESS)) {
                    this.response = content.optString("result");
                    if (this.response == null) {
                        Log.d(TAG, "const - result error");
                        hasData = false;
                    }
                }

                if (status.has("hasNextPage")) {
                    pagination = status.getString("hasNextPage");
                }
                this.apiStatus = new ApiStatus(status.getString("type"),
                        status.getString("message"), pagination);
            } else {
                String type = "", message = "";

                JSONObject result = new JSONObject();

                if (content.has("error")) {
                    type = "error";
                    message = content.getString("error_description");
                } else {
                    type = "success";
                    result.put("access_token", content.getString("access_token"));
                    result.put("user_name", content.getString("name"));
                }

                JSONObject oauthResp = new JSONObject();

                JSONObject status = new JSONObject();
                status.put("message", message);
                status.put("type", type);

                JSONArray resultArray = new JSONArray();
                resultArray.put(result);
                oauthResp.put("result", resultArray);
                oauthResp.put("status", status);
                this.response = oauthResp.optString("result");
                this.apiStatus = new ApiStatus(type, message, "");
            }
        } catch (JSONException ex) {
            this.hasData = false;
            this.apiStatus = new ApiStatus("error", ex.getMessage(), "");
        }
    }

    public ApiStatus getApiStatus() {
        return this.apiStatus;
    }

    public boolean hasData() {
        return hasData;
    }

    public Object getResponse() {
        return this.response;
    }

    public ApiMethod getApiMethod() {
        return this.apiMethod;
    }

}
