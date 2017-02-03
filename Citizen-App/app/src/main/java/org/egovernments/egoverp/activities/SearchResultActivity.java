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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.SearchListAdapter;
import org.egovernments.egoverp.api.ApiController;
import org.egovernments.egoverp.models.PropertySearchRequest;
import org.egovernments.egoverp.models.PropertyTaxCallback;
import org.egovernments.egoverp.models.SearchResultItem;
import org.egovernments.egoverp.models.TaxDetail;
import org.egovernments.egoverp.models.TaxOwnerDetail;
import org.egovernments.egoverp.models.WaterConnectionSearchRequest;
import org.egovernments.egoverp.models.WaterTaxCallback;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

import static org.egovernments.egoverp.config.Config.REFERER_IP_CONFIG_KEY;

public class SearchResultActivity extends BaseActivity {

    public static String ULB_CODE = "ulbCode";
    public static String ASSESSMENT_NO = "assessmentNo";
    public static String CONSUMER_NO = "consumerNo";
    ProgressBar progressBar;
    RecyclerView recyclerViewSearchResult;
    SearchListAdapter.SearchItemClickListener itemClickListener;
    List<PropertyTaxCallback> resultProperties=new ArrayList<>();
    List<WaterTaxCallback> resultWaterConnections=new ArrayList<>();
    CardView cvInfo;
    TextView tvMsg;
    ImageView imgInfo;
    int ulbCode;
    String referrerIp;
    String category;
    boolean isVacantLand=false;
    boolean isWaterCharges=false;
    PropertySearchRequest propertySearchRequest;
    WaterConnectionSearchRequest waterConnectionSearchRequest;

