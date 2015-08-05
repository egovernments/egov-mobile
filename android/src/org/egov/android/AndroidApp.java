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

package org.egov.android;

import java.io.IOException;
import java.io.InputStream;

import org.egov.android.common.ReflectionUtil;
import org.egov.android.conf.Config;
import org.egov.android.data.SQLiteHelper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Here the egov.conf file will be read and stored in the session We can get the egov.conf file
 * details through out the app
 */

public class AndroidApp extends Application {

    private Config config = null;
    private SharedPreferences session = null;

    public void configure(String configFile) {
        try {
            InputStream is = getAssets().open(configFile);
            config = new Config(is);
            is.close();
            ReflectionUtil.setFieldData(AndroidLibrary.getInstance(), "config", config);
            ReflectionUtil.setFieldData(AndroidLibrary.getInstance(), "session",
                    getSharedPreferences(config.getString("app.name"), Context.MODE_PRIVATE));
            SQLiteHelper.newInstance(getApplicationContext()).initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        session = getSharedPreferences(config.getString("app.name"), Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.configure("alib.conf");
    }

    public Config getConfig() {
        return this.config;
    }

    public SharedPreferences getSession() {
        return this.session;
    }
}
