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


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.HomeAdapter;
import org.egovernments.egoverp.config.Config;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.CardViewOnClickListener;
import org.egovernments.egoverp.models.City;
import org.egovernments.egoverp.models.HomeItem;
import org.egovernments.egoverp.models.NotificationItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends BaseActivity {

    public static String GRIEVANCE_INFO_BROADCAST = "GRIEVANCE-COUNT-INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.home_label);
        setContentViewWithNavBar(R.layout.activity_home, true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupWindowAnimations();
        }

        if (!sessionManager.isTermsAgreed()) {
            showTermsAndCondition();
        } else {
            displayLocationSettingsRequest(HomeActivity.this);
        }

        List<HomeItem> homeItemList = new ArrayList<>();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.home_recyclerview);
        recyclerView.setHasFixedSize(true);

        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);*/

        final int homeGridColumnsCount = getResources().getInteger(R.integer.homegridcolumns);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(HomeActivity.this, homeGridColumnsCount);
        recyclerView.setLayoutManager(gridLayoutManager);

        CardViewOnClickListener.OnItemClickCallback onItemClickCallback = new CardViewOnClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                HomeItem homeItem = ((HomeAdapter) recyclerView.getAdapter()).getItem(position);
                String homeItemTitle = homeItem.getTitle();
                openPage(homeItemTitle);
            }
        };


        City.Modules disabledModules = null;
        if (!TextUtils.isEmpty(sessionManager.getDisabledModulesJson()))
            disabledModules = new Gson().fromJson(sessionManager.getDisabledModulesJson(), City.Modules.class);

        if (!sessionManager.isProfileNotifyDismissed()) {

            NotificationItem.NotificationCallBackInterface notificationCallBackInterface = new NotificationItem.NotificationCallBackInterface() {
                @Override
                public void positiveButtonClicked(int position) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    sessionManager.setProfileNotifyDimissed(true);
                }

                @Override
                public void negativeNegativeButtonClicked(int position) {
                    sessionManager.setProfileNotifyDimissed(true);
                }
            };

            NotificationItem notificationItem = new NotificationItem(Color.parseColor("#227585"), getString(R.string.update_your_profile),
                    getString(R.string.update_profile_message), getString(R.string.update_now), getString(R.string.later), notificationCallBackInterface);
            homeItemList.add(new HomeItem(getString(R.string.profile_label), R.drawable.ic_person_black_36dp, "Update or review your profile details.", true, notificationItem));
        }

        //check for pgr module enabled or not
        if (Boolean.valueOf((String) configManager.get(Config.Modules.PGR, "true"))
                && (disabledModules == null || !disabledModules.isPgrDisable())) {
            HomeItem grievanceItem = new HomeItem(getString(R.string.grievances_label), getString(R.string.grievances_label2), R.drawable.ic_archive_black_36dp,
                    "File grievances or review and update previously filed grievances", ContextCompat.getColor(HomeActivity.this, R.color.grievance_color));
            //grievanceItem.setGrievanceItem(true);
            homeItemList.add(grievanceItem);
        }

        //check for property tax module enabled or not
        if (Boolean.valueOf((String) configManager.get(Config.Modules.PROPERTY_TAX, "true"))
                && (disabledModules == null || !disabledModules.isPropertyTaxDisable())) {
            homeItemList.add(new HomeItem(getString(R.string.propertytax_label), getString(R.string.propertytax_label2), R.drawable.ic_business_black_36dp,
                    "View property tax details for an assessment number", ContextCompat.getColor(HomeActivity.this, R.color.propertytax_color)));
        }

        //check for vacant land tax module enabled or not
        if (Boolean.valueOf((String) configManager.get(Config.Modules.VACANT_LAND_TAX, "true"))
                && (disabledModules == null || !disabledModules.isVacantLandTaxDisable())) {
            homeItemList.add(new HomeItem(getString(R.string.vacantlandtax_label), getString(R.string.vacantlandtax_label2), R.drawable.ic_vacant_land_36dp,
                    "", ContextCompat.getColor(HomeActivity.this, R.color.vacand_land_color)));
        }

        //check for water tax module enabled or not
        if (Boolean.valueOf((String) configManager.get(Config.Modules.WATER_CHARGE, "true"))
                && (disabledModules == null || !disabledModules.isWaterChargeDisable())) {
            homeItemList.add(new HomeItem(getString(R.string.watertax_label), getString(R.string.watertax_label2), R.drawable.ic_water_tab_black_36dp,
                    "View water tax details for a consumer code", ContextCompat.getColor(HomeActivity.this, R.color.watertax_color)));
        }


        if (Boolean.valueOf((String) configManager.get(Config.Modules.BPA, "true"))
                && (disabledModules == null || !disabledModules.isBPADisable())) {
            homeItemList.add(new HomeItem(getString(R.string.building_plan_label), getString(R.string.building_plan_label2),
                    R.drawable.ic_town_plan_36dp, "", ContextCompat.getColor(HomeActivity.this, R.color.bpacolor)));
        }

        if (Boolean.valueOf((String) configManager.get(Config.Modules.BPS, "true"))
                && (disabledModules == null || !disabledModules.isBPSDisable())) {
            homeItemList.add(new HomeItem(getString(R.string.building_penalization_label), getString(R.string.building_penalization_label2), R.drawable.ic_location_city_black_36dp,
                    "", ContextCompat.getColor(HomeActivity.this, R.color.bpcolor)));
        }

            /*if(Boolean.valueOf((String)configManager.get("app.module.birthdeathcertificate","true")))
            {
                homeItemList.add(new HomeItem(getString(R.string.birth_death_certificate_label), getString(R.string.birth_death_certificate_label2), R.drawable.ic_certificate_36dp,
                        "", ContextCompat.getColor(HomeActivity.this, R.color.birthdeathcolor)));
            }*/

        if (Boolean.valueOf((String) configManager.get(Config.Modules.CITIZEN_CHARTER, "true"))
                && (disabledModules == null || !disabledModules.isCitizenCharterDisable())) {
            homeItemList.add(new HomeItem(getString(R.string.citizen_charter_label), getString(R.string.citizen_charter_label2), R.drawable.ic_grid_on_black_36dp,
                    "", ContextCompat.getColor(HomeActivity.this, R.color.citizen_charter_color)));
        }

        if (Boolean.valueOf((String) configManager.get(Config.Modules.SOS, "true"))
                && (disabledModules == null || !disabledModules.isSOSDisable())) {
            homeItemList.add(new HomeItem(getString(R.string.sos_label), getString(R.string.sos_label2), R.drawable.ic_call_black_36dp,
                    "", ContextCompat.getColor(HomeActivity.this, R.color.sos_color)));
        }

        if (Boolean.valueOf((String) configManager.get(Config.Modules.SLA, "true"))
                && (disabledModules == null || !disabledModules.isSLADisable())) {
            homeItemList.add(new HomeItem(getString(R.string.sla_label), getString(R.string.sla_label2), R.drawable.ic_access_time_white_36dp,
                    "", ContextCompat.getColor(HomeActivity.this, R.color.sla_color)));
        }

        if (Boolean.valueOf((String) configManager.get(Config.Modules.ABOUT_US, "true"))
                && (disabledModules == null || !disabledModules.isAboutUsDisable())) {
            homeItemList.add(new HomeItem(getString(R.string.aboutus_label), getString(R.string.aboutus_label2), R.drawable.ic_info_black_36dp,
                    "", ContextCompat.getColor(HomeActivity.this, R.color.aboutus_color)));
        }

        final HomeAdapter homeAdapter = new HomeAdapter(HomeActivity.this, homeItemList, onItemClickCallback);
        recyclerView.setAdapter(homeAdapter);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (homeAdapter.getItem(position).isNotificationItem()) {
                    return homeGridColumnsCount;
                }
                return 1;
            }
        });

    }

    public void showTermsAndCondition() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);

        final ViewGroup nullParent = null;
        View viewTermsAndConditions = getLayoutInflater().inflate(R.layout.layout_terms_conditions, nullParent, false);
        final ProgressBar progressBar = (ProgressBar) viewTermsAndConditions.findViewById(R.id.progressBar);
        WebView termsAndConditionWV = (WebView) viewTermsAndConditions.findViewById(R.id.termsAndConditionWV);
        termsAndConditionWV.loadUrl(configManager.getString(Config.APP_TERMS_AND_CONS_URL));
        termsAndConditionWV.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
        alert.setView(viewTermsAndConditions);
        alert.setPositiveButton(R.string.i_agree, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                sessionManager.setTermsAgreed(true);
                dialog.dismiss();
                displayLocationSettingsRequest(HomeActivity.this);
            }
        });
        alert.setNegativeButton(R.string.dont_agree, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        recreate();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Slide enterTransition = new Slide();
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        enterTransition.setSlideEdge(Gravity.BOTTOM);
        getWindow().setEnterTransition(enterTransition);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_language);
        LinearLayout rootView = (LinearLayout) item.getActionView();
        TextView tvAppLocale = (TextView) rootView.findViewById(R.id.tvAppLocale);
        try {
            //condition for check multi-language support is there
            if (AppUtils.getSupportedLocalesCode(configManager).length > 1) {
                final PopupMenu popupMenu = createLanguagePopUpMenu(tvAppLocale);
                if (TextUtils.isEmpty(sessionManager.getAppLocale())) {
                    tvAppLocale.setText(" " + getResources().getConfiguration().locale.getDisplayLanguage());
                } else {
                    tvAppLocale.setText(" " + AppUtils.getLanguageDisplayNameByLocaleCode(sessionManager.getAppLocale()));
                }

                tvAppLocale.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupMenu.show();
                    }
                });
            } else {
                //no multi-language support
                tvAppLocale.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    //create multi-language popup menu
    PopupMenu createLanguagePopUpMenu(View view) {
        PopupMenu menu = new PopupMenu(HomeActivity.this, view);

        try {
            final Map<String, String> languages = AppUtils.getSupportedLanguagesList(configManager);
            menu.setGravity(Gravity.RIGHT);
            for (String languageName : languages.keySet()) {
                menu.getMenu().add(languageName);
            }

            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    /*showSnackBar(languages.get(item.getTitle().toString()));*/
                    setAppLocale(languages.get(item.getTitle().toString()));
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return menu;
    }


}