    Call<List<PropertyTaxCallback>> propertySearchCall;
    Call<List<WaterTaxCallback>> waterConnectionResultsCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_search_result);
        setContentView(R.layout.activity_search_result);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        referrerIp=getIntent().getStringExtra(REFERER_IP_CONFIG_KEY);

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


        isWaterCharges=getIntent().getBooleanExtra(WaterChargesSearchActivity.PARAM_IS_WATER_CON_SEARCH, false);

        itemClickListener = new SearchListAdapter.SearchItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(isWaterCharges) {
                    openViewWaterConnection(resultWaterConnections.get(position).getConsumerNo());
                }
                else {
                    openViewPropertyTaxScreen(resultProperties.get(position).getAssessmentNo());
                }
            }
        };

        if(!isWaterCharges) {
            isVacantLand = getIntent().getBooleanExtra(PropertyTaxSearchActivity.IS_VACANT_LAND, false);
            category = (isVacantLand ? PropertyTaxSearchActivity.VLT_CATEGORY_VALUE : PropertyTaxSearchActivity.PT_CATEGORY_VALUE);
            propertySearchRequest = (PropertySearchRequest) getIntent().getSerializableExtra(PropertyTaxSearchActivity.PARAM_PROPERTY_SEARCH_REQUEST);
            ulbCode = propertySearchRequest.getUlbCode();
            showSearchResults(propertySearchRequest);
        }
        else
        {
            waterConnectionSearchRequest = (WaterConnectionSearchRequest) getIntent().getSerializableExtra(WaterChargesSearchActivity.PARAM_WATER_CON_SEARCH_REQUEST);
            ulbCode = waterConnectionSearchRequest.getUlbCode();
            showSearchResults(waterConnectionSearchRequest);
        }

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

    void showEmptyMessage(int icoResId, String message)
    {
        progressBar.setVisibility(View.GONE);
        recyclerViewSearchResult.setVisibility(View.GONE);
        imgInfo.setImageDrawable(ContextCompat.getDrawable(SearchResultActivity.this, icoResId));
        tvMsg.setText(message);
        cvInfo.setVisibility(View.VISIBLE);
    }

    void showErrorMessage(String errorMsg)
    {
        progressBar.setVisibility(View.GONE);
        recyclerViewSearchResult.setVisibility(View.GONE);
        imgInfo.setImageDrawable(ContextCompat.getDrawable(SearchResultActivity.this, R.drawable.ic_cancel_white_48dp));
        tvMsg.setText(errorMsg);
        cvInfo.setVisibility(View.VISIBLE);
    }

    void openViewPropertyTaxScreen(String assessmentNo)
    {
        if(!TextUtils.isEmpty(assessmentNo)){
            Intent openPropertyTaxIntent=new Intent(SearchResultActivity.this, PropertyTaxViewActivity.class);
            openPropertyTaxIntent.putExtra(ULB_CODE, ulbCode);
            openPropertyTaxIntent.putExtra(ASSESSMENT_NO,  assessmentNo);
            openPropertyTaxIntent.putExtra(REFERER_IP_CONFIG_KEY,  referrerIp);
            openPropertyTaxIntent.putExtra(PropertyTaxSearchActivity.IS_VACANT_LAND,  isVacantLand);
            startActivity(openPropertyTaxIntent);
        }

    }

    void openViewWaterConnection(String consumerNo)
    {
        if(!TextUtils.isEmpty(consumerNo)){
            Intent openWaterChargesScreen=new Intent(SearchResultActivity.this, WaterChargesViewActivity.class);
            openWaterChargesScreen.putExtra(ULB_CODE, ulbCode);
            openWaterChargesScreen.putExtra(CONSUMER_NO,  consumerNo);
            openWaterChargesScreen.putExtra(REFERER_IP_CONFIG_KEY,  referrerIp);
            startActivity(openWaterChargesScreen);
        }

    }

    void loadPropertiesResultIntoRecyclerView(List<PropertyTaxCallback> properties)
    {

        if(properties.size()>=100)
        {
            showModifySearchCriteriaDialog();
        }

        List<SearchResultItem> resultItems=new ArrayList<>();
        for(PropertyTaxCallback propertyTaxCallback:properties)
        {

            String ownerNames = "";
            int check = 0;

            for (TaxOwnerDetail taxOwnerDetail : propertyTaxCallback.getTaxOwnerDetails()) {

                if (check > 0) {
                    ownerNames += ", ";
                }

                ownerNames += taxOwnerDetail.getOwnerName();
                check++;
            }

            double total=0;

            for (TaxDetail taxDetail : propertyTaxCallback.getTaxDetails()) {
                total =taxDetail.getTaxAmount()+taxDetail.getPenalty()+total;
            }

            String totalFormatted=NumberFormat.getInstance(new Locale("hi","IN")).format(total);
            resultItems.add(new SearchResultItem(propertyTaxCallback.getAssessmentNo(), ownerNames, propertyTaxCallback.getPropertyAddress(),totalFormatted));
        }

        SearchListAdapter adapter= new SearchListAdapter(getApplicationContext(), resultItems, itemClickListener);
        recyclerViewSearchResult.setAdapter(adapter);
        hideLoadingIndicator();
    }


    void loadWaterConnectionsResultIntoRecyclerView(List<WaterTaxCallback> waterConnections)
    {

        if(waterConnections.size()>=100)
        {
            showModifySearchCriteriaDialog();
        }

        List<SearchResultItem> resultItems=new ArrayList<>();
        for(WaterTaxCallback waterTaxCallback:waterConnections)
        {
            SearchResultItem searchResultItem=new SearchResultItem();
            searchResultItem.setTitleText(waterTaxCallback.getConsumerNo());
            searchResultItem.setSecondaryText(waterTaxCallback.getOwnerName());
            searchResultItem.setOtherText(waterTaxCallback.getPropertyAddress());

            double total=0;

            for (TaxDetail taxDetail : waterTaxCallback.getTaxDetails()) {
                total=taxDetail.getTaxAmount()+taxDetail.getPenalty()+total;
            }
            String totalFormatted=NumberFormat.getInstance(new Locale("hi","IN")).format(total);
            resultItems.add(new SearchResultItem(waterTaxCallback.getConsumerNo(), waterTaxCallback.getOwnerName(), waterTaxCallback.getPropertyAddress(), totalFormatted));
        }

        SearchListAdapter adapter= new SearchListAdapter(getApplicationContext(), resultItems, itemClickListener);
        recyclerViewSearchResult.setAdapter(adapter);
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

    private void showSearchResults(final PropertySearchRequest propertySearchRequest)
    {
        showLoadingIndicator();

        propertySearchCall = ApiController.getRetrofit2API(getApplicationContext())
                .searchProperty(referrerIp, propertySearchRequest);

        propertySearchCall.enqueue(new retrofit2.Callback<List<PropertyTaxCallback>>() {
            @Override
            public void onResponse(Call<List<PropertyTaxCallback>> call, retrofit2.Response<List<PropertyTaxCallback>> response) {

                List<PropertyTaxCallback> propertyTaxCallbacks = response.body();

                resultProperties = propertyTaxCallbacks;

                if (propertyTaxCallbacks.size() > 0) {
                    if (propertyTaxCallbacks.size() == 1) {
                        PropertyTaxCallback propertyTaxCallback = propertyTaxCallbacks.get(0);

                        if (propertyTaxCallback.getErrorDetail() == null
                                && !TextUtils.isEmpty(propertyTaxCallback.getAssessmentNo()) ||
                                TextUtils.isEmpty(propertyTaxCallback.getErrorDetail().getErrorMessage())
                                        && !TextUtils.isEmpty(propertyTaxCallback.getAssessmentNo()) ||
                                propertyTaxCallback.getErrorDetail().getErrorMessage().equals("SUCCESS")
                                        && !TextUtils.isEmpty(propertyTaxCallback.getAssessmentNo())) {
                            SearchResultActivity.this.finish();
                            openViewPropertyTaxScreen(propertyTaxCallback.getAssessmentNo());
                        }
                        else
                        {
                            showEmptyMessage(R.drawable.ic_business_black_36dp, getString(R.string.no_property_found));
                        }
                    } else {
                        loadPropertiesResultIntoRecyclerView(propertyTaxCallbacks);
                    }
                } else {
                    showEmptyMessage(R.drawable.ic_business_black_36dp, getString(R.string.no_property_found));
                }

            }

            @Override
            public void onFailure(Call<List<PropertyTaxCallback>> call, Throwable t) {
                showErrorMessage(t.getLocalizedMessage());
            }
        });

    }

    private void showSearchResults(final WaterConnectionSearchRequest waterConnectionSearchRequest)
    {

        showLoadingIndicator();

        Call<List<WaterTaxCallback>> waterConnectionResultsCall = ApiController.getRetrofit2API(getApplicationContext())
                .searchWaterConnection(referrerIp, waterConnectionSearchRequest);

        waterConnectionResultsCall.enqueue(new retrofit2.Callback<List<WaterTaxCallback>>() {
            @Override
            public void onResponse(Call<List<WaterTaxCallback>> call, retrofit2.Response<List<WaterTaxCallback>> response) {

                List<WaterTaxCallback> waterTaxCallbacks = response.body();
                resultWaterConnections = waterTaxCallbacks;

                if (resultWaterConnections.size() > 0) {
                    if (resultWaterConnections.size() == 1) {
                        WaterTaxCallback waterTaxCallback = waterTaxCallbacks.get(0);

                        if (waterTaxCallback.getTaxErrorDetails() == null && !TextUtils.isEmpty(waterTaxCallback.getConsumerNo())
                                || TextUtils.isEmpty(waterTaxCallback.getTaxErrorDetails().getErrorMessage())
                                && !TextUtils.isEmpty(waterTaxCallback.getConsumerNo())
                                || waterTaxCallback.getTaxErrorDetails().getErrorMessage().equals("SUCCESS")
                                && !TextUtils.isEmpty(waterTaxCallback.getConsumerNo())) {
                            SearchResultActivity.this.finish();
                            openViewWaterConnection(waterTaxCallback.getConsumerNo());
                        }
                        else
                        {
                            showEmptyMessage(R.drawable.ic_water_tab_black_36dp, getString(R.string.no_water_connection_found));
                        }
                    } else {
                        loadWaterConnectionsResultIntoRecyclerView(waterTaxCallbacks);
                    }
                } else {
                    showEmptyMessage(R.drawable.ic_water_tab_black_36dp, getString(R.string.no_water_connection_found));
                }
            }

            @Override
            public void onFailure(Call<List<WaterTaxCallback>> call, Throwable t) {
                showErrorMessage(t.getLocalizedMessage());
            }
        });

    }


    public void showModifySearchCriteriaDialog()
    {
        new AlertDialog.Builder(SearchResultActivity.this)
                .setMessage(R.string.search_criteria_100_remodify)
                .setPositiveButton(R.string.modify, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SearchResultActivity.this.finish();
                    }
                }).create().show();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (propertySearchCall != null && !propertySearchCall.isCanceled())
            propertySearchCall.cancel();
        else if (waterConnectionResultsCall != null && !waterConnectionResultsCall.isCanceled())
            waterConnectionResultsCall.cancel();
    }
}
