
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

package org.egovernments.egoverp.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

import static org.egovernments.egoverp.config.Config.Modules.ABOUT_US;
import static org.egovernments.egoverp.config.Config.Modules.BPA;
import static org.egovernments.egoverp.config.Config.Modules.BPS;
import static org.egovernments.egoverp.config.Config.Modules.CITIZEN_CHARTER;
import static org.egovernments.egoverp.config.Config.Modules.PGR;
import static org.egovernments.egoverp.config.Config.Modules.PROPERTY_TAX;
import static org.egovernments.egoverp.config.Config.Modules.SLA;
import static org.egovernments.egoverp.config.Config.Modules.SOS;
import static org.egovernments.egoverp.config.Config.Modules.VACANT_LAND_TAX;
import static org.egovernments.egoverp.config.Config.Modules.WATER_CHARGE;

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
    @SerializedName("non_active_modules")
    @Expose
    private Modules modules;
    @SerializedName("locale")
    @Expose
    private HashMap<String, String> locale = new HashMap<>();
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

    public Modules getModules() {
        return modules;
    }

    public HashMap<String, String> getLocale() {
        return locale;
    }

    public class Modules{

        @SerializedName(PGR)
        @Expose
        private boolean isPgrDisable;

        @SerializedName(PROPERTY_TAX)
        @Expose
        private boolean isPropertyTaxDisable;

        @SerializedName(VACANT_LAND_TAX)
        @Expose
        private boolean isVacantLandTaxDisable;

        @SerializedName(WATER_CHARGE)
        @Expose
        private boolean isWaterChargeDisable;

        @SerializedName(BPA)
        @Expose
        private boolean isBPADisable;

        @SerializedName(BPS)
        @Expose
        private boolean isBPSDisable;

        @SerializedName(CITIZEN_CHARTER)
        @Expose
        private boolean isCitizenCharterDisable;

        @SerializedName(SOS)
        @Expose
        private boolean isSOSDisable;

        @SerializedName(SLA)
        @Expose
        private boolean isSLADisable;

        @SerializedName(ABOUT_US)
        @Expose
        private boolean isAboutUsDisable;

        public Modules(){

        }

        public boolean isPgrDisable() {
            return isPgrDisable;
        }

        public boolean isPropertyTaxDisable() {
            return isPropertyTaxDisable;
        }

        public boolean isVacantLandTaxDisable() {
            return isVacantLandTaxDisable;
        }

        public boolean isWaterChargeDisable() {
            return isWaterChargeDisable;
        }

        public boolean isBPADisable() {
            return isBPADisable;
        }

        public boolean isBPSDisable() {
            return isBPSDisable;
        }

        public boolean isCitizenCharterDisable() {
            return isCitizenCharterDisable;
        }

        public boolean isSOSDisable() {
            return isSOSDisable;
        }

        public boolean isSLADisable() {
            return isSLADisable;
        }

        public boolean isAboutUsDisable() {
            return isAboutUsDisable;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
