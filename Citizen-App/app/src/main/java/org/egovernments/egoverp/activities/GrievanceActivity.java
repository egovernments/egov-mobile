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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.fragments.GrievanceFragment;
import org.egovernments.egoverp.network.ApiUrl;

/**
 * The activity containing grievance list
 **/


public class GrievanceActivity extends BaseActivity {

    public static int ACTION_UPDATE_REQUIRED = 111;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTabs(R.layout.activity_grievance);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(new GrievanceFragmentPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        com.melnykov.fab.FloatingActionButton newComplaintButtonCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.list_fabcompat);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Activity refreshes if NewGrievanceActivity finishes with success
                startActivityForResult(new Intent(GrievanceActivity.this, NewGrievanceActivity.class), ACTION_UPDATE_REQUIRED);

            }
        };
        newComplaintButtonCompat.setOnClickListener(onClickListener);
        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount()-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grievance_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh)
        {
            refreshGrievanceViewPager();
        }

        return super.onOptionsItemSelected(item);
    }

    //Handles result when NewGrievanceActivity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_UPDATE_REQUIRED && resultCode == RESULT_OK) {
           refreshGrievanceViewPager();
        }

    }

    public void refreshGrievanceViewPager()
    {
        int selectedIdx=viewPager.getCurrentItem();
        viewPager.setAdapter(new GrievanceFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount()-1);
        viewPager.setCurrentItem(selectedIdx);
    }

    private class GrievanceFragmentPagerAdapter extends FragmentPagerAdapter {

        String[] titles={"ALL", "PENDING", "COMPLETED", "REJECTED"};

        public GrievanceFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return GrievanceFragment.instantiateItem(sessionManager.getAccessToken(), titles[position], position, sessionManager.getBaseURL()+ ApiUrl.COMPLAINT_DOWNLOAD_IMAGE);
        }
    }


}

