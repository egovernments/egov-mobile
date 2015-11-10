package com.egovernments.egov.activities;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.egovernments.egov.R;
import com.egovernments.egov.adapters.GrievanceAdapter;
import com.egovernments.egov.events.GrievancesUpdatedEvent;
import com.egovernments.egov.helper.CardViewOnClickListener;
import com.egovernments.egov.models.Grievance;
import com.egovernments.egov.network.UpdateService;

import java.util.List;

import de.greenrobot.event.EventBus;


public class GrievanceActivity extends BaseActivity {

    public static List<Grievance> grievanceList;

    private CardViewOnClickListener.OnItemClickCallback onItemClickCallback;

    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    public static GrievanceAdapter grievanceAdapter;

    private android.support.v4.widget.SwipeRefreshLayout swipeRefreshLayout;

    private int pageNo = 1;

    public static int pageLoaded = 0;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;

    private final int ACTION_UPDATE_REQUIRED = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recyclerview_template);

        progressBar = (ProgressBar) findViewById(R.id.recylerview_placeholder);

        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int firstVisibleItem, visibleItemCount, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;

                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    // Do something
                    if (pageNo >= pageLoaded) {
                        pageNo++;
                        Intent intent = new Intent(GrievanceActivity.this, UpdateService.class);
                        intent.putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_COMPLAINTS);
                        intent.putExtra(UpdateService.COMPLAINTS_PAGE, String.valueOf(pageNo));
                        startService(intent);
                        loading = true;
                    }
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.recylerview_refreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.GONE);
                pageNo = 1;
                Intent intent = new Intent(GrievanceActivity.this, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_COMPLAINTS);
                intent.putExtra(UpdateService.COMPLAINTS_PAGE, "1");
                startService(intent);
                grievanceAdapter = null;
            }
        });


        onItemClickCallback = new CardViewOnClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {

                Intent intent = new Intent(GrievanceActivity.this, GrievanceDetailsActivity.class);
                intent.putExtra(GrievanceDetailsActivity.GRIEVANCE_ITEM, grievanceList.get(position));
                startActivityForResult(intent,ACTION_UPDATE_REQUIRED);

            }
        };

        if (grievanceList != null) {
            pageLoaded++;
            progressBar.setVisibility(View.GONE);
            grievanceAdapter = new GrievanceAdapter(GrievanceActivity.this, grievanceList, onItemClickCallback);
            recyclerView.setAdapter(grievanceAdapter);
        }


        FloatingActionButton newComplaintButton = (FloatingActionButton) findViewById(R.id.list_fab);
        com.melnykov.fab.FloatingActionButton newComplaintButtonCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.list_fabcompat);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(GrievanceActivity.this, NewGrievanceActivity.class), ACTION_UPDATE_REQUIRED);

            }
        };

        if (Build.VERSION.SDK_INT >= 21) {

            newComplaintButton.setOnClickListener(onClickListener);
        } else {
            newComplaintButton.setVisibility(View.GONE);
            newComplaintButtonCompat.setVisibility(View.VISIBLE);

            newComplaintButtonCompat.setOnClickListener(onClickListener);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_UPDATE_REQUIRED && resultCode == RESULT_OK) {
            progressBar.setVisibility(View.GONE);
            pageNo = 1;
            Intent intent = new Intent(GrievanceActivity.this, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_COMPLAINTS);
            intent.putExtra(UpdateService.COMPLAINTS_PAGE, "1");
            startService(intent);
            grievanceAdapter = null;
        }

    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @SuppressWarnings("unused")
    public void onEvent(GrievancesUpdatedEvent grievancesUpdatedEvent) {

        if (grievanceAdapter == null) {
            pageLoaded = 1;
            previousTotal = 0;
            progressBar.setVisibility(View.GONE);
            grievanceAdapter = new GrievanceAdapter(GrievanceActivity.this, grievanceList, onItemClickCallback);
            recyclerView.setAdapter(grievanceAdapter);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            pageLoaded++;
            grievanceAdapter.notifyDataSetChanged();
        }


    }
}

