package org.egov.android.api;

import java.text.SimpleDateFormat;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.egov.android.model.IModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ApiResponse {

    private static final String TAG = ApiResponse.class.getName();

    private ApiMethod apiMethod = null;
    private ApiStatus apiStatus = null;
    private Object response = null;
    private boolean hasData = true;

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

        if (response.isEmpty()) {
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
                String type = "", message = "", value = "";

                JSONObject result = new JSONObject();

                if (content.has("error")) {
                    type = "error";
                    message = content.getString("error_description");
                } else {
                    type = "success";
                    value = content.getString("access_token");
                    result.put("access_token", value);
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

    public <T extends IModel> List<T> parse(Class<T> clazz) {
        if (this.response == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        List<T> result = null;
        try {
            result = mapper.readValue(this.response.toString(), mapper.getTypeFactory()
                    .constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ApiMethod getApiMethod() {
        return this.apiMethod;
    }

}
