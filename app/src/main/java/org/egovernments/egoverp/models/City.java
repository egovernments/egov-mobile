
package org.egovernments.egoverp.models;

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
        if(url!=null)
        {
            return url;
        }
        return "";
    }

    public int getCityCode() {
        return cityCode;
    }
}
