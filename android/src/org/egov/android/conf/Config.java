/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.egov.android.R;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This is to read the egov.conf file. Get the values like database name, database version, cache
 * duration and date format If the value is assigned in the assets/egov.conf file then it will take
 * that value. If not then it will take the default value from here.
 */
public class Config {

    Properties property = null;
    Context appContext=null;

    public Config(InputStream inputStream, Context appContext) {

        try {
            property = new Properties();
            property.load(inputStream);
            inputStream.close();
            this.appContext = appContext;
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }

    public String getDatabaseName() {
        return property.getProperty("db.name");
    }

    public int getDatabaseVersion() {
        return Integer.valueOf(property.getProperty("db.version", "1"));
    }

    public long getCacheDuration() {
        return Integer.valueOf(property.getProperty("cache.duration", "300"));
    }

    public String getDateFormat() {
        return property.getProperty("date.format", "yyyy-MM-dd HH:mm:ss");
    }

    public String getPasswordLevel()
    {
    	return property.getProperty("app.pwd.level","LOW");
    }
    
    public Object get(String key, Object defaultValue) {
        return (property.get(key) == null) ? defaultValue : property.get(key);
    }

    public String getString(String key) {
    	if(key.equals("api.baseUrl"))
    	{
    		SharedPreferences pref = appContext.getSharedPreferences(appContext.getString(R.string.app_name), 0);
    		String value=pref.getString(key, null);
    		if(value!=null)
    		{
    			return value;
    		}
    	}
        return this.get(key, "").toString();
    }

    public int getInt(String key) {
        return Integer.valueOf(this.get(key, 0).toString());
    }

    public float getFloat(String key) {
        return Float.valueOf(this.get(key, 0).toString());
    }

}
