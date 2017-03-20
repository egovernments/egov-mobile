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

package org.egovernments.egoverp.config;

/**
 * Created by egov on 17/11/16.
 */

public class Config {

    public static final String APP_LOCALES = "app.locales";
    public static final String API_MULTICITIES = "api.multicities";
    public static final String APP_DEBUG_LOG = "app.debug.log";
    public static final String APP_TIMEOUTDAYS = "app.timeoutdays";
    public static final String API_APP_VERSION_CHECK = "api.appVersionCheck";
    public static final String API_CITY_URL = "api.cityUrl";
    public static final String APP_PASSWORD_LEVEL = "app.passwordLevel";
    public static final String API_MULTIPLE_CITIES_URL = "api.multipleCitiesUrl";
    public static final String REFERER_IP_CONFIG_KEY="app.referrer.ip";
    public static final String APP_PAYMENT_GATEWAY_VACANTLAND_TAX = "app.payment.gateway.vacantland.tax";
    public static final String APP_PAYMENT_GATEWAY_PROPERTY_TAX = "app.payment.gateway.property.tax";
    public static final String APP_PAYMENT_GATEWAY_WATER_TAX = "app.payment.gateway.water.tax";
    public final static String ACCOUNT_RECOVERY_MESSAGE = "Your OTP for recovering password is";
    public final static String ACCOUNT_VERIFICATION_MESSAGE = "Use OTP";
    public final static String APP_TERMS_AND_CONS_URL = "app.termsAndConditionUrl";
    public final static String APP_SLA_DOCUMENT_URL = "app.sla.document";

    public class Modules
    {
        public static final String ABOUT_US="app.module.aboutus";
        public static final String SLA="app.module.sla";
        public static final String SOS="app.module.sos";
        public static final String CITIZEN_CHARTER="app.module.citizencharter";
        public static final String BPS="app.module.buildingpenalization";
        public static final String BPA="app.module.buildingplanapproval";
        public static final String WATER_CHARGE="app.module.watertax";
        public static final String VACANT_LAND_TAX ="app.module.vacantlandtax";
        public static final String PROPERTY_TAX ="app.module.propertytax";
        public static final String PGR="app.module.pgr";
    }

}
