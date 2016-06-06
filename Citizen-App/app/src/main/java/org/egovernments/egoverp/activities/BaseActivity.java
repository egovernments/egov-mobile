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

package org.egovernments.egoverp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.NavdrawAdapter;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.models.NavigationItem;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.SessionManager;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The activity sets up common features of layout for other activities
 **/

public class BaseActivity extends AppCompatActivity {

    protected LinearLayout activityContent;

    protected ArrayList<NavigationItem> arrayList;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected CharSequence mActionBarTitle;

    protected SessionManager sessionManager;

    protected ProgressDialog progressDialog;

    protected boolean isTabSupport=false;
    protected TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baselayout);


    }

    public void setContentViewWithTabs(final int layoutResID)
    {
        isTabSupport=true;
        setContentView(layoutResID);
    }

    //Overridden method will intercept layout passed and inflate it into baselayout.xml
    @Override
    public void setContentView(final int layoutResID) {

        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.baselayout, null);

        activityContent = (LinearLayout) drawerLayout.findViewById(R.id.activityContent);

        getLayoutInflater().inflate(layoutResID, activityContent, true);

        super.setContentView(drawerLayout);

        sessionManager = new SessionManager(getApplicationContext());
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.processing_msg));
        progressDialog.setCancelable(false);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        android.support.v7.widget.Toolbar toolbarCollapse = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbarCollapse);

        tabLayout=(TabLayout)findViewById(R.id.tablayout);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.drawer);
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        if(isTabSupport)
        {
            if (Build.VERSION.SDK_INT >= 22) {
                toolbar.setElevation(0);
            }
            tabLayout.setVisibility(View.VISIBLE);
        }

        if(toolbarCollapse!=null)
        {
            toolbar.setVisibility(View.GONE);
            toolbar=toolbarCollapse;
        }

        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;

        mActionBarTitle = actionBar.getTitle();

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);




        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {

                actionBar.setTitle(mActionBarTitle);
            }

            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(R.string.menu_label);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);


        arrayList = new ArrayList<>();
        arrayList.add(null);
        fillList();

        NavdrawAdapter.NavItemClickListener navItemClickListener=new NavdrawAdapter.NavItemClickListener() {
            @Override
            public void onItemClick(final NavigationItem navigationItem) {
                drawerLayout.closeDrawer(recyclerView);
                drawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openPage(navigationItem.getNavTitle());
                    }
                }, 300);
            }
        };


        recyclerView.setAdapter(new NavdrawAdapter(arrayList, sessionManager.getName(), sessionManager.getMobile(), R.drawable.ic_person_black_36dp, navItemClickListener));

    }

    public void openPage(String menuItemText)
    {
        if(getString(R.string.home_label).equals(menuItemText))
        {
            openHomePage();
        }
        else if(getString(R.string.grievances_label).equals(menuItemText))
        {
            openGrievancePage();
        }
        else if(getString(R.string.propertytax_label).equals(menuItemText))
        {
            openPropertyPage();
        }
        else if(getString(R.string.watertax_label).equals(menuItemText))
        {
            openWaterTaxPage();
        }
        else if(getString(R.string.profile_label).equals(menuItemText))
        {
            openProfilePage();
        }
        else if(getString(R.string.logout_label).equals(menuItemText))
        {
            logoutUser();
        }

    }

    public void openHomePage()
    {
        if (!getTitle().toString().equals(getString(R.string.home_label)))
            finish();
    }

    public void openGrievancePage()
    {
        Intent intent;
        if (!getTitle().toString().equals(getString(R.string.grievances_label))) {
            intent = new Intent(BaseActivity.this, GrievanceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if (!getTitle().toString().equals(getString(R.string.home_label)))
                finish();
        }
    }

    public void openPropertyPage()
    {
        Intent intent;
        if (!getTitle().toString().equals(getString(R.string.propertytax_label))) {
            intent = new Intent(BaseActivity.this, PropertyTaxSearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if (!getTitle().toString().equals(getString(R.string.home_label)))
                finish();
        }
    }

    public void openWaterTaxPage()
    {
        Intent intent;
        if (!getTitle().toString().equals(getString(R.string.watertax_label))) {
            intent = new Intent(BaseActivity.this, WaterTaxSearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if (!getTitle().toString().equals(getString(R.string.home_label)))
                finish();
        }
    }

    public void openProfilePage()
    {
        Intent intent;
        if (!getTitle().toString().equals(getString(R.string.profile_label))) {
            intent = new Intent(BaseActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if (!getTitle().toString().equals(getString(R.string.home_label)))
                finish();
        }
    }

    public void logoutUser()
    {
        progressDialog.show();
        ApiController.getAPI(BaseActivity.this).logout(sessionManager.getAccessToken(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                sessionManager.logoutUser();

                ApiController.apiInterface = null;

                Intent intent = new Intent(BaseActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                progressDialog.dismiss();


            }

            @Override
            public void failure(RetrofitError error) {

                sessionManager.logoutUser();

                ApiController.apiInterface = null;

                Intent intent = new Intent(BaseActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                progressDialog.dismiss();
                finish();
            }
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //Method fills the nav drawer's ArrayList
    private void fillList() {

        try {
            ConfigManager configManager= AppUtils.getConfigManager(getApplicationContext());


            arrayList.add(new NavigationItem(R.drawable.ic_home_black_24dp, getString(R.string.home_label), (getString(R.string.home_label).equals(mActionBarTitle))));

            //check for pgr module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.pgr","true")))
            {
                arrayList.add(new NavigationItem(R.drawable.ic_error_outline_black_24dp, getString(R.string.grievances_label), (getString(R.string.grievances_label).equals(mActionBarTitle))));
            }

            //check for property tax module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.propertytax","true")))
            {
                arrayList.add(new NavigationItem(R.drawable.ic_business_black_24dp, getString(R.string.propertytax_label), (getString(R.string.propertytax_label).equals(mActionBarTitle))));
            }

            //check for water tax module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.watertax","true")))
            {
                arrayList.add(new NavigationItem(R.drawable.ic_local_drink_black_24dp, getString(R.string.watertax_label), (getString(R.string.watertax_label).equals(mActionBarTitle))));
            }

            arrayList.add(new NavigationItem(R.drawable.ic_person_black_24dp, getString(R.string.profile_label), (getString(R.string.profile_label).equals(mActionBarTitle))));
            arrayList.add(new NavigationItem(R.drawable.ic_backspace_black_24dp, getString(R.string.logout_label), (getString(R.string.logout_label).equals(mActionBarTitle))));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}