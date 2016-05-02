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


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.HomeAdapter;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.CardViewOnClickListener;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.models.HomeItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        List<HomeItem> homeItemList = new ArrayList<>();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.home_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

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

            //check for pgr module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.pgr","true")))
            {
                homeItemList.add(new HomeItem(getString(R.string.grievances_label), R.drawable.ic_error_outline_black_36dp, "File grievances or review and update previously filed grievances"));
            }

            //check for property tax module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.propertytax","true")))
            {
                homeItemList.add(new HomeItem(getString(R.string.propertytax_label), R.drawable.ic_business_black_36dp, "View property tax details for an assessment number"));
            }

            //check for water tax module enabled or not
            if(Boolean.valueOf((String)configManager.get("app.module.watertax","true")))
            {
                homeItemList.add(new HomeItem(getString(R.string.watertax_label), R.drawable.ic_local_drink_black_36dp, "View water tax details for a consumer code"));
            }

            homeItemList.add(new HomeItem(getString(R.string.profile_label), R.drawable.ic_person_black_36dp, "Update or review your profile details."));

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        HomeAdapter homeAdapter = new HomeAdapter(homeItemList, onItemClickCallback);
        recyclerView.setAdapter(homeAdapter);


    }
}
