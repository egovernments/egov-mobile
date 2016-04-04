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


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.GrievanceAdapter;
import org.egovernments.egoverp.events.GrievanceUpdateFailedEvent;
import org.egovernments.egoverp.events.GrievancesUpdatedEvent;
import org.egovernments.egoverp.helper.CardViewOnClickListener;
import org.egovernments.egoverp.helper.EndlessRecyclerOnScrollListener;
import org.egovernments.egoverp.models.Grievance;
import org.egovernments.egoverp.network.UpdateService;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * The activity containing grievance list
 **/


public class GrievanceActivity extends BaseActivity {

    public static List<Grievance> grievanceList;

    private CardViewOnClickListener.OnItemClickCallback onItemClickCallback;

    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    public static GrievanceAdapter grievanceAdapter;

    private android.support.v4.widget.SwipeRefreshLayout swipeRefreshLayout;

    //The currently visible page no.
    private int pageNo = 1;

    private final int ACTION_UPDATE_REQUIRED = 111;

    private boolean loading = true;
    private boolean paginationEnded = false;
    public static boolean isUpdateFailed = false;
    EndlessRecyclerOnScrollListener onScrollListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_grievance);

        progressBar = (ProgressBar) findViewById(R.id.grievance_recylerview_placeholder);
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        onScrollListener=new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(!loading && !paginationEnded) {
                    pageNo++;
                    loading = true;
                    Intent intent = new Intent(GrievanceActivity.this, UpdateService.class);
                    intent.putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_COMPLAINTS);
                    intent.putExtra(UpdateService.COMPLAINTS_PAGE, String.valueOf(pageNo));
                    startService(intent);
                }
            }
        };

        //Enables infinite scrolling (pagination)
        recyclerView.addOnScrollListener(onScrollListener);

        //Enables pull to refresh
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.recylerview_refreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.GONE);
                refreshGrievanceList();
            }
        });

        //CardView on click listener
        onItemClickCallback = new CardViewOnClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {

                Intent intent = new Intent(GrievanceActivity.this, GrievanceDetailsActivity.class);
                intent.putExtra(GrievanceDetailsActivity.GRIEVANCE_ITEM, grievanceList.get(position));
                startActivityForResult(intent, ACTION_UPDATE_REQUIRED);

            }
        };

        //Checks if the update service has fetched complaints before setting up list
        if (grievanceList != null) {
            progressBar.setVisibility(View.GONE);
            grievanceAdapter = new GrievanceAdapter(GrievanceActivity.this, grievanceList, onItemClickCallback);
            recyclerView.setAdapter(grievanceAdapter);
        } else {
            grievanceList = new ArrayList<>();
            grievanceAdapter = new GrievanceAdapter(GrievanceActivity.this, grievanceList, onItemClickCallback);
            recyclerView.setAdapter(grievanceAdapter);
            grievanceList = null;
        }

        FloatingActionButton newComplaintButton = (FloatingActionButton) findViewById(R.id.list_fab);
        com.melnykov.fab.FloatingActionButton newComplaintButtonCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.list_fabcompat);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Activity refreshes if NewGrievanceActivity finishes with success
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

        if (isUpdateFailed) {
            progressBar.setVisibility(View.GONE);
            isUpdateFailed = false;
        }


        refreshGrievanceList();


    }

    //Handles result when NewGrievanceActivity finishes
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

    //Subscribes the activity to events
    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    //Unsubscribes the activity to events
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    //Updates the complaint list when subscribed and a GrievancesUpdatedEvent is posted by the UpdateService
    @SuppressWarnings("unused")
    public void onEvent(GrievancesUpdatedEvent grievancesUpdatedEvent) {

        paginationEnded=grievancesUpdatedEvent.isPaginationEnded();
        loading=false;

        if(grievancesUpdatedEvent.isSendRequest() && (grievanceAdapter==null || (grievanceAdapter.getItemCount()==0)))
        {
            progressBar.setVisibility(View.VISIBLE);
            return;
        }
        //If a refresh action has been taken, reinitialize the list
        if (grievanceAdapter == null) {
            pageNo = 1;
            grievanceAdapter = new GrievanceAdapter(GrievanceActivity.this, grievanceList, onItemClickCallback);
            recyclerView.setAdapter(grievanceAdapter);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            grievanceAdapter.notifyItemInserted(grievanceList.size());
        }
        progressBar.setVisibility(View.GONE);
    }

    @SuppressWarnings("unused")
    public void onEvent(GrievanceUpdateFailedEvent grievanceUpdateFailedEvent) {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        grievanceList = new ArrayList<>();
        grievanceAdapter = new GrievanceAdapter(GrievanceActivity.this, grievanceList, onItemClickCallback);
        recyclerView.setAdapter(grievanceAdapter);
        grievanceList = null;
    }

    public void refreshGrievanceList()
    {
        loading=true;
        paginationEnded=false;
        pageNo=1;
        onScrollListener.resetScrollListenerValues();
        Intent intent = new Intent(GrievanceActivity.this, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_COMPLAINTS);
        intent.putExtra(UpdateService.COMPLAINTS_PAGE, "1");
        startService(intent);
    }
}

