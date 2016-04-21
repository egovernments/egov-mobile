/*
 *    eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (c) 2016  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egovernments.egoverp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The activity sets up common features of layout for other activities
 **/

public class BaseActivity extends AppCompatActivity {

    protected CoordinatorLayout fullLayout;
    protected FrameLayout activityContent;

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

        fullLayout = (CoordinatorLayout) getLayoutInflater().inflate(R.layout.baselayout, null);

        activityContent = (FrameLayout) fullLayout.findViewById(R.id.drawer_content);

        getLayoutInflater().inflate(layoutResID, activityContent, true);

        super.setContentView(fullLayout);

        sessionManager = new SessionManager(getApplicationContext());
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.processing_msg));
        progressDialog.setCancelable(false);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        android.support.v7.widget.Toolbar toolbarCollapse = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbarCollapse);

        tabLayout=(TabLayout)findViewById(R.id.tablayout);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final ListView listView = (ListView) findViewById(R.id.drawer);

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
            listView.setPadding(0,getStatusBarHeight(),0,0);
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
        fillList();

        BaseAdapter navAdapter = new NavAdapter(arrayList);

        listView.setAdapter(navAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                drawerLayout.closeDrawer(listView);
                drawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NavigationItem navigationItem=(NavigationItem)listView.getItemAtPosition(position);
                        openPage(navigationItem.getNavTitle());
                    }
                }, 300);
            }
        });
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


            arrayList.add(new NavigationItem(R.drawable.ic_home_black_24dp, getString(R.string.home_label)));

            //check for pgr module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.pgr","true")))
            {
                arrayList.add(new NavigationItem(R.drawable.ic_feedback_black_24dp, getString(R.string.grievances_label)));
            }

            //check for property tax module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.propertytax","true")))
            {
                arrayList.add(new NavigationItem(R.drawable.ic_business_black_24dp, getString(R.string.propertytax_label)));
            }

            //check for water tax module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.watertax","true")))
            {
                arrayList.add(new NavigationItem(R.drawable.ic_water_black_24dp, getString(R.string.watertax_label)));
            }

            arrayList.add(new NavigationItem(R.drawable.ic_person_black_24dp, getString(R.string.profile_label)));
            arrayList.add(new NavigationItem(R.drawable.ic_backspace_black_24dp, getString(R.string.logout_label)));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //POJO class for nav drawer items
    private class NavigationItem {

        private final int NavIcon;
        private final String NavTitle;

        public NavigationItem(int navIcon, String navTitle) {
            NavIcon = navIcon;
            NavTitle = navTitle;
        }

        public int getNavIcon() {
            return NavIcon;
        }

        public String getNavTitle() {
            return NavTitle;
        }

    }


    private class NavigationViewHolder {
        private ImageView nav_item_icon;
        private TextView nav_item_text;
        private RelativeLayout nav_drawer_row;
    }

    //Custom adapter for nav drawer
    public class NavAdapter extends BaseAdapter {

        List<NavigationItem> navigationItems;

        public NavAdapter(List<NavigationItem> navigationItems) {
            this.navigationItems = navigationItems;
        }

        @Override
        public int getCount() {
            return navigationItems.size();
        }

        @Override
        public NavigationItem getItem(int position) {
            return navigationItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            NavigationViewHolder navigationViewHolder = null;
            View view = convertView;
            if (convertView == null) {

                view = getLayoutInflater().inflate(R.layout.item_navdrawer, parent, false);

                navigationViewHolder = new NavigationViewHolder();
                navigationViewHolder.nav_item_text = (TextView) view.findViewById(R.id.title);
                navigationViewHolder.nav_item_icon = (ImageView) view.findViewById(R.id.icon);
                navigationViewHolder.nav_drawer_row = (RelativeLayout) view.findViewById(R.id.navdrawer_row);

                view.setTag(navigationViewHolder);
            }
            if (navigationViewHolder == null) {
                navigationViewHolder = (NavigationViewHolder) view.getTag();
            }

            NavigationItem navigationItem = getItem(position);
            navigationViewHolder.nav_item_text.setText(navigationItem.getNavTitle());
            if (mActionBarTitle.equals(navigationItem.getNavTitle())) {
                navigationViewHolder.nav_drawer_row.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
                navigationViewHolder.nav_drawer_row.getBackground().setAlpha(102);
            }
            navigationViewHolder.nav_item_icon.setImageResource(navigationItem.getNavIcon());
            return view;
        }

    }
}