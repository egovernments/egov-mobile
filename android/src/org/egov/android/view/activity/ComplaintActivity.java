package org.egov.android.view.activity;

import org.egov.android.R;
import org.egov.android.view.SlidingTab.SlidingTabLayout;
import org.egov.android.view.adapter.SlidingTabAdapter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

public class ComplaintActivity extends BaseFragmentActivity {

    ViewPager pager;
    int numberOfTabs = 2;
    SlidingTabLayout tabs;
    SlidingTabAdapter slidingTabAdapter;
    CharSequence titles[] = { "Complaints", "Me" };

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
        pager.setCurrentItem(1);
    }
}
