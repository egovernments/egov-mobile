package org.egov.android.view.adapter;

import org.egov.android.view.activity.AllComplaintActivity;
import org.egov.android.view.activity.UserComplaintActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SlidingTabAdapter extends FragmentStatePagerAdapter {

    CharSequence titles[];
    int numberOfTabs;

    public SlidingTabAdapter(FragmentManager fm, CharSequence titles[], int numberOfTabs) {
        super(fm);
        this.titles = titles;
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new AllComplaintActivity();
            case 1:
                return new UserComplaintActivity();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
