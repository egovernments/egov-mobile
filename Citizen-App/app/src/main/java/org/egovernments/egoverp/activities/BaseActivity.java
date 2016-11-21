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
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.NavdrawAdapter;
import org.egovernments.egoverp.api.ApiController;
import org.egovernments.egoverp.config.Config;
import org.egovernments.egoverp.config.SessionManager;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.models.City;
import org.egovernments.egoverp.models.NavigationItem;

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

        final ViewGroup nullParent = null;
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.baselayout, nullParent);

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
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        if(isTabSupport)
        {
            if (Build.VERSION.SDK_INT >= 21) {
                assert toolbar != null;
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
        actionBar.setTitle(getToolbarTitle(actionBar.getTitle().toString()));
        mActionBarTitle = actionBar.getTitle();

        if(toolbar!=null)
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {

                actionBar.setTitle(mActionBarTitle);
            }

            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(R.string.menu_label);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

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


        recyclerView.setAdapter(new NavdrawAdapter(arrayList, sessionManager.getName(), sessionManager.getMobile(), sessionManager.getUrlLocation(), R.drawable.ic_person_black_36dp, navItemClickListener));

    }

    public String getToolbarTitle(String titleFromResource)
    {
        return titleFromResource;
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
        else if(getString(R.string.vacantlandtax_label).equals(menuItemText))
        {
            openVacandLandPage();
        }
        else if(getString(R.string.watertax_label).equals(menuItemText))
        {
            openWaterTaxPage();
        }
        else if(getString(R.string.building_plan_label).equals(menuItemText))
        {
            openBuildingPlanApprovalPage();
        }
        else if(getString(R.string.building_penalization_label).equals(menuItemText))
        {
            openBuildingPenalization();
        }
        else if(getString(R.string.citizen_charter_label).equals(menuItemText))
        {
            openCitizenCharterPage();
        }
        else if(getString(R.string.sos_label).equals(menuItemText))
        {
            openSOSPage();
        }
        else if(getString(R.string.aboutus_label).equals(menuItemText))
        {
            openAboutUsPage();
        }
        else if(getString(R.string.sla_label).equals(menuItemText))
        {
            openSLAPage();
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
        if (!mActionBarTitle.equals(getString(R.string.propertytax_label))) {
            if (!getTitle().toString().equals(getString(R.string.home_label)))
                finish();
            intent = new Intent(BaseActivity.this, PropertyTaxSearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void openVacandLandPage()
    {
        Intent intent;
        if (!mActionBarTitle.equals(getString(R.string.vacantlandtax_label))) {
            if (!getTitle().toString().equals(getString(R.string.home_label)))
                finish();
            intent = new Intent(BaseActivity.this, PropertyTaxSearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(PropertyTaxSearchActivity.IS_VACANT_LAND, true);
            startActivity(intent);
        }
    }

    public void openBuildingPenalization()
    {
        Intent intent;
        if (!mActionBarTitle.equals(getString(R.string.title_activity_building_plan))) {
            if (!getTitle().toString().equals(getString(R.string.home_label)))
                finish();
            intent = new Intent(BaseActivity.this, BuildingPlanActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(BuildingPlanActivity.IS_BUILDING_PENALIZATION, true);
            startActivity(intent);
        }
    }

    public void openWaterTaxPage()
    {
        Intent intent;
        if (!getTitle().toString().equals(getString(R.string.watertax_label))) {
            intent = new Intent(BaseActivity.this, WaterChargesSearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if (!getTitle().toString().equals(getString(R.string.home_label)))
                finish();
        }
    }

    public void openBuildingPlanApprovalPage()
    {
        Intent intent;
        if (!getTitle().toString().equals(getString(R.string.building_plan_label))) {
            intent = new Intent(BaseActivity.this, BuildingPlanActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if (!getTitle().toString().equals(getString(R.string.home_label)))
                finish();
        }
    }

    public void openCitizenCharterPage()
    {
        Intent intent;
        if (!getTitle().toString().equals(getString(R.string.citizen_charter_label))) {
            intent = new Intent(BaseActivity.this, CitizenCharterActivity.class);
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
            startActivityForResult(intent, 0);
            if (!getTitle().toString().equals(getString(R.string.home_label)))
                finish();
        }
    }

    public void openSOSPage()
    {
        Intent intent;
        if (!getTitle().toString().equals(getString(R.string.sos_label))) {
            intent = new Intent(BaseActivity.this, SOSActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 0);
            if (!getTitle().toString().equals(getString(R.string.home_label)))
                finish();
        }
    }

    public void openAboutUsPage()
    {
        Intent intent;
        if (!getTitle().toString().equals(getString(R.string.aboutus_label))) {
            intent = new Intent(BaseActivity.this, AboutUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 0);
            if (!getTitle().toString().equals(getString(R.string.home_label)))
                finish();
        }
    }

    public void openSLAPage()
    {
        Intent openPdfViewer=new Intent(BaseActivity.this, PdfViewerActivity.class);
        openPdfViewer.putExtra(PdfViewerActivity.PAGE_TITLE, getString(R.string.sla_label));
        openPdfViewer.putExtra(PdfViewerActivity.PDF_URL, "http://egovernments.org/egov-apps/puraseva/sla/sla.pdf");
        startActivity(openPdfViewer);
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

                if(progressDialog.isShowing())
                progressDialog.dismiss();


            }

            @Override
            public void failure(RetrofitError error) {

                if(progressDialog.isShowing())
                progressDialog.dismiss();

                sessionManager.logoutUser();

                ApiController.apiInterface = null;

                Intent intent = new Intent(BaseActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    //Method fills the nav drawer's ArrayList
    private void fillList() {

        try {
            ConfigManager configManager= AppUtils.getConfigManager(getApplicationContext());

            City.Modules disabledModules=null;
            if(!TextUtils.isEmpty(sessionManager.getDisabledModulesJson()))
                disabledModules=new Gson().fromJson(sessionManager.getDisabledModulesJson(), City.Modules.class);

            arrayList.add(new NavigationItem(R.drawable.ic_home_black_24dp, getString(R.string.home_label), (getString(R.string.home_label).equals(mActionBarTitle)), R.color.home_color));

            //check for pgr module enabled or not
            if(Boolean.valueOf((String)configManager.get(Config.Modules.PGR,"true")) && disabledModules!=null
                    && !disabledModules.isPgrDisable())
            {
                arrayList.add(new NavigationItem(R.drawable.ic_archive_black_24dp, getString(R.string.grievances_label), (getString(R.string.grievances_label).equals(mActionBarTitle)), R.color.grievance_color));
            }

            //check for property tax module enabled or not
            if(Boolean.valueOf((String)configManager.get(Config.Modules.PROPERTY_TAX,"true"))
                    && disabledModules!=null && !disabledModules.isPropertyTaxDisable()
                    && (sessionManager.getUrlLocationCode() != 1021)
                    && (sessionManager.getUrlLocationCode() != 1073)
                    && (sessionManager.getUrlLocationCode() != 1086))
            {
                arrayList.add(new NavigationItem(R.drawable.ic_business_black_24dp, getString(R.string.propertytax_label), (getString(R.string.propertytax_label).equals(mActionBarTitle)), R.color.propertytax_color));
            }

            //check for vacant land tax module enabled or not
            if(Boolean.valueOf((String)configManager.get(Config.Modules.VACANT_LAND_TAX,"true"))
                    && disabledModules!=null && !disabledModules.isVacantLandTaxDisable()
                    && (sessionManager.getUrlLocationCode() != 1021)
                    && (sessionManager.getUrlLocationCode() != 1073)
                    && (sessionManager.getUrlLocationCode() != 1086))
            {
                arrayList.add(new NavigationItem(R.drawable.ic_vacant_land_36dp, getString(R.string.vacantlandtax_label), (getString(R.string.vacantlandtax_label).equals(mActionBarTitle)), R.color.vacand_land_color));
            }

            //check for water tax module enabled or not
            if(Boolean.valueOf((String)configManager.get(Config.Modules.WATER_CHARGE,"true"))
                    && disabledModules!=null && !disabledModules.isWaterChargeDisable()
                    && (sessionManager.getUrlLocationCode() != 1021)
                    && (sessionManager.getUrlLocationCode() != 1073)
                    && (sessionManager.getUrlLocationCode() != 1086))
            {
                arrayList.add(new NavigationItem(R.drawable.ic_water_tab_black_24dp, getString(R.string.watertax_label), (getString(R.string.watertax_label).equals(mActionBarTitle)), R.color.watertax_color));
            }

            //check for building plan module enabled or not
            if(Boolean.valueOf((String)configManager.get(Config.Modules.BPA,"true"))
                    && disabledModules!=null && !disabledModules.isBPADisable()
                    && (sessionManager.getUrlLocationCode() != 1021)
                    && (sessionManager.getUrlLocationCode() != 1073)
                    && (sessionManager.getUrlLocationCode() != 1086))
            {
                arrayList.add(new NavigationItem(R.drawable.ic_town_plan_36dp, getString(R.string.building_plan_label), (getString(R.string.building_plan_label).equals(mActionBarTitle)), R.color.bpacolor));
            }

            //check for building plan module enabled or not
            if(Boolean.valueOf((String)configManager.get(Config.Modules.BPS,"true"))
                    && disabledModules!=null && !disabledModules.isBPSDisable()
                    && (sessionManager.getUrlLocationCode() != 1021)
                    && (sessionManager.getUrlLocationCode() != 1073)
                    && (sessionManager.getUrlLocationCode() != 1086))
            {
                arrayList.add(new NavigationItem(R.drawable.ic_location_city_black_36dp, getString(R.string.building_penalization_label), (getString(R.string.building_penalization_label).equals(mActionBarTitle)), R.color.bpcolor));
            }

            if(Boolean.valueOf((String)configManager.get(Config.Modules.CITIZEN_CHARTER,"true"))
                    && disabledModules!=null && !disabledModules.isCitizenCharterDisable())
            {
                arrayList.add(new NavigationItem(R.drawable.ic_grid_on_black_24dp, getString(R.string.citizen_charter_label), (getString(R.string.citizen_charter_label).equals(mActionBarTitle)), R.color.citizen_charter_color));
            }

            if(Boolean.valueOf((String)configManager.get(Config.Modules.SOS,"true"))
                    && disabledModules!=null && !disabledModules.isSOSDisable())
            {
                arrayList.add(new NavigationItem(R.drawable.ic_call_black_36dp, getString(R.string.sos_label),(getString(R.string.sos_label).equals(mActionBarTitle)), R.color.sos_color));
            }

            if(Boolean.valueOf((String)configManager.get(Config.Modules.SLA,"true"))
                    && disabledModules!=null && !disabledModules.isSLADisable())
            {
                arrayList.add(new NavigationItem(R.drawable.ic_access_time_white_36dp, getString(R.string.sla_label),(getString(R.string.sla_label).equals(mActionBarTitle)), R.color.sla_color));
            }

            if(Boolean.valueOf((String)configManager.get(Config.Modules.ABOUT_US,"true"))
                    && disabledModules!=null && !disabledModules.isAboutUsDisable())
            {
                arrayList.add(new NavigationItem(R.drawable.ic_info_black_24dp, getString(R.string.aboutus_label),(getString(R.string.aboutus_label).equals(mActionBarTitle)), R.color.aboutus_color));
            }

            arrayList.add(new NavigationItem(R.drawable.ic_person_black_24dp, getString(R.string.profile_label), (getString(R.string.profile_label).equals(mActionBarTitle)), R.color.profile_color));

            arrayList.add(new NavigationItem(R.drawable.ic_backspace_black_24dp, getString(R.string.logout_label), (getString(R.string.logout_label).equals(mActionBarTitle)), R.color.logout_color));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}