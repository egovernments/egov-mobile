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

package org.egov.employee.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Response;

import org.egov.employee.api.ApiController;
import org.egov.employee.application.EgovApp;

import java.util.Date;

import offices.org.egov.egovemployees.R;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

public class SplashScreen extends BaseActivity {

    ProgressBar pbsplash;
    LinearLayout layerror;
    Boolean isFromSessionTimeOut;
    Boolean isFromLogOut;

    String serverErrorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Button)findViewById(R.id.btnretry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAppStartUpSetup();
            }
        });

        pbsplash=(ProgressBar)findViewById(R.id.pbsplash);
        layerror=(LinearLayout) findViewById(R.id.layerror);

        isFromSessionTimeOut=getIntent().getBooleanExtra("isFromSessionTimeOut",false);
        isFromLogOut=getIntent().getBooleanExtra("isLoggedOut", false);
        if(isFromSessionTimeOut || isFromLogOut)
        {
            if(isFromSessionTimeOut)
            {
                showLogOutMsg();
                performAppStartUpSetup();
            }
            else
            {
                logOutCurrentUser();
            }
        }
        else {
            performAppStartUpSetup();
        }



    }


    private void performAppStartUpSetup()
    {

        ((ProgressBar)findViewById(R.id.pbsplash)).setVisibility(View.VISIBLE);
        ((LinearLayout)findViewById(R.id.layerror)).setVisibility(View.GONE);

        Long urlTimeOut=preference.getLastUrlUpdateTime()+(EgovApp.getInstance().getUrlTimeOutDays()*24*60*60*1000);

        if(urlTimeOut < new Date().getTime() || (EgovApp.getInstance().isMultiCitySupport() && !TextUtils.isEmpty(preference.getApiAccessToken())))
        {
            if(checkInternetConnectivity())
            {
                //refresh server url resources
                new getCityResource().execute();
            }
            else
            {
                showError("No internet connection!");
            }
        }
        else {

            if(TextUtils.isEmpty(preference.getApiAccessToken()))
            {
                new getCityResource().execute();
            }
            else {
                openHomePageifUserLogged();
            }
        }
    }

    public void openHomePageifUserLogged()
    {
        if(!TextUtils.isEmpty(preference.getApiAccessToken())){
            recordEmployeeLog();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    Intent newIntent = new Intent(SplashScreen.this, Homepage.class);
                    startActivity(newIntent);
                    finish();
                }
            }, 2000);
        }
        else
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent newIntent = new Intent(SplashScreen.this, LoginActivity.class);
                    newIntent.putExtra("isFromSessionTimeOut", isFromSessionTimeOut);
                    startActivity(newIntent);
                    finish();
                }
            }, 2000);
        }
    }

    public void showError(String msg)
    {
        pbsplash.setVisibility(View.GONE);
        layerror.setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.tvError)).setText((TextUtils.isEmpty(msg) ? "No response from server!" : msg));
    }

    public void showLogOutMsg()
    {
        ((ImageView)findViewById(R.id.appLogo)).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.tvinfosplash)).setVisibility(View.VISIBLE);
    }

    @Override
    public void showSnackBar(String msg) {
        super.showSnackBar(msg);
        serverErrorMsg=msg;
    }

    public void logOutCurrentUser()
    {

        showLogOutMsg();
        Call<JsonObject> requestLogOut = ApiController.getAPI(getApplicationContext(), SplashScreen.this).logout(preference.getApiAccessToken());
        final Callback<JsonObject> logoutCallback = new Callback<JsonObject>() {

            @Override
            public void onResponse(retrofit.Response<JsonObject> response, Retrofit retrofit) {
                preference.setApiAccessToken("");
                preference.setActiveCityCode(-1);
                performAppStartUpSetup();
            }

            @Override
            public void onFailure(Throwable t) {

                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                finish();

            }
        };

        requestLogOut.enqueue(logoutCallback);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_splash_screen;
    }

    class getCityResource extends AsyncTask<String, Integer, Response>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Response doInBackground(String... params) {
            Response response=null;
            if(EgovApp.getInstance().isMultiCitySupport() && !TextUtils.isEmpty(preference.getApiAccessToken()) && preference.getActiveCityCode() !=-1)
            {
                response = ApiController.getCityURL(EgovApp.getInstance().getCityResourceUrl(), preference.getActiveCityCode(), SplashScreen.this);
            }
            else {
                response = ApiController.getCityURL(EgovApp.getInstance().getCityResourceUrl(), SplashScreen.this);
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);

            try {
                if(response!=null) {
                    if (response.code() == 200) {
                        String s = response.body().string();
                        //check and load city config's

                        if (EgovApp.getInstance().isMultiCitySupport() && TextUtils.isEmpty(preference.getApiAccessToken())) {
                            JsonArray jsonCitiesList = new JsonParser().parse(s).getAsJsonArray();
                            preference.setCitiesList(jsonCitiesList.toString());
                        } else {
                            JsonObject jsonCityObj = new JsonParser().parse(s).getAsJsonObject();
                            //reset active city resources
                            preference.setActiveCityName(jsonCityObj.get("city_name").getAsString());
                            preference.setActiveCityUrl(jsonCityObj.get("url").getAsString());
                            preference.setLastUrlUpdateTime(new Date().getTime());
                        }

                        openHomePageifUserLogged();

                    }
                }
                else
                {
                    showError(serverErrorMsg);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
