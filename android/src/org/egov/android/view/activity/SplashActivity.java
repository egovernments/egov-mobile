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

package org.egov.android.view.activity;

import org.egov.android.AndroidLibrary;
import org.egov.android.R;
import org.egov.android.data.SQLiteHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends BaseActivity implements Runnable {

	
	
    /**
     * To set the layout for the SplashActivity this screen appears for 2000 milliseconds. Create a
     * table named 'jobs' to handle upload and download jobs.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        SharedPreferences sharedPreference = getApplicationContext().getSharedPreferences("eGovPreference", 0);
		String baseServerURL = sharedPreference.getString("api.baseUrl", null);
		
		Log.v("org.egov.android", "Is Multicity support? "+ AndroidLibrary.getInstance().getConfig().get("api.multicities", true));
		
		if(baseServerURL == null && 
			AndroidLibrary.getInstance().getConfig().get("api.multicities", "true").equals("false"))
		{
			Log.v("org.egov.android", "CONDITION WORKED :)!");
			Editor editor = sharedPreference.edit();
        	editor.putString("api.baseUrl", (String)AndroidLibrary.getInstance().getConfig().get("api.baseUrl", ""));
        	editor.commit();
		}
		
        
        SQLiteHelper
                .getInstance()
                .execSQL(
                        "CREATE TABLE IF NOT EXISTS tbl_cache (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, url TEXT, data TEXT, timeStamp TEXT, UNIQUE(data) ON CONFLICT REPLACE)");
        SQLiteHelper
                .getInstance()
                .execSQL(
                        "CREATE TABLE IF NOT EXISTS tbl_jobs (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, data TEXT, status TEXT, type TEXT, triedCount INTEGER, timeStamp DATETIME DEFAULT (datetime('now','localtime')), UNIQUE(data) ON CONFLICT REPLACE)");
        new Handler().postDelayed(this, 2000);
    }

    /**
     * After 2000 milliseconds check the access_token in session. If the access_token is empty then
     * move to login page else move to complaints list page.
     */
    @Override
    public void run() {
        String access_token = getSession().getString("access_token", "");
        if (!access_token.equalsIgnoreCase("")) {
            startActivity(new Intent(this, ComplaintActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
