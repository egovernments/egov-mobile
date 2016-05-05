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

package org.egovernments.egoverp.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.activities.GrievanceActivity;
import org.egovernments.egoverp.activities.GrievanceDetailsActivity;
import org.egovernments.egoverp.adapters.GrievanceListAdapater;
import org.egovernments.egoverp.helper.EndlessRecyclerOnScrollListener;
import org.egovernments.egoverp.helper.GrievanceItemInterface;
import org.egovernments.egoverp.models.Grievance;
import org.egovernments.egoverp.models.GrievanceAPIResponse;
import org.egovernments.egoverp.network.ApiController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by egov on 20/4/16.
 */
public class GrievanceFragment extends android.support.v4.app.Fragment {

    GrievanceItemInterface grievanceItemInterface;

    private List<Grievance> grievanceList=new ArrayList<>();

    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    private CardView cvNoComplaints;

    public GrievanceListAdapater grievanceAdapter;

    //The currently visible page no.
    private int pageNo = 1;
    private int position;

    private boolean loading = true;
    private boolean isPaginationEnded = false;
    EndlessRecyclerOnScrollListener onScrollListener;

    String accessToken;
    String pageTitle;

    Bundle downImageArgs;

    public static GrievanceFragment instantiateItem(String access_token, String title, int position, String imageDownloadUrl) {
        GrievanceFragment imageFragment = new GrievanceFragment();
        Bundle args = new Bundle();
        args.putString("access_token", access_token);
        args.putString("title", title);
        args.putInt("position", position);
        args.putString("downImgUrl", imageDownloadUrl);
        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            if(progressBar!=null)
            {
                if(progressBar.getVisibility()==View.VISIBLE)
                refreshGrievanceList();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragement_grievance, container, false);

        cvNoComplaints=(CardView)fragmentView.findViewById(R.id.cvnocomplaintsnotify);
        progressBar = (ProgressBar)fragmentView.findViewById(R.id.grievance_recylerview_placeholder);
        recyclerView = (RecyclerView)fragmentView.findViewById(R.id.recylerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);
        final WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        Bundle bundle = getArguments();
        accessToken=bundle.getString("access_token");
        pageTitle=bundle.getString("title");
        position=bundle.getInt("position");

        downImageArgs=new Bundle();
        downImageArgs.putString("downImgUrl",bundle.getString("downImgUrl"));
        downImageArgs.putString("access_token", accessToken);

        onScrollListener=new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {

                if(!loading && !isPaginationEnded) {
                    pageNo++;
                    loading = true;
                    updateComplaints(String.valueOf(pageNo));
                }
            }
        };

        //Enables infinite scrolling (pagination)
        recyclerView.addOnScrollListener(onScrollListener);

        grievanceItemInterface=new GrievanceItemInterface() {
            @Override
            public void clickedItem(Grievance grievance) {
                Intent intent = new Intent(getActivity(), GrievanceDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GrievanceDetailsActivity.GRIEVANCE_ITEM, grievance);
                bundle.putParcelableArrayList(GrievanceDetailsActivity.GRIEVANCE_SUPPORT_DOCS, grievance.getSupportDocs());
                intent.putExtras(bundle);
                getActivity().startActivityForResult(intent, GrievanceActivity.ACTION_UPDATE_REQUIRED);
            }
        };

        if(position==0)
        {
            refreshGrievanceList();
        }


        return fragmentView;
    }

    public void refreshGrievanceList()
    {
        loading=true;
        isPaginationEnded =false;
        pageNo=1;
        onScrollListener.resetScrollListenerValues();
        updateComplaints("1");
    }

    public String returnValidString(String string)
    {
        if(!TextUtils.isEmpty(string)){
            return string;
        }
        return "";
    }

    private void updateComplaints(final String page) {


            if(grievanceAdapter==null) {
                cvNoComplaints.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
            else if(grievanceList!=null)
            {
                if(grievanceList.size()==0){
                    cvNoComplaints.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            if(grievanceAdapter!=null) {
                grievanceList.add(null);
                grievanceAdapter.notifyItemInserted(grievanceList.size());
            }

            ApiController.getAPI(getActivity()).getMyComplaints(page, "10", accessToken, pageTitle, new Callback<GrievanceAPIResponse>() {
                        @Override
                        public void success(GrievanceAPIResponse grievanceAPIResponse, Response response) {

                            //If the request is a refresh request
                            for(Grievance grievance:grievanceAPIResponse.getResult())
                            {
                                if (TextUtils.isEmpty(grievance.getLocationName()) && grievance.getLat()>0)
                                {
                                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                    List<Address> addresses;
                                    try {
                                        addresses = geocoder.getFromLocation(grievance.getLat(),grievance.getLng(), 1);
                                        String location=(TextUtils.isEmpty(addresses.get(0).getSubLocality())?addresses.get(0).getThoroughfare():addresses.get(0).getSubLocality());
                                        grievance.setLocationName(returnValidString(location));
                                        grievance.setChildLocationName(addresses.get(0).getAddressLine(0));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            if (page.equals("1")) {
                                if(grievanceList!=null) {
                                    grievanceList.clear();
                                }
                                grievanceList = grievanceAPIResponse.getResult();
                                grievanceAdapter=new GrievanceListAdapater(getActivity(), grievanceList, grievanceItemInterface, downImageArgs);
                                recyclerView.setAdapter(grievanceAdapter);
                                progressBar.setVisibility(View.GONE);
                            }
                            //If the request is a next page request
                            else {
                                grievanceList.remove(grievanceList.size() - 1);
                                grievanceAdapter.notifyItemRemoved(grievanceList.size());
                                grievanceList.addAll(grievanceList.size(), grievanceAPIResponse.getResult());
                                grievanceAdapter.notifyItemInserted(grievanceList.size());
                            }

                            if (grievanceAPIResponse.getStatus().getHasNextPage().equals("false")) {
                                isPaginationEnded=true;
                            }
                            loading=false;
                            updateSuccessEvent();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (error != null) {
                                if (error.getLocalizedMessage() != null && !error.getLocalizedMessage().equals("Invalid access token"))
                                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                else {
                                    Toast.makeText(getActivity(), "Session expired! Please, logout and login again.", Toast.LENGTH_LONG).show();
                                }
                            }
                            loading=false;
                            updateFailedEvent();
                        }


                    }

            );
        }

       public void updateSuccessEvent()
       {
           //check and show no complaint information
           if(grievanceList.size()==0)
           {
               cvNoComplaints.setVisibility(View.VISIBLE);
           }
           else
           {
               cvNoComplaints.setVisibility(View.GONE);
           }
       }

       public void updateFailedEvent()
       {
           progressBar.setVisibility(View.GONE);
           grievanceList = new ArrayList<>();
           grievanceAdapter = new GrievanceListAdapater(getActivity(), grievanceList, grievanceItemInterface, downImageArgs);
           recyclerView.setAdapter(grievanceAdapter);
           grievanceList = null;
       }

        public class WrapContentLinearLayoutManager extends LinearLayoutManager {

            public WrapContentLinearLayoutManager(Context context)
            {
                super(context);
            }

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    Log.e("probe", "meet a IOOBE in RecyclerView");
                }
            }
        }


    }