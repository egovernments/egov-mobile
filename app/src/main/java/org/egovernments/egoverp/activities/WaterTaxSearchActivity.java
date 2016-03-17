/*
 *    eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (c) 2016  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egovernments.egoverp.activities;


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

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.TaxAdapter;
import org.egovernments.egoverp.helper.CustomEditText;
import org.egovernments.egoverp.models.WaterTaxCallback;
import org.egovernments.egoverp.models.WaterTaxRequest;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.ApiUrl;

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
                        new WaterTaxRequest(String.format("%04d", sessionManager.getUrlLocationCode()), code),
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

