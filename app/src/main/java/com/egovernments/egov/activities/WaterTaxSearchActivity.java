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
import com.egovernments.egov.models.WaterTaxCallback;
import com.egovernments.egov.models.WaterTaxRequest;
import com.egovernments.egov.network.ApiController;
import com.egovernments.egov.network.ApiUrl;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WaterTaxSearchActivity extends BaseActivity {

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
        setContentView(R.layout.activity_watertax_search);

        progressBar = (ProgressBar) findViewById(R.id.watertax_progressbar);

        LinearLayout headerView = (LinearLayout) getLayoutInflater().inflate(R.layout.header_watertax_list, null);

        listView = (ListView) findViewById(R.id.watertax_taxdetails);
        listView.addHeaderView(headerView);

        assessmentNo = (TextView) headerView.findViewById(R.id.watertax_assessmentno);
        address = (TextView) headerView.findViewById(R.id.watertax_address);
        locality = (TextView) headerView.findViewById(R.id.watertax_locality);
        ownerName = (TextView) headerView.findViewById(R.id.watertax_name);
        ownerPhone = (TextView) headerView.findViewById(R.id.watertax_contact);

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
            Toast toast = Toast.makeText(WaterTaxSearchActivity.this, "Consumer code must be at least 10 characters", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        ApiController.getAPI(WaterTaxSearchActivity.this)
                .getWaterTax(ApiUrl.REFERRER_URL,
                        new WaterTaxRequest("0001", code),
                        new Callback<WaterTaxCallback>() {
                            @Override
                            public void success(WaterTaxCallback taxCallback, Response response) {

                                if (taxCallback.getTaxErrorDetails().getErrorMessage().equals("SUCCESS")) {

                                    assessmentNo.setText(taxCallback.getConsumerNo());
                                    address.setText(taxCallback.getPropertyAddress());
                                    locality.setText(taxCallback.getLocalityName());
                                    ownerName.setText(taxCallback.getOwnerName());
                                    ownerPhone.setText(taxCallback.getMobileNo());

                                    listView.setAdapter(new TaxAdapter(taxCallback.getTaxDetails(), WaterTaxSearchActivity.this));

                                } else {
                                    Toast toast = Toast.makeText(WaterTaxSearchActivity.this, taxCallback.getTaxErrorDetails().getErrorMessage(), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                                progressBar.setVisibility(View.GONE);


                            }

                            @Override
                            public void failure(RetrofitError error) {

                                Toast toast;
                                if (error.getLocalizedMessage() != null)
                                    toast = Toast.makeText(WaterTaxSearchActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT);
                                else
                                    toast = Toast.makeText(WaterTaxSearchActivity.this, "An unexpected error occurred", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                progressBar.setVisibility(View.GONE);


                            }
                        });
    }

}

