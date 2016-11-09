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

package org.egov.employee.application;

import android.app.Application;
import android.content.Context;

import org.egov.employee.config.AppConfigReader;

import java.util.Properties;

/**
 * Created by egov on 21/1/16.
 */
public class EgovApp extends Application {

    private static EgovApp appinstance;
    private Context context;
    private static Properties appConfig;

    private static final String KEY_MULTICITY="app.type.ismulticity";
    private static final String KEY_URL_RESOURCE="app.resource.serverurl";
    private static final String KEY_URL_TIMEOUT="app.refresh.serverurl.days";
    private static final String KEY_APP_VERSION_CHECK="app.version.check";

    @Override
    public void onCreate() {
        super.onCreate();
        this.context= getApplicationContext();
        initializeInstance();
    }

    public void initializeInstance()
    {
        if(appinstance==null)
        {
            appinstance=new EgovApp();
            AppConfigReader appConfigReader = new AppConfigReader(context);
            appConfig = appConfigReader.getProperties("app.conf");
        }
    }

    public static EgovApp getInstance()
    {
        return appinstance;
    }

    public Boolean isMultiCitySupport()
    {
        return Boolean.valueOf(appConfig.get(KEY_MULTICITY).toString());
    }

    public String getCityResourceUrl()
    {
        return appConfig.get(KEY_URL_RESOURCE).toString();
    }

    public Integer getUrlTimeOutDays()
    {
        return Integer.valueOf(appConfig.get(KEY_URL_TIMEOUT).toString());
    }

    public String getAppVersionCheckApiUrl()
    {
        return appConfig.get(KEY_APP_VERSION_CHECK).toString();
    }

}
