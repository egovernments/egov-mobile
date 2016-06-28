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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.SearchListAdapater;
import org.egovernments.egoverp.models.PropertySearchRequest;
import org.egovernments.egoverp.models.PropertyTaxCallback;
import org.egovernments.egoverp.models.SearchResultItem;
import org.egovernments.egoverp.models.TaxOwnerDetail;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.ApiUrl;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchResultActivity extends AppCompatActivity {

    ProgressBar progressBar;
    RecyclerView recyclerViewSearchResult;
    SearchListAdapater.SearchItemClickListener itemClickListener;
    List<PropertyTaxCallback> resultProperties=new ArrayList<>();

    CardView cvInfo;
    TextView tvMsg;
    ImageView imgInfo;

    int ulbCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar=(ProgressBar)findViewById(R.id.pbPropSearch);

        cvInfo=(CardView)findViewById(R.id.cvinfo);
        tvMsg=(TextView) findViewById(R.id.tvMsg);
        imgInfo=(ImageView)findViewById(R.id.imgInfo);

        recyclerViewSearchResult=(RecyclerView)findViewById(R.id.RVPropSearch);
        recyclerViewSearchResult.setHasFixedSize(true);
        recyclerViewSearchResult.setClickable(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchResultActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewSearchResult.setLayoutManager(linearLayoutManager);


        itemClickListener=new SearchListAdapater.SearchItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openViewPropertyTaxScreen(resultProperties.get(position).getAssessmentNo());
            }
        };

        ulbCode=getIntegerParam(PropertyTaxSearchActivity.paramUlbCode);
        showSearchResults(ulbCode, getStringParam(PropertyTaxSearchActivity.paramAssessmentNo), getStringParam(PropertyTaxSearchActivity.paramOwnerName), getStringParam(PropertyTaxSearchActivity.paramMobileNo));

    }

    String getStringParam(String paramName)
    {
        return getEmptyStringIfNull(getIntent().getStringExtra(paramName));
    }

    Integer getIntegerParam(String paramName)
    {
        return getIntent().getIntExtra(paramName, 0);
    }

    void hideLoadingIndicator()
    {
        cvInfo.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        recyclerViewSearchResult.setVisibility(View.VISIBLE);
    }

    void showLoadingIndicator()
    {
        cvInfo.setVisibility(View.GONE);
        recyclerViewSearchResult.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    void showEmptyMessage()
    {
        progressBar.setVisibility(View.GONE);
        recyclerViewSearchResult.setVisibility(View.GONE);
        imgInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_black_48dp));
        tvMsg.setText("No property found");
        cvInfo.setVisibility(View.VISIBLE);
    }

    void showErrorMessage(String errorMsg)
    {
        progressBar.setVisibility(View.GONE);
        recyclerViewSearchResult.setVisibility(View.GONE);
        imgInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel_white_48dp));
        tvMsg.setText(errorMsg);
        cvInfo.setVisibility(View.VISIBLE);
    }

    void openViewPropertyTaxScreen(String assessmentNo)
    {
        Intent openPropertyTaxIntent=new Intent(SearchResultActivity.this, PropertyTaxViewActivity.class);
        openPropertyTaxIntent.putExtra("ulbCode", ulbCode);
        openPropertyTaxIntent.putExtra("assessmentNo",  assessmentNo);
        startActivity(openPropertyTaxIntent);
    }

    void loadRecylerView(List<PropertyTaxCallback> properties)
    {

        List<SearchResultItem> resultItems=new ArrayList<>();
        for(PropertyTaxCallback propertyTaxCallback:properties)
        {
            SearchResultItem searchResultItem=new SearchResultItem();
            searchResultItem.setTitleText(propertyTaxCallback.getAssessmentNo());

            String ownerNames = "";
            int check = 0;

            for (TaxOwnerDetail taxOwnerDetail : propertyTaxCallback.getTaxOwnerDetails()) {

                if (check > 0) {
                    ownerNames += ", ";
                }

                ownerNames += taxOwnerDetail.getOwnerName();
                check++;
            }

            searchResultItem.setSecondaryText(ownerNames);
            searchResultItem.setOtherText(propertyTaxCallback.getPropertyAddress());
            resultItems.add(searchResultItem);
        }

        SearchListAdapater adapater= new SearchListAdapater(getApplicationContext(), resultItems, itemClickListener);
        recyclerViewSearchResult.setAdapter(adapater);
        hideLoadingIndicator();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSearchResults(int ulbCode, String assessmentNo, String ownerName, String mobileNo)
    {

        showLoadingIndicator();

        ApiController.getAPI(SearchResultActivity.this)
                .searchProperty(ApiUrl.REFERRER_URL, new PropertySearchRequest(ulbCode, assessmentNo, ownerName, mobileNo),
                        new Callback<List<PropertyTaxCallback>>() {
                @Override
                public void success(List<PropertyTaxCallback> propertyTaxCallbacks, Response response) {

                    resultProperties=propertyTaxCallbacks;

                    if(propertyTaxCallbacks.size()>0)
                    {
                        if(propertyTaxCallbacks.size()==1)
                        {
                            PropertyTaxCallback propertyTaxCallback=propertyTaxCallbacks.get(0);

                            if (propertyTaxCallback.getTaxErrorDetails()==null) {
                                SearchResultActivity.this.finish();
                                openViewPropertyTaxScreen(propertyTaxCallback.getAssessmentNo());
                            }
                            else if(propertyTaxCallback.getTaxErrorDetails().getErrorMessage().equals("SUCCESS"))
                            {
                                SearchResultActivity.this.finish();
                                openViewPropertyTaxScreen(propertyTaxCallback.getAssessmentNo());
                            }
                            else if(!propertyTaxCallback.getTaxErrorDetails().getErrorMessage().equals("SUCCESS"))
                            {
                                showEmptyMessage();
                            }
                        }
                        else
                        {
                            loadRecylerView(propertyTaxCallbacks);
                        }
                    }
                    else
                    {
                        showEmptyMessage();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    showErrorMessage(error.getLocalizedMessage());
                }
       });

    }


    String getEmptyStringIfNull(String string)
    {
        return (TextUtils.isEmpty(string)?"":string);
    }


}
