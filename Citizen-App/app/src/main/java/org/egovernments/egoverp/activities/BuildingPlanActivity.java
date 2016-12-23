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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.FilesDownloadAdapter;
import org.egovernments.egoverp.api.ApiController;
import org.egovernments.egoverp.api.ApiUrl;
import org.egovernments.egoverp.helper.CustomEditText;
import org.egovernments.egoverp.helper.KeyboardUtils;
import org.egovernments.egoverp.models.BuildingPenalizationAPIResponse;
import org.egovernments.egoverp.models.BuildingPlanAPIResponse;
import org.egovernments.egoverp.models.DownloadDoc;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import retrofit2.Call;
import tr.xip.errorview.ErrorView;


public class BuildingPlanActivity extends BaseActivity {

    public static String IS_BUILDING_PENALIZATION = "Is_building_penalization";
    CustomEditText searchEditText;
    Call<BuildingPlanAPIResponse> buildingPlanAPIResponseCall;
    ProgressBar progressBar;
    boolean isKeyboardVisible=false;
    boolean isBuildingPenalization=false;
    CardView cvBuildingPlanDetails;
    CardView cvBPSDetails;
    //Textview for Building plan
    TextView tvApplicationNo, tvApplicationType, tvApplicationStatus, tvOwnerName, tvOwnerMobNo, tvOwnerEmailId, tvOwnerAddress, tvSiteAddress,
            tvNatureOfSite, tvPermissionType, tvApplicantName, tvApplicantMobNo, tvApplicantEmailId;
    //Textview for BPS
    TextView tvBPSApplicationNo, tvBPSApplicationStatus, tvBPSApplicantName, tvBPSApplicantMobNo;
    LinearLayout layoutBPSDocsSection;
    RecyclerView recyclerView, recyclerViewBPS;

