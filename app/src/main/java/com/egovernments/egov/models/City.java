
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class City {

    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("city_code")
    @Expose
    private int cityCode;

    /**
     * @return The cityName
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    public int getCityCode() {
        return cityCode;
    }
}
