package org.egov.employee.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egov on 25/4/16.
 */
public class MultiDistrictsAPIResponse {

    @SerializedName("district_name")
    private String districtName;
    @SerializedName("cities")
    private List<City> cities = new ArrayList<City>();

    /**
     *
     * @return
     * The districtName
     */
    public String getDistrictName() {
        return districtName;
    }

    /**
     *
     * @param districtName
     * The district_name
     */
    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    /**
     *
     * @return
     * The cities
     */
    public List<City> getCities() {
        return cities;
    }

    /**
     *
     * @param cities
     * The cities
     */
    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public class City {

        @SerializedName("url")
        private String url;
        @SerializedName("city_name")
        private String cityName;
        @SerializedName("city_code")
        private Integer cityCode;

        /**
         * @return The url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url The url
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * @return The cityName
         */
        public String getCityName() {
            return cityName;
        }

        /**
         * @param cityName The city_name
         */
        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        /**
         * @return The cityCode
         */
        public Integer getCityCode() {
            return cityCode;
        }

        /**
         * @param cityCode The city_code
         */
        public void setCityCode(Integer cityCode) {
            this.cityCode = cityCode;
        }
    }

}
