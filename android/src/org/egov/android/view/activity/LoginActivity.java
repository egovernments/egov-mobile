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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.egov.android.AndroidLibrary;
import org.egov.android.R;
import org.egov.android.api.ApiResponse;
import org.egov.android.common.JSONUtil;
import org.egov.android.controller.ApiController;
import org.egov.android.listener.Event;
import org.egov.android.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

	private String baseServerURL="";
	private JSONArray jsoncitiesarry=null;
	SharedPreferences sharedPreference=null;
	private Spinner citydropdown=null;
	
    /**
     * It is used to initialize an activity. An Activity is an application component that provides a
     * screen with which users can interact in order to do something, To initialize and set the
     * layout for the LoginActivity.Set click listeners to the login, register and forgot password
     * views.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((Button) findViewById(R.id.login_doLogin)).setOnClickListener(this);
        ((Button) findViewById(R.id.login_register)).setOnClickListener(this);
        ((TextView) findViewById(R.id.forgot_pwd_link)).setOnClickListener(this);

        ((TextView) findViewById(R.id.hdr_title)).setPadding(25, 0, 0, 0);

        getSession().edit().putInt("api_level", Build.VERSION.SDK_INT).commit();
        
        sharedPreference = getApplicationContext().getSharedPreferences(getString(R.string.app_name), 0);
		baseServerURL = sharedPreference.getString("api.baseUrl", null);
        
		citydropdown=(Spinner) findViewById(R.id.logincitydropdown);
		
		if(baseServerURL==null)
		{
			((LinearLayout)findViewById(R.id.logincitydropdownc)).setVisibility(View.VISIBLE);
			new getCitiesFromURL(LoginActivity.this, citydropdown).execute(AndroidLibrary.getInstance().getConfig()
					.getString("app.citiesJsonUrl"));
		}
		else
		{
			((LinearLayout)findViewById(R.id.logincitydropdownc)).setVisibility(View.GONE);
			/*((Button)findViewById(R.id.login_register)).setVisibility(View.GONE);*/
		}
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	sharedPreference = getApplicationContext().getSharedPreferences(getString(R.string.app_name), 0);
		baseServerURL = sharedPreference.getString("api.baseUrl", null);
    }
    
    
    /**
     * async task for getting cities list from httprequest
     */
    
    class getCitiesFromURL extends AsyncTask<String, Void, String>
    {
    	
    	ProgressDialog mDialog = null;
    	Spinner citydropdown=null;
    	Context context=null;
    	
    	public getCitiesFromURL(Context context, Spinner citydropdown) {
			// TODO Auto-generated constructor stub
    		this.context=context;
    		this.citydropdown=citydropdown;
		}
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		mDialog=new ProgressDialog(context);
    		mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.show();
    	}
    	
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			/**
		     * Get cities list json from app config url
		     */
			return JSONUtil.getJSON(params[0]);
		}
    	
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			try {
				if(!result.startsWith("ERROR"))
				{
				  //sort cities a to z
			      jsoncitiesarry = JSONUtil.sort(new JSONArray(result), new Comparator(){
			    		   public int compare(Object a, Object b){
			    		      JSONObject    ja = (JSONObject)a;
			    		      JSONObject    jb = (JSONObject)b;
			    		      return ja.optString("city_name", "").toLowerCase().compareTo(jb.optString("city_name", "").toLowerCase());
			    		   }
			      });
				  loadCitiesFromJsonArray(citydropdown);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(context, "Something went wrong in application!", Toast.LENGTH_LONG).show();
			}
			
			if(mDialog!=null)
			{
				mDialog.dismiss();
			}
			
		}
    	
    	
    }
    
    
    /**
     * load spinner from jsonarray
     * @throws JSONException 
     */
    
    
    public void loadCitiesFromJsonArray(Spinner dropdown) throws JSONException
    {
    	
    	List<String> spinnerArray =  new ArrayList<String>();
    	
    	//default option
    	spinnerArray.add("Select City");
    	
    	for(int i=0; i<jsoncitiesarry.length(); i++)
    	{
    		JSONObject city=jsoncitiesarry.getJSONObject(i);
    		spinnerArray.add(city.getString("city_name"));
    	}
    	
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
           this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);

    }
    

    /**
     * Event triggered when clicking on the item having click listener. When clicking on login
     * button, _login() function get called. When clicking on register button, redirect to
     * RegisterActivity. When clicking on forgot password, textview redirect to
     * ForgotPasswordActivity.
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.login_doLogin:
                _login();
                break;
            case R.id.login_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.forgot_pwd_link:
                Integer citySelectedIdx = ((Spinner) findViewById(R.id.logincitydropdown)).getSelectedItemPosition();
            	if(citySelectedIdx == 0 && baseServerURL == null)
            	{
            	  showMessage(getMessage(R.string.city_selection_empty));
            	  break;
            	}
            	else if(baseServerURL != null)
            	{
            	   startActivity(new Intent(this, ForgotPasswordActivity.class).putExtra("baseServerURL", baseServerURL));
            	   break;
            	}
				try {
				   startActivity(new Intent(this, ForgotPasswordActivity.class).putExtra("baseServerURL", getValidURL(jsoncitiesarry.getJSONObject((citySelectedIdx-1)).getString("url"))));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}            
                break;
        }
    }

    /**
     * Function called when clicking on login button. Check the empty validations If any require
     * field is empty, show the message by toast. If the user enter the credentials, call the login
     * api to check the user credentials.
     */
    private void _login() {

        String email = ((EditText) findViewById(R.id.login_email)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString()
                .trim();
        Integer citySelectedIdx = ((Spinner) findViewById(R.id.logincitydropdown)).getSelectedItemPosition();


        if (isEmpty(email)) {
            showMessage(getMessage(R.string.email_phone_empty));
            return;
        } else if (isEmpty(password)) {
            showMessage(getMessage(R.string.password_empty));
            return;
        } else if (citySelectedIdx == 0 && baseServerURL == null) {
            showMessage(getMessage(R.string.city_selection_empty));
            return;
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        try {
			ApiController.getInstance().login(this, user, (baseServerURL == null?getValidURL(jsoncitiesarry.getJSONObject((citySelectedIdx-1)).getString("url")):baseServerURL));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(LoginActivity.this, "Something went wrong on login operation!", Toast.LENGTH_LONG).show();
		}
    }
    
    private String getValidURL(String url)
    {
       return (url.endsWith("/")? url: url+"/");
    }

    /**
     * The onResponse method will be invoked after the Login API call onResponse methods will
     * contain the response If the response has status as "success" then store the access_token from
     * the response in shared preference object for session management. Shared Preference is used to
     * save and retrieve data in the form of key,value pair. finally redirects to ComplaintActivity
     * and finish the LoginActivity. If the response has error as activate your account then
     * redirect to the AccountActivationActivity and send the email/phone entered by the user from
     * login form through intent.
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        showMessage(msg);
        if (status.equalsIgnoreCase("success")) {
            try {
            	
            	if(baseServerURL==null)
            	{
	            	Integer citySelectedIdx = citydropdown.getSelectedItemPosition();
	            	Editor editor = sharedPreference.edit();
	            	editor.putString("api.baseUrl", getValidURL(jsoncitiesarry.getJSONObject((citySelectedIdx-1)).getString("url")));
	            	editor.putInt("api.citycode", jsoncitiesarry.getJSONObject((citySelectedIdx-1)).getInt("city_code"));
	            	editor.commit();
            	}
            	
                JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                JSONObject jo = ja.getJSONObject(0);
                getSession().edit().putString("access_token", jo.getString("access_token"))
                        .commit();
                getSession().edit().putString("user_name", jo.getString("user_name")).commit();
                startActivity(new Intent(this, ComplaintActivity.class));
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (msg.equalsIgnoreCase("Please activate your account")) {
                Intent intent = new Intent(this, AccountActivationActivity.class);
                intent.putExtra("username", ((EditText) findViewById(R.id.login_email)).getText()
                        .toString().trim());
                intent.putExtra("password", ((EditText) findViewById(R.id.login_password))
                        .getText().toString().trim());
                startActivity(intent);
            }
        }
    }
}
