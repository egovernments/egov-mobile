package com.egovernments.egov.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.egovernments.egov.R;
import com.egovernments.egov.helper.BadgeUpdater;
import com.egovernments.egov.network.ApiController;
import com.egovernments.egov.network.SessionManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BaseActivity extends AppCompatActivity {

    protected LinearLayout fullLayout;
    protected FrameLayout actContent;

    protected ArrayList<NavItem> arrayList;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected CharSequence mTitle;

    protected SessionManager sessionManager;

    protected ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baselayout);


    }

    @Override
    public void setContentView(final int layoutResID) {

        fullLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.baselayout, null);
        actContent = (FrameLayout) fullLayout.findViewById(R.id.drawer_content);

        getLayoutInflater().inflate(layoutResID, actContent, true);

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

        mTitle = actionBar.getTitle();

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {

                actionBar.setTitle(mTitle);
            }

            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(R.string.menu_label);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);


        ListView lv = (ListView) findViewById(R.id.drawer);

        arrayList = new ArrayList<>();
        fillList();

        BaseAdapter navAdapter = new NavAdapter(arrayList);

        lv.setAdapter(navAdapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        intent = new Intent(BaseActivity.this, PropertyActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        if (!getTitle().toString().equals(getString(R.string.home_label)))
                            finish();
                        break;

                    case 6:
                        intent = new Intent(BaseActivity.this, ProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        if (!getTitle().toString().equals(getString(R.string.home_label)))
                            finish();
                        break;

                    case 7:
                        intent = new Intent(BaseActivity.this, NotificationsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        if (!getTitle().toString().equals(getString(R.string.home_label)))
                            finish();
                        break;

                    case 9:
                        progressDialog.show();
                        ApiController.getAPI().logout(sessionManager.getAccessToken(), new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject jsonObject, Response response) {

                                Toast.makeText(BaseActivity.this, R.string.logged_out_msg, Toast.LENGTH_SHORT).show();

                                sessionManager.logoutUser();

                                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();

                            }

                            @Override
                            public void failure(RetrofitError error) {

                                JsonObject jsonObject = null;

                                if (error != null) {
                                    try {
                                        jsonObject = (JsonObject) error.getBodyAs(JsonObject.class);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (jsonObject != null) {
                                        String s = jsonObject.get("error").toString().trim();
                                        if (s.contains("invalid_token")) {
                                            Toast.makeText(BaseActivity.this, R.string.logged_out_msg, Toast.LENGTH_SHORT).show();

                                            sessionManager.logoutUser();

                                            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            progressDialog.dismiss();
                                            finish();
                                        }

                                    }
                                } else {
                                    Toast.makeText(BaseActivity.this, R.string.no_connection_msg, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                        break;

                }

            }
        });
    }

    private void fillList() {

        arrayList.add(new NavItem(R.drawable.ic_feedback_black_24dp, getString(R.string.grievances_label)));
        arrayList.add(new NavItem(R.drawable.ic_home_black_24dp, getString(R.string.propertytax_label)));
        arrayList.add(new NavItem(R.drawable.ic_location_city_black_24dp, getString(R.string.buildingplans_label)));
        arrayList.add(new NavItem(R.drawable.ic_accessibility_black_24dp, getString(R.string.births_deaths_label)));
        arrayList.add(new NavItem(R.drawable.ic_business_black_24dp, getString(R.string.ads_label)));
        arrayList.add(new NavItem(R.drawable.ic_account_balance_black_24dp, getString(R.string.shops_label)));
        arrayList.add(new NavItem(R.drawable.ic_person_black_24dp, getString(R.string.profile_label)));
        arrayList.add(new NavItem(R.drawable.ic_notifications_black_24dp, getString(R.string.notifs_label)));
        arrayList.add(new NavItem(R.drawable.ic_settings_black_24dp, getString(R.string.settings_label)));
        arrayList.add(new NavItem(R.drawable.ic_backspace_black_24dp, getString(R.string.logout_label)));

    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.baselayout_actions, menu);
        MenuItem item = menu.findItem(R.id.badge);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        if (Build.VERSION.SDK_INT >= 21) {

//        Update LayerDrawable BadgeDrawable
            BadgeUpdater.setBadgeCount(this, icon, NotificationsActivity.getCount());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.badge:
                Intent intent = new Intent(BaseActivity.this, NotificationsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private class NavItem {

        private final int NavIcon;
        private final String NavTitle;

        public NavItem(int navIcon, String navTitle) {
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


    public class NavAdapter extends BaseAdapter {
        List<NavItem> navItems;

        public NavAdapter(List<NavItem> musicList) {
            this.navItems = musicList;
        }

        @Override
        public int getCount() {
            return navItems.size();
        }

        @Override
        public NavItem getItem(int position) {
            return navItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.item_navdrawer, parent, false);

            ImageView nav_item_ico;
            TextView nav_item_text;

            nav_item_text = (TextView) row.findViewById(R.id.title);
            nav_item_ico = (ImageView) row.findViewById(R.id.icon);

            NavItem navItem = getItem(position);
            nav_item_text.setText(navItem.getNavTitle());
            nav_item_ico.setImageResource(navItem.getNavIcon());

            return row;
        }
    }

    protected SessionManager getSessionManager() {
        return sessionManager;
    }


}