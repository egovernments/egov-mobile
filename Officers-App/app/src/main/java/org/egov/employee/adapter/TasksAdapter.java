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

package org.egov.employee.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.google.gson.JsonArray;

import org.egov.employee.fragment.TasksFragment;

/**
 * Created by egov on 14/12/15.
 */
public class TasksAdapter extends FragmentStatePagerAdapter {

    JsonArray tasksList;
    private static String TAG=TasksAdapter.class.getName().toString();
    private String accessToken;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public TasksAdapter(FragmentManager fm, JsonArray tasksList, String accessToken) {
        super(fm);
        this.tasksList=tasksList;
        this.accessToken=accessToken;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("workFlowType", tasksList.get(position).getAsJsonObject().get("workflowtype").getAsString());
        bundle.putInt("itemsCount", tasksList.get(position).getAsJsonObject().get("inboxlistcount").getAsInt());
        bundle.putString("accessToken", accessToken);

        TasksFragment taskstab = new TasksFragment();
        taskstab.setArguments(bundle);
        return taskstab;

    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        try
        {
            return tasksList.get(position).getAsJsonObject().get("workflowtypename").getAsString().toUpperCase();
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

    public String getBadgeCount(int position) {
        try
        {
            return String.valueOf(tasksList.get(position).getAsJsonObject().get("inboxlistcount").getAsInt());
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return tasksList.size();
    }
}
