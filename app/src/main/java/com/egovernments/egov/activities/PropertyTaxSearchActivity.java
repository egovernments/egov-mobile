package com.egovernments.egov.activities;


import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egovernments.egov.R;
import com.egovernments.egov.adapters.TaxAdapter;
import com.egovernments.egov.helper.CustomEditText;
import com.egovernments.egov.models.PropertyTaxCallback;
import com.egovernments.egov.models.PropertyTaxRequest;
import com.egovernments.egov.models.TaxOwnerDetail;
import com.egovernments.egov.network.ApiController;
import com.egovernments.egov.network.ApiUrl;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PropertyTaxSearchActivity extends BaseActivity {

    private TextView assessmentNo;
    private TextView address;
    private TextView locality;
    private TextView ownerName;
    private TextView ownerPhone;

    private ListView listView;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propertytax_search);

        progressBar = (ProgressBar) findViewById(R.id.propertytax_progressbar);

        LinearLayout headerView = (LinearLayout) getLayoutInflater().inflate(R.layout.header_propertytax_list, null);

        listView = (ListView) findViewById(R.id.propertytax_taxdetails);
        listView.addHeaderView(headerView);

        assessmentNo = (TextView) headerView.findViewById(R.id.propertytax_assessmentno);
        address = (TextView) headerView.findViewById(R.id.propertytax_address);
        locality = (TextView) headerView.findViewById(R.id.propertytax_locality);
        ownerName = (TextView) headerView.findViewById(R.id.propertytax_name);
        ownerPhone = (TextView) headerView.findViewById(R.id.propertytax_contact);

        final CustomEditText searchEditText = (CustomEditText) findViewById(R.id.property_searchTextbox);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit(searchEditText.getText().toString().trim());
                }
                return true;
            }
        });
        searchEditText.setDrawableClickListener(new CustomEditText.DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {
                    submit(searchEditText.getText().toString().trim());
                }
            }
        });

        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(0);


    }

    private void submit(final String code) {

        progressBar.setVisibility(View.VISIBLE);

        if (code.length() < 10) {
            Toast toast = Toast.makeText(PropertyTaxSearchActivity.this, "Assessment no. must be at least 10 characters", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        ApiController.getAPI(PropertyTaxSearchActivity.this)
                .getPropertyTax(ApiUrl.REFERRER_URL,
                        new PropertyTaxRequest(/*String.format("%04d", sessionManager.getUrlLocationCode())*/"0001", code),
                        new Callback<PropertyTaxCallback>() {
                            @Override
                            public void success(PropertyTaxCallback propertyTaxCallback, Response response) {

                                if (propertyTaxCallback.getTaxErrorDetails().getErrorMessage().equals("SUCCESS")) {

                                    assessmentNo.setText(propertyTaxCallback.getAssessmentNo());
                                    address.setText(propertyTaxCallback.getPropertyAddress());
                                    locality.setText(propertyTaxCallback.getLocalityName());

                                    String owners = "";
                                    String mobileNos = "";
                                    int check = 0;
                                    for (TaxOwnerDetail taxOwnerDetail : propertyTaxCallback.getTaxOwnerDetails()) {

                                        owners += taxOwnerDetail.getOwnerName();
                                        mobileNos += taxOwnerDetail.getMobileNo();
                                        if (check > 0) {
                                            owners += ", ";
                                            mobileNos += ", ";
                                        }
                                        check++;
                                    }
                                    ownerName.setText(owners);
                                    ownerPhone.setText(mobileNos);

                                    listView.setAdapter(new TaxAdapter(propertyTaxCallback.getTaxDetails(), PropertyTaxSearchActivity.this));

                                } else {
                                    Toast toast = Toast.makeText(PropertyTaxSearchActivity.this, propertyTaxCallback.getTaxErrorDetails().getErrorMessage(), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                                progressBar.setVisibility(View.GONE);


                            }

                            @Override
                            public void failure(RetrofitError error) {

                                Toast toast;
                                if (error.getLocalizedMessage() != null)
                                    toast = Toast.makeText(PropertyTaxSearchActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT);
                                else
                                    toast = Toast.makeText(PropertyTaxSearchActivity.this, "An unexpected error occurred", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                progressBar.setVisibility(View.GONE);


                            }
                        });
    }

}

