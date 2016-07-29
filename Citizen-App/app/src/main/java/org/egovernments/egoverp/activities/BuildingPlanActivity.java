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
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.CustomEditText;
import org.egovernments.egoverp.helper.KeyboardUtils;
import org.egovernments.egoverp.models.BuildingPlanAPIResponse;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.ApiUrl;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class BuildingPlanActivity extends BaseActivity {

    CustomEditText searchEditText;
    ApiController.APIInterface buildingPlanApi;
    ProgressBar progressBar;
    boolean isKeyboardVisible=false;
    CardView cvApplicationDetails;

    TextView tvApplicationNo, tvApplicationStatus, tvOwnerName, tvOwnerMobNo, tvOwnerEmailId, tvOwnerAddress, tvSiteAddress,
            tvNatureOfSite, tvPermissionType, tvApplicantName, tvApplicantMobNo, tvApplicantEmailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_plan);

        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        tvApplicationNo=(TextView)findViewById(R.id.tvApplicationNo);
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
                    findBuildingPlanApplication(searchEditText.getText().toString().trim());
                    hideSoftKeyboard(imm);
                }
                return true;
            }
        });
        searchEditText.setDrawableClickListener(new CustomEditText.DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {
                    findBuildingPlanApplication(searchEditText.getText().toString().trim());
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

    public void findBuildingPlanApplication(String applicationNo)
    {

        if(TextUtils.isEmpty(applicationNo.trim()))
        {
            Toast.makeText(getApplicationContext(),"Please enter application no", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        cvApplicationDetails.setVisibility(View.GONE);

        applicationNo=applicationNo.replaceAll("/","$");

        buildingPlanApi.getBuildingPlanApprovalDetails(applicationNo, ApiUrl.BPA_AUTH_KEY, new Callback<BuildingPlanAPIResponse>() {
            @Override
            public void success(BuildingPlanAPIResponse buildingPlanAPIResponse, Response response) {

                progressBar.setVisibility(View.GONE);
                if(TextUtils.isEmpty(buildingPlanAPIResponse.getSuccess()))
                {
                    Toast.makeText(getApplicationContext(), buildingPlanAPIResponse.getError(), Toast.LENGTH_SHORT).show();
                }
                else{
                    BuildingPlanAPIResponse.Response bpadetails=buildingPlanAPIResponse.getResponse().get(0);
                    tvApplicationNo.setText(searchEditText.getText());
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
                }

            }

            @Override
            public void failure(RetrofitError error) {

                progressBar.setVisibility(View.GONE);

            }
        });

    }

}
