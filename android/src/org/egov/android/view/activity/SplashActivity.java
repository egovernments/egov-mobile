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

import java.net.InetAddress;
import java.util.Date;
import org.egov.android.AndroidLibrary;
import org.egov.android.R;
import org.egov.android.common.JSONUtil;
import org.egov.android.data.SQLiteHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SplashActivity extends BaseActivity implements Runnable {

	
	SharedPreferences sharedPreference;
	boolean isMultiCity;
	
    /**
     * To set the layout for the SplashActivity this screen appears for 2000 milliseconds. Create a
     * table named 'jobs' to handle upload and download jobs.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        sharedPreference = getApplicationContext().getSharedPreferences(getString(R.string.app_name), 0);
		String baseServerURL = sharedPreference.getString("api.baseUrl", null);
		isMultiCity=AndroidLibrary.getInstance().getConfig().get("api.multicities", "false").equals("true");
		
		Log.v(SplashActivity.class.getName(), "Is Multicity support? "+ isMultiCity);
		
		if(baseServerURL == null && 
			!isMultiCity)
		{
			new getBaseServerURL().execute((String)AndroidLibrary.getInstance().getConfig().get("api.baseUrl", ""));
		}
		else
		{
			if(baseServerURL != null)
			{
				Long lasturlupdationtime=sharedPreference.getLong("urlupdatetime", 0);
				Integer timeOutDays=AndroidLibrary.getInstance().getConfig().getInt("app.timeoutdays");
				
				lasturlupdationtime=lasturlupdationtime+(timeOutDays*24*60*60*1000);
								
				if(lasturlupdationtime < new Date().getTime())
				{
					if(isMultiCity)
					{
						new getBaseServerURL().execute((String)AndroidLibrary.getInstance().getConfig().get("app.citiesJsonUrl", ""));
					}
					else
					{
						new getBaseServerURL().execute((String)AndroidLibrary.getInstance().getConfig().get("api.baseUrl", ""));	
					}
				}
				else
				{
					new Handler().postDelayed(this, 3000);
				}
			}
			else
			{
				new Handler().postDelayed(this, 3000);
			}
		}
        
        SQLiteHelper
                .getInstance()
                .execSQL(
                        "CREATE TABLE IF NOT EXISTS tbl_cache (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, url TEXT, data TEXT, timeStamp TEXT, UNIQUE(data) ON CONFLICT REPLACE)");
        SQLiteHelper
                .getInstance()
                .execSQL(
                        "CREATE TABLE IF NOT EXISTS tbl_jobs (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, data TEXT, status TEXT, type TEXT, triedCount INTEGER, timeStamp DATETIME DEFAULT (datetime('now','localtime')), UNIQUE(data) ON CONFLICT REPLACE)");
        
    }
    
    private String getValidURL(String url)
    {
       return (url.endsWith("/")? url: url+"/");
    }
    
    public void setOrRefreshBaseURL(SharedPreferences sharedPreference, JSONObject cityJSON) throws JSONException
    {
		Editor editor = sharedPreference.edit();
	    editor.putString("api.baseUrl", getValidURL(cityJSON.getString("url")));
	    editor.putLong("urlupdatetime", new Date().getTime());
	    editor.commit();
	    new Handler().postDelayed(SplashActivity.this, 3000);
    }

    public class getBaseServerURL extends AsyncTask<String, Integer, String>
    {
    	ProgressBar loadSpinner;
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		loadSpinner=(ProgressBar)findViewById(R.id.splashprogress);
    		loadSpinner.setVisibility(View.VISIBLE);
    	}
    	
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			if(isInternetAvailable())
			{
				return JSONUtil.getJSON(params[0]);
			}
			return null;
		}
    	
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				
				if(result == null)
				{
					Toast.makeText(getApplicationContext(), "Please, Check your internet connection!", Toast.LENGTH_LONG).show();
					finish();
					return;
				}
				
				Log.d(SplashActivity.class.getName(), result);
				
	    		if(result.startsWith("ERROR"))
	    		{
	    			Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
	    			finish();
	    			return;
	    		}
	    		
	    		if(isMultiCity)
	    		{
	    			JSONArray cities_array=new JSONArray(result);
	    			Integer citycode=sharedPreference.getInt("api.citycode", 0);
	    			for(int i=0;i<cities_array.length();i++)
	    			{
	    				JSONObject city=cities_array.getJSONObject(i);
	    				if(city.getInt("city_code") == citycode)
	    				{
	    					setOrRefreshBaseURL(sharedPreference, city);
	    					return;
	    				}
	    			}
	    			
	    		}else
	    		{
	    			setOrRefreshBaseURL(sharedPreference, new JSONObject(result));
	    		}
	    		
	    		
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
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
    
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }
    
}
