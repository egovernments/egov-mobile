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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.HomeAdapter;
import org.egovernments.egoverp.helper.CardViewOnClickListener;
import org.egovernments.egoverp.models.HomeItem;

import java.util.ArrayList;
import java.util.List;

public class CitizenCharterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithNavBar(R.layout.activity_citizen_charter, true);

        final List<HomeItem> homeItemList = new ArrayList<>();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.menu_recyclerview);
        recyclerView.setHasFixedSize(true);
        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);*/

        GridLayoutManager gridLayoutManager=new GridLayoutManager(CitizenCharterActivity.this, getResources().getInteger(R.integer.homegridcolumns));
        recyclerView.setLayoutManager(gridLayoutManager);

        homeItemList.add(new HomeItem(getString(R.string.revenue_section), R.drawable.ic_rupee_24dp, "", ContextCompat.getColor(this, R.color.propertytax_color)));
        homeItemList.add(new HomeItem(getString(R.string.engineering_secion), R.drawable.ic_build_black_36dp, "", ContextCompat.getColor(this, R.color.watertax_color)));
        homeItemList.add(new HomeItem(getString(R.string.health_section), R.drawable.ic_heartbeat_36dp, "", ContextCompat.getColor(this, R.color.grievance_color)));
        homeItemList.add(new HomeItem(getString(R.string.town_planing_section), R.drawable.ic_town_plan_36dp, "", ContextCompat.getColor(this, R.color.vacand_land_color)));



        CardViewOnClickListener.OnItemClickCallback onItemClickCallback = new CardViewOnClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {

                String pdfUrl="";

                if(position==0)
                {
                    pdfUrl="http://egovernments.org/egov-apps/puraseva/citizen-charter/revenue-section.pdf";
                }
                else if(position==1)
                {
                    pdfUrl="http://egovernments.org/egov-apps/puraseva/citizen-charter/engineering-section.pdf";
                }
                else if(position==2)
                {
                    pdfUrl="http://egovernments.org/egov-apps/puraseva/citizen-charter/health-section.pdf";
                }
                else if(position==3)
                {
                    pdfUrl="http://egovernments.org/egov-apps/puraseva/citizen-charter/town-planing-section.pdf";
                }

                Intent openPdfViewer=new Intent(CitizenCharterActivity.this, PdfViewerActivity.class);
                openPdfViewer.putExtra(PdfViewerActivity.PAGE_TITLE, homeItemList.get(position).getTitle());
                openPdfViewer.putExtra(PdfViewerActivity.PDF_URL, pdfUrl);
                startActivity(openPdfViewer);

            }
        };


        final HomeAdapter homeAdapter = new HomeAdapter(CitizenCharterActivity.this, homeItemList, onItemClickCallback);
        recyclerView.setAdapter(homeAdapter);


    }

}