    ErrorView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithNavBar(R.layout.activity_building_plan, true);

        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);


        tvApplicationNo=(TextView)findViewById(R.id.tvApplicationNo);
        tvApplicationType=(TextView)findViewById(R.id.tvApplicationType);
        tvApplicationStatus=(TextView)findViewById(R.id.tvApplicationStatus);
        tvOwnerName=(TextView)findViewById(R.id.tvOwnerName);
        tvOwnerMobNo=(TextView)findViewById(R.id.tvOwnerMobNo);
        tvOwnerEmailId=(TextView)findViewById(R.id.tvOwnerEmailId);
        tvOwnerAddress=(TextView)findViewById(R.id.tvOwnerAddress);
        tvSiteAddress=(TextView)findViewById(R.id.tvSiteAddress);
        tvNatureOfSite=(TextView)findViewById(R.id.tvNatureOfSite);
        tvPermissionType=(TextView)findViewById(R.id.tvPermissionType);
        tvApplicantName=(TextView)findViewById(R.id.tvApplicantName);
        tvApplicantMobNo=(TextView)findViewById(R.id.tvApplicantMobNo);
        tvApplicantEmailId=(TextView)findViewById(R.id.tvApplicantEmailId);
        cvBuildingPlanDetails =(CardView)findViewById(R.id.cvBuildingPlanDetails);
        cvBPSDetails=(CardView)findViewById(R.id.cvBPSDetails);

        tvBPSApplicationNo=(TextView)findViewById(R.id.tvBPSApplicationNo);
        tvBPSApplicationStatus=(TextView)findViewById(R.id.tvBPSApplicationStatus);
        tvBPSApplicantName=(TextView)findViewById(R.id.tvBPSApplicantName);
        tvBPSApplicantMobNo=(TextView)findViewById(R.id.tvBPSApplicantMobNo);
        layoutBPSDocsSection=(LinearLayout)findViewById(R.id.layoutBPSDocsSection);

        recyclerView=(RecyclerView)findViewById(R.id.recylerViewFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewBPS=(RecyclerView)findViewById(R.id.recylerviewBPSFiles);
        recyclerViewBPS.setLayoutManager(new LinearLayoutManager(this));

        errorView = (ErrorView) findViewById(R.id.errorView);

        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener()
        {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible)
            {
                isKeyboardVisible=isVisible;
            }
        });

        searchEditText = (CustomEditText) findViewById(R.id.editTextSearch);
        searchEditText.setVisibility(View.VISIBLE);
        searchEditText.setHint(R.string.bpa_search_hint);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    findApplication(searchEditText.getText().toString().trim());
                    hideSoftKeyboard(imm);
                }
                return true;
            }
        });
        searchEditText.setDrawableClickListener(new CustomEditText.DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {
                    findApplication(searchEditText.getText().toString().trim());
                    hideSoftKeyboard(imm);
                }
            }
        });

        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(0);

    }

    private void hideSoftKeyboard(InputMethodManager imm)
    {
        if(imm != null && isKeyboardVisible){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    public void findApplication(String applicationNo)
    {

        if(TextUtils.isEmpty(applicationNo.trim()))
        {
            showSnackBar(getString(R.string.please_enter_application_no));
            return;
        }

        if(isBuildingPenalization)
        {
            findBuildingPenalizationApplication(applicationNo);
        }
        else
        {
            findBuildingPlanApplication(applicationNo);
        }
    }

    public void findBuildingPenalizationApplication(String applicationNo)
    {
        new getBuildingPenalizationDetails().execute(applicationNo);
    }

    public void findBuildingPlanApplication(String applicationNo)
    {

        progressBar.setVisibility(View.VISIBLE);
        cvBuildingPlanDetails.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);

        applicationNo=applicationNo.replaceAll("/","\\$");

        buildingPlanAPIResponseCall = ApiController.getRetrofit2API(getApplicationContext(), ApiUrl.BPA_SERVER_ADDRESS).getBuildingPlanApprovalDetails(applicationNo, ApiUrl.BPA_AUTH_KEY);

        buildingPlanAPIResponseCall.enqueue(new retrofit2.Callback<BuildingPlanAPIResponse>() {
            @Override
            public void onResponse(Call<BuildingPlanAPIResponse> call, retrofit2.Response<BuildingPlanAPIResponse> response) {

                BuildingPlanAPIResponse buildingPlanAPIResponse = response.body();

                progressBar.setVisibility(View.GONE);
                if (TextUtils.isEmpty(buildingPlanAPIResponse.getStatus()) || !buildingPlanAPIResponse.getStatus().equals("success"))
                {
                    showSnackBar(buildingPlanAPIResponse.getError());
                }
                else{
                    BuildingPlanAPIResponse.Response bpadetails=buildingPlanAPIResponse.getResponse().get(0);
                    tvApplicationNo.setText(searchEditText.getText());
                    tvApplicationType.setText(bpadetails.getCaseType());
                    tvApplicationStatus.setText(bpadetails.getStatus());
                    tvOwnerName.setText(bpadetails.getOwnerName());
                    tvOwnerEmailId.setText(bpadetails.getOwnerEmailID());
                    tvOwnerMobNo.setText(bpadetails.getOwnerMobileNo());
                    tvOwnerAddress.setText(bpadetails.getOwnerAddress());
                    tvSiteAddress.setText(bpadetails.getSiteAddress());
                    tvNatureOfSite.setText(bpadetails.getNatureOfSite());
                    tvPermissionType.setText(bpadetails.getPermissionType());
                    tvApplicantName.setText(bpadetails.getLTPName());
                    tvApplicantEmailId.setText(bpadetails.getLTPEmailID());
                    tvApplicantMobNo.setText(bpadetails.getLTPMobileNo());
                    cvBuildingPlanDetails.setVisibility(View.VISIBLE);



                    ArrayList<DownloadDoc> downloadDocs=new ArrayList<>();

                    if(!TextUtils.isEmpty(bpadetails.getDrawingPlain()))
                    {
                        downloadDocs.add(new DownloadDoc(getString(R.string.drawing_plan_document), bpadetails.getDrawingPlain()));
                    }

                    if(!TextUtils.isEmpty(bpadetails.getProceedingLetter()))
                    {
                        downloadDocs.add(new DownloadDoc(getString(R.string.proceeding_letter), bpadetails.getProceedingLetter()));
                    }

                    if(!TextUtils.isEmpty(bpadetails.getScrutinyReport()))
                    {
                        downloadDocs.add(new DownloadDoc(getString(R.string.scruntiny_report), bpadetails.getScrutinyReport()));
                    }

                    recyclerView.setAdapter(new FilesDownloadAdapter(BuildingPlanActivity.this, downloadDocs));

                }

            }

            @Override
            public void onFailure(Call<BuildingPlanAPIResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public String getToolbarTitle(String titleFromResource) {
        isBuildingPenalization =getIntent().getBooleanExtra(IS_BUILDING_PENALIZATION, false);
        if(isBuildingPenalization)
        {
            return getString(R.string.building_penalization_label);
        }
        return getString(R.string.building_plan_label);
    }

    private String getValidUrl(String url) {

        url = url.replace("\\", "/");

        if (url.startsWith("http:")) {
            return url;
        } else {
            url = "http:" + url;
        }

        return url;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buildingPlanAPIResponseCall != null && !buildingPlanAPIResponseCall.isCanceled())
            buildingPlanAPIResponseCall.cancel();
    }

    void showErrorView(int httpCode) {
        ErrorView.Config config = ErrorView.Config.create().retryVisible(false)
                .build();
        errorView.setConfig(config);
        errorView.setError(httpCode);
        errorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void errorOccurred(String errorMsg, int errorCode) {
        showErrorView(errorCode);
    }

    private class getBuildingPenalizationDetails extends AsyncTask<String, Void, BuildingPenalizationAPIResponse> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            cvBPSDetails.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(BuildingPenalizationAPIResponse buildingPenalizationAPIResponse) {

            super.onPostExecute(buildingPenalizationAPIResponse);

            progressBar.setVisibility(View.GONE);

            String genericError="Something went wrong on server!";

            if(buildingPenalizationAPIResponse!=null)
            {

                if(TextUtils.isEmpty(buildingPenalizationAPIResponse.getApplicationNo()))
                {
                    String error=buildingPenalizationAPIResponse.getCurrentStatus();
                    showSnackBar(TextUtils.isEmpty(error) ? genericError : error);
                }
                else{

                    layoutBPSDocsSection=(LinearLayout)findViewById(R.id.layoutBPSDocsSection);

                    tvBPSApplicationNo.setText(buildingPenalizationAPIResponse.getApplicationNo());
                    tvBPSApplicationStatus.setText(buildingPenalizationAPIResponse.getCurrentStatus());
                    tvBPSApplicantName.setText(buildingPenalizationAPIResponse.getApplicantName());
                    tvBPSApplicantMobNo.setText(buildingPenalizationAPIResponse.getMobileNo());

                    if(buildingPenalizationAPIResponse.getDocType().equals("NA"))
                    {
                        layoutBPSDocsSection.setVisibility(View.GONE);
                    }
                    else
                    {
                        layoutBPSDocsSection.setVisibility(View.VISIBLE);

                        ArrayList<DownloadDoc> downloadDocs=new ArrayList<>();

                        if(buildingPenalizationAPIResponse.getStatusID() == 20)
                        {


                            String[] docsPath=buildingPenalizationAPIResponse.getDocPath().split("##");

                            if(docsPath.length>0) {
                                downloadDocs.add(new DownloadDoc(getString(R.string.procedding_document), getValidUrl(docsPath[0])));
                                downloadDocs.add(new DownloadDoc(getString(R.string.plan_document), getValidUrl(docsPath[1])));
                            }

                        }
                        else
                        {
                            downloadDocs.add(new DownloadDoc(buildingPenalizationAPIResponse.getDocType(), getValidUrl(buildingPenalizationAPIResponse.getDocPath())));
                        }

                        recyclerViewBPS.setAdapter(new FilesDownloadAdapter(BuildingPlanActivity.this, downloadDocs));

                    }

                    cvBPSDetails.setVisibility(View.VISIBLE);
                }

            }
            else
            {
                showSnackBar(genericError);
                showErrorView(500);
            }

        }

        @Override
        protected BuildingPenalizationAPIResponse doInBackground(String... params) {

            BuildingPenalizationAPIResponse buildingPenalizationAPIResponse=null;

            String SOAP_ACTION = ApiUrl.BPS_SOAP_ACTION+ApiUrl.BPS_SOAP_METHOD_NAME;
            SoapObject	request = new SoapObject(ApiUrl.BPS_SOAP_ACTION, ApiUrl.BPS_SOAP_METHOD_NAME);
            request.addProperty(ApiUrl.BPS_SOAP_METHOD_PARAM_NAME, params[0]);
            SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE	androidHttpTransport = new HttpTransportSE(ApiUrl.BPS_SOAP_SERVICE_URL, 120000);
            androidHttpTransport.debug = true;

            try {
                androidHttpTransport.call(SOAP_ACTION,envelope);
                SoapPrimitive result =(SoapPrimitive) envelope.getResponse();
                buildingPenalizationAPIResponse=new Gson().fromJson(result.toString().trim(), BuildingPenalizationAPIResponse.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return buildingPenalizationAPIResponse;

        }
    }
}
