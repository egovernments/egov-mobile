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
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.HomeAdapter;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.CardViewOnClickListener;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.models.HomeItem;
import org.egovernments.egoverp.models.NotificationItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    public static String GRIEVANCE_INFO_BROADCAST="GRIEVANCE-COUNT-INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        if(!sessionManager.isTermsAgreed())
        {
            showTermsAndCondition();
        }

        List<HomeItem> homeItemList = new ArrayList<>();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.home_recyclerview);
        recyclerView.setHasFixedSize(true);
        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);*/

        GridLayoutManager gridLayoutManager=new GridLayoutManager(HomeActivity.this, getResources().getInteger(R.integer.homegridcolumns));
        recyclerView.setLayoutManager(gridLayoutManager);

        CardViewOnClickListener.OnItemClickCallback onItemClickCallback = new CardViewOnClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                HomeItem homeItem = ((HomeAdapter)recyclerView.getAdapter()).getItem(position);
                String homeItemTitle=homeItem.getTitle();
                openPage(homeItemTitle);
            }
        };

        try
        {

            ConfigManager configManager= AppUtils.getConfigManager(getApplicationContext());

            if(!sessionManager.isProfileNotifyDismissed()) {

                NotificationItem.NotificationCallBackInterface notificationCallBackInterface=new NotificationItem.NotificationCallBackInterface() {
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

                NotificationItem notificationItem = new NotificationItem(Color.parseColor("#0288D1"), "Update your profile", "Please, update your profile details.", "UPDATE NOW", "LATER", notificationCallBackInterface);
                homeItemList.add(new HomeItem(getString(R.string.profile_label), R.drawable.ic_person_black_36dp, "Update or review your profile details.", true, notificationItem));
            }

            //check for pgr module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.pgr","true")))
            {
                HomeItem grievanceItem=new HomeItem(getString(R.string.grievances_label), getString(R.string.grievances_label2), R.drawable.ic_archive_black_36dp, "File grievances or review and update previously filed grievances", getResources().getColor(R.color.grievance_color));
                //grievanceItem.setGrievanceItem(true);
                homeItemList.add(grievanceItem);

            }

            //check for property tax module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.propertytax","true")))
            {
                homeItemList.add(new HomeItem(getString(R.string.propertytax_label), getString(R.string.propertax_label2), R.drawable.ic_business_black_36dp, "View property tax details for an assessment number", getResources().getColor(R.color.propertytax_color)));
            }

            //check for vacant land tax module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.vacantlandtax","true")))
            {
                homeItemList.add(new HomeItem(getString(R.string.vacantlandtax_label), getString(R.string.vacantlandtax_label2), R.drawable.ic_vacant_land_36dp, "", getResources().getColor(R.color.vacand_land_color)));
            }

            //check for water tax module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.watertax","true")))
            {
                homeItemList.add(new HomeItem(getString(R.string.watertax_label), getString(R.string.watertax_label2), R.drawable.ic_water_tab_black_36dp, "View water tax details for a consumer code", getResources().getColor(R.color.watertax_color)));
            }


            if(Boolean.valueOf((String)configManager.get("app.module.buildingplanapproval","true")))
            {
                homeItemList.add(new HomeItem(getString(R.string.building_plan_label), getString(R.string.building_plan_label2), R.drawable.ic_town_plan_36dp, "", getResources().getColor(R.color.bpacolor)));
            }

            if(Boolean.valueOf((String)configManager.get("app.module.buildingpenalization","true")))
            {
                homeItemList.add(new HomeItem(getString(R.string.building_penalization_label), getString(R.string.building_penalization_label2), R.drawable.ic_location_city_black_36dp, "", getResources().getColor(R.color.bpcolor)));
            }

            if(Boolean.valueOf((String)configManager.get("app.module.birthdeathcertificate","true")))
            {
                homeItemList.add(new HomeItem(getString(R.string.birth_death_certificate_label), getString(R.string.birth_death_certificate_label2), R.drawable.ic_certificate_36dp, "", getResources().getColor(R.color.birthdeathcolor)));
            }

            if(Boolean.valueOf((String)configManager.get("app.module.citizencharter","true")))
            {
                homeItemList.add(new HomeItem(getString(R.string.citizen_charter_label), getString(R.string.citizen_charter_label2), R.drawable.ic_grid_on_black_36dp, "", getResources().getColor(R.color.citizen_charter_color)));
            }


        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        final HomeAdapter homeAdapter = new HomeAdapter(HomeActivity.this, homeItemList, onItemClickCallback);
        recyclerView.setAdapter(homeAdapter);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(homeAdapter.getItem(position).isNotificationItem()){
                  return 2;
                }
                return 1;
            }
        });

    }

    public void showTermsAndCondition()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);

        View viewTermsAndConditions=getLayoutInflater().inflate(R.layout.layout_terms_conditions, null, false);

       /* wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });*/

        alert.setView(viewTermsAndConditions);

        alert.setPositiveButton(R.string.i_agree, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                sessionManager.setTermsAgreed(true);
                dialog.dismiss();
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
}
