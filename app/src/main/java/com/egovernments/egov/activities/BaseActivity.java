package com.egovernments.egov.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.egovernments.egov.R;
import com.egovernments.egov.network.ApiController;
import com.egovernments.egov.network.SessionManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The activity sets up common features of layout for other activities
 **/

public class BaseActivity extends AppCompatActivity {

    protected LinearLayout fullLayout;
    protected FrameLayout activityContent;

    protected ArrayList<NavigationItem> arrayList;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected CharSequence mActionBarTitle;

    protected SessionManager sessionManager;

    protected ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baselayout);


    }

    //Overridden method will intercept layout passed and inflate it into baselayout.xml
    @Override
    public void setContentView(final int layoutResID) {

        fullLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.baselayout, null);
        activityContent = (FrameLayout) fullLayout.findViewById(R.id.drawer_content);

        getLayoutInflater().inflate(layoutResID, activityContent, true);

        super.setContentView(fullLayout);

        sessionManager = new SessionManager(getApplicationContext());

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.processing_msg));
        progressDialog.setCancelable(false);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mActionBarTitle = actionBar.getTitle();

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {

                actionBar.setTitle(mActionBarTitle);
            }

            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(R.string.menu_label);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);


        ListView listView = (ListView) findViewById(R.id.drawer);

        arrayList = new ArrayList<>();
        fillList();

        BaseAdapter navAdapter = new NavAdapter(arrayList);

        listView.setAdapter(navAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent;

                switch (position) {

                    case 0:
                        intent = new Intent(BaseActivity.this, GrievanceActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        if (!getTitle().toString().equals(getString(R.string.home_label)))
                            finish();
                        break;

                    case 1:
                        intent = new Intent(BaseActivity.this, PropertyTaxSearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        if (!getTitle().toString().equals(getString(R.string.home_label)))
                            finish();
                        break;

                    case 2:
                        intent = new Intent(BaseActivity.this, WaterTaxSearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        if (!getTitle().toString().equals(getString(R.string.home_label)))
                            finish();
                        break;

                    case 3:
                        intent = new Intent(BaseActivity.this, ProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        if (!getTitle().toString().equals(getString(R.string.home_label)))
                            finish();
                        break;

                    case 4:
                        progressDialog.show();
                        ApiController.getAPI(BaseActivity.this).logout(sessionManager.getAccessToken(), new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject jsonObject, Response response) {

                                sessionManager.logoutUser();

                                ApiController.apiInterface = null;

                                Intent intent = new Intent(BaseActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();

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
                        break;

                }

            }
        });
    }

    //Method fills the nav drawer's ArrayList
    private void fillList() {

        arrayList.add(new NavigationItem(R.drawable.ic_feedback_black_24dp, getString(R.string.grievances_label)));
        arrayList.add(new NavigationItem(R.drawable.ic_home_black_24dp, getString(R.string.propertytax_label)));
        arrayList.add(new NavigationItem(R.drawable.ic_water_black_24dp, getString(R.string.watertax_label)));
        arrayList.add(new NavigationItem(R.drawable.ic_person_black_24dp, getString(R.string.profile_label)));
        arrayList.add(new NavigationItem(R.drawable.ic_backspace_black_24dp, getString(R.string.logout_label)));

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

                view.setTag(navigationViewHolder);
            }
            if (navigationViewHolder == null) {
                navigationViewHolder = (NavigationViewHolder) view.getTag();
            }

            NavigationItem navigationItem = getItem(position);
            navigationViewHolder.nav_item_text.setText(navigationItem.getNavTitle());
            navigationViewHolder.nav_item_icon.setImageResource(navigationItem.getNavIcon());

            return view;
        }

    }


    //Allows child classes to access session manager
    protected SessionManager getSessionManager() {
        return sessionManager;
    }


}