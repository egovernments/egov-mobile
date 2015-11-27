
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

    /**
     * @return The cityName
     */
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

}
