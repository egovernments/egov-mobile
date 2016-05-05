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
