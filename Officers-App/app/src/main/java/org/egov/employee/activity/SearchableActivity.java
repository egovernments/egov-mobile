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

package org.egov.employee.activity;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;

import org.egov.employee.adapter.SearchListAdapater;
import org.egov.employee.api.ApiController;
import org.egov.employee.data.SearchResultItem;
import org.egov.employee.data.Task;
import org.egov.employee.data.TaskAPISearchResponse;
import org.egov.employee.utils.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import offices.org.egov.egovemployees.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchableActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    public static final String XY_LOCATIONS = "xyLocations";
    CoordinatorLayout rootLayout;
    SearchView searchView;
    SearchRunnable searchRunnable;
    Handler appHandler;
    RecyclerView recyclerView;
    ImageView searchLoadingImage;
    ProgressBar pbSearch;
    SearchListAdapater.ItemClickListener itemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appHandler=new Handler();

        rootLayout=(CoordinatorLayout)findViewById(R.id.rootLayout);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerViewResults);

        searchLoadingImage=(ImageView)findViewById(R.id.searchImage);

        pbSearch=(ProgressBar)findViewById(R.id.pbsearch);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(SearchableActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.DKGRAY);
            startAnimation(savedInstanceState);
        }

        itemClickListener=new SearchListAdapater.ItemClickListener() {
            @Override
            public void onClick(Task task) {

                Intent openTaskScreen=new Intent(SearchableActivity.this, ViewTask.class);
                openTaskScreen.putExtra(ViewTask.TASK, task);
                startActivityForResult(openTaskScreen, Homepage.ACTION_UPDATE_REQUIRED);

            }
        };

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startAnimation(Bundle savedInstanceState)
    {
        final int xyLocations[] = getIntent().getIntArrayExtra(XY_LOCATIONS);
            if (savedInstanceState == null) {
                rootLayout.setVisibility(View.INVISIBLE);
                final ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {

                    final ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            circularRevealActivity(xyLocations[0], xyLocations[1]);
                            rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    };
                    viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener);
                }
            }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void circularRevealActivity(int cx, int cy) {

        if(cx==0 && cy==0) {
            //center of the layout finding
            cx = rootLayout.getWidth() / 2;
            cy = rootLayout.getHeight() / 2;
        }

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(700);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searchactivity, menu);
        MenuItem searchItem=menu.findItem(R.id.action_search);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });

        searchItem.expandActionView();
        searchView=(SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    protected int getLayoutResource() {
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        return R.layout.activity_searchable;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        recyclerView.setVisibility(View.GONE);
        searchLoadingImage.setVisibility(View.GONE);
        pbSearch.setVisibility(View.VISIBLE);
        searchInboxItems(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        recyclerView.setVisibility(View.GONE);
        searchLoadingImage.setVisibility(View.VISIBLE);
        pbSearch.setVisibility(View.GONE);
        return true;
    }

    private void searchInboxItems(String searchText)
    {

        if(searchRunnable!=null)
        {
            appHandler.removeCallbacks(searchRunnable);
        }

        JsonObject searchJson=new JsonObject();
        searchJson.addProperty("searchText", searchText);
        searchRunnable = new SearchRunnable(searchJson);
        searchRunnable.run();

    }

    class SearchRunnable implements Runnable {

        JsonObject searchJsonObject;
        SearchListAdapater searchListAdapater;
        boolean hasNextPage=false;
        List<SearchResultItem> searchResultItemList;

        EndlessRecyclerOnScrollListener scrollListener=null;

        int pageNo=1;
        int resultLimit=10;

        public SearchRunnable(JsonObject searchJsonObject)
        {
            this.searchJsonObject=searchJsonObject;
            hasNextPage=false;
            searchListAdapater=null;
            searchResultItemList=new ArrayList<>();
            pageNo=1;
            scrollListener=new EndlessRecyclerOnScrollListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
                @Override
                public void onLoadMore(int current_page) {
                    loadMoreSearchResults();
                }
            };
            recyclerView.setOnScrollListener(scrollListener);
        }

        @Override
        public void run() {
            loadSearchResults();
        }

        public void loadMoreSearchResults()
        {
            if(hasNextPage){
                pageNo=pageNo+1;
                loadSearchResults();
            }
        }

        public void loadSearchResults()
        {
            if(searchListAdapater!=null) {

                recyclerView.setOnScrollListener(null);

                if(searchResultItemList.get(searchResultItemList.size()-1) == null)
                {
                    searchResultItemList.remove(searchResultItemList.size() - 1);
                    searchListAdapater.notifyItemRemoved(searchResultItemList.size());
                }

                searchResultItemList.add(null);
                searchListAdapater.notifyItemInserted(searchResultItemList.size());
            }


            Call<TaskAPISearchResponse> searchInboxItemsCall = ApiController.getAPI(getApplicationContext()).searchInboxItems("application/json", searchJsonObject, pageNo, resultLimit, preference.getApiAccessToken());
            Callback<TaskAPISearchResponse> searchInboxItemsCallBack = new Callback<TaskAPISearchResponse>() {

                @Override
                public void onResponse(Call<TaskAPISearchResponse> call, Response<TaskAPISearchResponse> response) {
                    hasNextPage=response.body().getResult().isHasNextPage();


                    if (searchListAdapater == null) {
                        searchResultItemList=response.body().getResult().getSearchItems();
                        searchListAdapater = new SearchListAdapater(getApplicationContext(),searchResultItemList, itemClickListener);
                        recyclerView.setAdapter(searchListAdapater);

                        recyclerView.setVisibility(View.VISIBLE);
                        searchLoadingImage.setVisibility(View.GONE);
                        pbSearch.setVisibility(View.GONE);


                    } else {
                        searchResultItemList.remove(searchResultItemList.size() - 1);
                        searchListAdapater.notifyItemRemoved(searchResultItemList.size());
                        searchResultItemList.addAll(response.body().getResult().getSearchItems());
                        searchListAdapater.notifyItemInserted(searchResultItemList.size());
                    }
                }

                @Override
                public void onFailure(Call<TaskAPISearchResponse> call, Throwable t) {

                }
            };
            searchInboxItemsCall.enqueue(searchInboxItemsCallBack);
        }

    }



}
