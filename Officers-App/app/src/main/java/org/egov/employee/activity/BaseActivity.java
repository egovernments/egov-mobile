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

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.egov.employee.api.ApiController;
import org.egov.employee.api.LoggingInterceptor;
import org.egov.employee.config.AppPreference;
import org.egov.employee.config.NavMenuItems;

import java.lang.reflect.Method;

import offices.org.egov.egovemployees.R;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by egov on 15/12/15.
 */
public abstract class BaseActivity extends AppCompatActivity implements LoggingInterceptor.ErrorListener {

    AppPreference preference;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        //app preference variable
        preference=new AppPreference(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected abstract int getLayoutResource();

    //error message function called from error listener when receive an error response from server
    @Override
    public void showSnackBar(String msg) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }

    //user session timeout function handle
    @Override
    public void sessionTimeOutError() {

        //clear current access token
        preference.setApiAccessToken("");

        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("isFromSessionTimeOut", true);
        startActivity(intent);

    }

    //check internet connection available method with retry function parameter
    public boolean checkInternetConnectivity(final Object classObj, final String retrymethod)
    {
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info)
                {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        Snackbar snackbar=Snackbar.make(findViewById(android.R.id.content), "No connection", Snackbar.LENGTH_LONG);

        //if retry function is not null then, this code will add retry button along with snackbar
        if(!TextUtils.isEmpty(retrymethod)) {
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //call retry function
                    try {
                        Method method = classObj.getClass().getMethod(retrymethod, (Class<?>[]) null);
                        method.invoke(classObj, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        snackbar.show();

        return false;
    }

    //check internet connection available method
    public boolean checkInternetConnectivity() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        Snackbar snackbar=Snackbar.make(findViewById(android.R.id.content), "No connection", Snackbar.LENGTH_LONG);
        snackbar.show();



        return false;
    }


    public void recordEmployeeLog()
    {
        if(TextUtils.isEmpty(preference.getApiAccessToken()))
        {
            return;
        }

        String deviceId= Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceOS=Integer.toString(Build.VERSION.SDK_INT);
        String deviceType="mobile";

        Call<JsonObject> jsonDeviceLog = ApiController.getAPI(getApplicationContext(), BaseActivity.this).addDeviceLog(deviceId, deviceOS, deviceType, preference.getApiAccessToken());
        final Callback<JsonObject> deviceLog = new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        };

        jsonDeviceLog.enqueue(deviceLog);


    }

    public void setupNavDrawer(NavMenuItems navMenuItem)
    {
        //Initialising NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //Setting OnNavigationItemSelectedListener to the Navigation View.
        //This is used to perform specific action when an item is clicked.
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Navigation View must close when any of this item is clicked.
                //To do this we use the closeDrawers() method.
                drawerLayout.closeDrawers();

                if(menuItem.isChecked())
                {
                    return false;
                }

                //Using switch case to identify the ID of the menu item
                // and then performing relevant action.
                switch (menuItem.getItemId()){
                    case R.id.item_worklist:
                        openWorkListPage();
                        return false;
                    case R.id.item_grievance:
                        openGrievancePage();
                        return false;
                    case R.id.item_logout:
                        logout();
                        return true;
                    default:
                        return true;
                }

            }
        });


        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.tvFullName)).setText(preference.getName());
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.tvUserId)).setText(preference.getUserName());

        navigationView.getMenu().getItem(navMenuItem.getMenuCode()).setChecked(true);

        //Initialising DrawerLayout.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {

            }

            public void onDrawerOpened(View drawerView) {

            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
    }

    public void openGrievancePage()
    {
        Intent intent = new Intent(BaseActivity.this, GrievanceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        closeCurrentActivityIsNotWorkList();
    }

    public void openWorkListPage()
    {
        Intent intent = new Intent(BaseActivity.this, Homepage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void closeCurrentActivityIsNotWorkList()
    {
        if(!(this instanceof Homepage))
        {
            finish();
        }
    }

    public void logout()
    {
        //clear current user access token from app preference in splashscreen with flag param
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("isLoggedOut", true);
        this.startActivity(intent);
        this.overridePendingTransition(0,0);
    }


    public void enableBackButton()
    {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

}
