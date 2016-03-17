package org.egovernments.egoverp.models;

/**
 * Created by egov on 8/3/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class District {

    @SerializedName("district_name")
    @Expose
    private String districtName;
    @SerializedName("cities")
    @Expose
    private List<City> cities = new ArrayList<>();

    /**
     *
     * @return
     *     The district
     */
    public String getDistrictName() {
        return districtName;
    }

    /**
     *
     * @param districtName
     *     The district
     */
    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    /**
     *
     * @return
     *     The cities
     */
    public List<City> getCities() {
        return cities;
    }

    /**
     *
     * @param cities
     *     The cities
     */
    public void setCities(List<City> cities) {
        this.cities = cities;
    }

}