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

package org.egov.employee.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.egov.employee.activity.Homepage;
import org.egov.employee.adapter.TasksAdapter;
import org.egov.employee.adapter.TasksListAdapater;
import org.egov.employee.api.ApiController;
import org.egov.employee.data.Task;
import org.egov.employee.data.TaskAPIResponse;
import org.egov.employee.interfaces.TasksItemClickListener;
import org.egov.employee.utils.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import offices.org.egov.egovemployees.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Task Fragment by work category
 */
public class TasksFragment extends Fragment implements TasksItemClickListener.TaskItemClickedIndex {

    RecyclerView recyclerviewTasks;
    Toolbar mToolbar;
    AppBarLayout appBarLayout;
    TasksItemClickListener listener;
    List<Task> tasks;
    String workflowtype;
    String accessToken;
    String priority;
    int totalItemsCount;
    int pagePerItems=10;
    int currentPage=1;
    boolean _areItemsLoaded = false;
    TasksListAdapater taskListAdapater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_tasks, container, false);

        mToolbar=(Toolbar)getActivity().findViewById(R.id.toolbar);
        appBarLayout=(AppBarLayout)getActivity().findViewById(R.id.homepagebarlayout);

        recyclerviewTasks=(RecyclerView)v.findViewById(R.id.tasks_list);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerviewTasks.setLayoutManager(layoutManager);

        recyclerviewTasks.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                //add progress item
                loadInboxListItems();
            }
        });


        if(getUserVisibleHint() && !_areItemsLoaded)
        {
            workflowtype = getArguments().getString(TasksAdapter.WORK_FLOW_TYPE);
            totalItemsCount = getArguments().getInt(TasksAdapter.ITEMS_COUNT);
            accessToken = getArguments().getString(TasksAdapter.ACCESS_TOKEN);
            priority = getArguments().getString(TasksAdapter.PRIORITY_VALUE);
            tasks = new ArrayList<>();
            _areItemsLoaded=true;
            loadInboxListItems();
        }

        return v;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && !_areItemsLoaded && getActivity()!=null)
        {
            workflowtype = getArguments().getString(TasksAdapter.WORK_FLOW_TYPE);
            totalItemsCount = getArguments().getInt(TasksAdapter.ITEMS_COUNT);
            accessToken = getArguments().getString(TasksAdapter.ACCESS_TOKEN);
            priority = getArguments().getString(TasksAdapter.PRIORITY_VALUE);
            tasks = new ArrayList<>();
            _areItemsLoaded=true;
            loadInboxListItems();
        }
        else if(isVisibleToUser)
        {
            if(taskListAdapater!=null)
            {
                tasks=taskListAdapater.getTasks();
            }
        }
    }


    public void loadInboxListItems()
    {

        String currentMethodName=Thread.currentThread().getStackTrace()[2].getMethodName();

        if(currentPage ==1)
        {
            ((Homepage)getActivity()).homePageLoader.setVisibility(View.VISIBLE);
        }

        if(tasks.size() < totalItemsCount) {

            if(taskListAdapater!=null) {

                if(tasks.get(tasks.size()-1) == null)
                {
                    tasks.remove(tasks.size() - 1);
                    taskListAdapater.notifyItemRemoved(tasks.size());
                }

                tasks.add(null);
                taskListAdapater.notifyItemInserted(tasks.size());
            }

            if(((Homepage)getActivity()).checkInternetConnectivity(TasksFragment.this, currentMethodName)) {

                Call<TaskAPIResponse> getInboxList = ApiController.getAPI(getActivity().getApplicationContext()).getInboxItemsByCategory(workflowtype,
                        ((currentPage - 1) * pagePerItems), pagePerItems, priority, accessToken);

                Callback<TaskAPIResponse> inboxListCallback = new Callback<TaskAPIResponse>() {

                    @Override
                    public void onResponse(Call<TaskAPIResponse> getInboxList, Response<TaskAPIResponse> response) {

                        if(currentPage ==1)
                        {
                            ((Homepage)getActivity()).homePageLoader.setVisibility(View.GONE);
                        }

                        if (taskListAdapater == null) {
                            tasks = response.body().getResult();
                            taskListAdapater = new TasksListAdapater(tasks, TasksFragment.this);
                            recyclerviewTasks.setAdapter(taskListAdapater);
                        } else {
                            tasks.remove(tasks.size() - 1);
                            taskListAdapater.notifyItemRemoved(tasks.size());
                            tasks.addAll(response.body().getResult());
                            taskListAdapater.notifyItemInserted(tasks.size());
                        }

                        currentPage++;

                    }

                    @Override
                    public void onFailure(Call<TaskAPIResponse> getInboxList, Throwable t) {

                    }
                };
                getInboxList.enqueue(inboxListCallback);
            }


        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity!=null);
        listener=(TasksItemClickListener)activity;
    }

    @Override
    public void onTaskItemClickedIdx(Task selectedTasks) {
        //Toast.makeText(getActivity(), "Task sender is ->"+selectedTasks.getSender()  , Toast.LENGTH_LONG).show();
        listener.onTaskItemClicked(selectedTasks);
    }

}
