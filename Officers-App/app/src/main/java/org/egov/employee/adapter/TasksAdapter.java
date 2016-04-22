package org.egov.employee.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.google.gson.JsonArray;

import org.egov.employee.fragment.TasksFragment;

import java.util.ArrayList;
import java.util.HashMap;

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
