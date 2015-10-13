/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.view.activity;

import org.egov.android.R;
import org.egov.android.controller.ServiceController;
import org.egov.android.view.SlidingTab.SlidingTabLayout;
import org.egov.android.view.adapter.SlidingTabAdapter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

public class ComplaintActivity extends BaseFragmentActivity {

    ViewPager pager;
    int numberOfTabs = 1;
    /*int numberOfTabs = 2;*/
    SlidingTabLayout tabs;
    SlidingTabAdapter slidingTabAdapter;
    CharSequence titles[] = { "Me" };
    /*CharSequence titles[] = { "Complaints", "Me" };*/


    /**
     * It is  used to initialize an activity.
     * An Activity is an application component that provides a screen 
     * with which users can interact in order to do something,
     * To initialize and set the layout for the ComplaintActivity.
     * This activity acts as a sliding tab. Here we have two tabs, 
     * each tab contain separate activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        ((TextView) findViewById(R.id.hdr_title)).setPadding(25, 0, 0, 0);

        slidingTabAdapter = new SlidingTabAdapter(getSupportFragmentManager(), titles, numberOfTabs);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(slidingTabAdapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(pager);
        pager.setCurrentItem(0);
        
        /*pager.setCurrentItem(1);*/
    }
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	//ServiceController.getInstance().startService(this);
    }
    
}
