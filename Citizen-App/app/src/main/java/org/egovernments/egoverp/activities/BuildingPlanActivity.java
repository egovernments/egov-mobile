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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.FilesDownloadAdapter;
import org.egovernments.egoverp.helper.CustomEditText;
import org.egovernments.egoverp.helper.KeyboardUtils;
import org.egovernments.egoverp.models.BuildingPlanAPIResponse;
import org.egovernments.egoverp.models.DownloadDoc;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.ApiUrl;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class BuildingPlanActivity extends BaseActivity {

    CustomEditText searchEditText;
    ApiController.APIInterface buildingPlanApi;
    ProgressBar progressBar;
    boolean isKeyboardVisible=false;
    boolean isBuildingPenalization=false;
    CardView cvApplicationDetails;

    TextView tvApplicationNo, tvApplicationType, tvApplicationStatus, tvOwnerName, tvOwnerMobNo, tvOwnerEmailId, tvOwnerAddress, tvSiteAddress,
            tvNatureOfSite, tvPermissionType, tvApplicantName, tvApplicantMobNo, tvApplicantEmailId;

    RecyclerView recyclerView;

    public static String IS_BUILDING_PENALIZATION="Is_building_penalization";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_plan);

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
        cvApplicationDetails=(CardView)findViewById(R.id.cvApplicationDetails);

        recyclerView=(RecyclerView)findViewById(R.id.recylerViewFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        buildingPlanApi=ApiController.getCustomAPI(BuildingPlanActivity.this, ApiUrl.BPA_SERVER_ADDRESS);

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
            Toast.makeText(getApplicationContext(),"Please enter application no", Toast.LENGTH_SHORT).show();
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
        new TestAsynk().execute(applicationNo);
        Toast.makeText(BuildingPlanActivity.this, "Search building peanlization application!", Toast.LENGTH_SHORT).show();
    }

    public void findBuildingPlanApplication(String applicationNo)
    {

        progressBar.setVisibility(View.VISIBLE);
        cvApplicationDetails.setVisibility(View.GONE);

        applicationNo=applicationNo.replaceAll("/","\\$");

        Log.v("Application No", applicationNo);

        buildingPlanApi.getBuildingPlanApprovalDetails(applicationNo, ApiUrl.BPA_AUTH_KEY, new Callback<BuildingPlanAPIResponse>() {
            @Override
            public void success(BuildingPlanAPIResponse buildingPlanAPIResponse, Response response) {

                progressBar.setVisibility(View.GONE);
                if(TextUtils.isEmpty(buildingPlanAPIResponse.getStatus()))
                {
                    Toast.makeText(getApplicationContext(), buildingPlanAPIResponse.getError(), Toast.LENGTH_SHORT).show();
                }
                else if(!buildingPlanAPIResponse.getStatus().equals("success")){
                    Toast.makeText(getApplicationContext(), buildingPlanAPIResponse.getError(), Toast.LENGTH_SHORT).show();
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
                    cvApplicationDetails.setVisibility(View.VISIBLE);



                    ArrayList<DownloadDoc> downloadDocs=new ArrayList<>();

                    if(!TextUtils.isEmpty(bpadetails.getDrawingPlain()))
                    {
                      downloadDocs.add(new DownloadDoc("Drawing-Plan-Document", bpadetails.getDrawingPlain()));
                    }

                    if(!TextUtils.isEmpty(bpadetails.getProceedingLetter()))
                    {
                        downloadDocs.add(new DownloadDoc("Proceeding-Letter", bpadetails.getProceedingLetter()));
                    }

                    if(!TextUtils.isEmpty(bpadetails.getScrutinyReport()))
                    {
                        downloadDocs.add(new DownloadDoc("Scrutiny-Report", bpadetails.getScrutinyReport()));
                    }

                    recyclerView.setAdapter(new FilesDownloadAdapter(BuildingPlanActivity.this, downloadDocs));


                }
            }

            @Override
            public void failure(RetrofitError error) {

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


    private class TestAsynk extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            /*Toast.makeText(getApplicationContext(),
                    String.format("%.2f", Float.parseFloat(result)),
                    Toast.LENGTH_SHORT).show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            SoapObject request = new SoapObject(ApiUrl.BPS_SOAP_NAMESPACE,
                    ApiUrl.BPS_SOAP_METHOD_NAME);
            request.addProperty("ApplicationNo", params[0]);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(
                    ApiUrl.BPS_URL);
            Object response = null;
            try {

                androidHttpTransport.call(ApiUrl.BPS_SOAP_ACTION, envelope);
                response = envelope.getResponse();
                Log.e("Object response", response.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return (response == null?"":response.toString());
        }
    }


}
