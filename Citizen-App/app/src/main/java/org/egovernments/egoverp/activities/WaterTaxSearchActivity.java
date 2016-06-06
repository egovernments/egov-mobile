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
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.CustomEditText;
import org.egovernments.egoverp.models.TaxDetail;
import org.egovernments.egoverp.models.WaterTaxCallback;
import org.egovernments.egoverp.models.WaterTaxRequest;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.ApiUrl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WaterTaxSearchActivity extends BaseActivity {

    private TextView assessmentNo;
    private TextView address;
    private TextView locality;
    private TextView ownerContact;
    private ProgressBar progressBar;
    private CardView waterTaxCardView;
    private TextView tvArrearsTotal, tvArrearsPenalty, tvCurrentTotal, tvCurrentPenalty, tvTotal;
    List<TaxDetail> listBreakups;
    Button btnBreakups;
    FloatingActionButton fabPayWaterTax;
    ScrollView scrollViewWaterTax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watertax_search);

        listBreakups= new ArrayList<>();
        progressBar = (ProgressBar) findViewById(R.id.watertax_progressbar);

        scrollViewWaterTax = (ScrollView) findViewById(R.id.scrollviewwatertax);

        fabPayWaterTax=(FloatingActionButton)findViewById(R.id.fabpaywatertax);
        fabPayWaterTax.setVisibility(View.GONE);

        fabPayWaterTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        assessmentNo = (TextView) findViewById(R.id.watertax_assessmentno);
        address = (TextView) findViewById(R.id.watertax_address);
        locality = (TextView) findViewById(R.id.watertax_locality);
        ownerContact=(TextView)findViewById(R.id.watertax_ownernamecontact);
        waterTaxCardView=(CardView)findViewById(R.id.propertywatertax_layout);
        tvArrearsTotal = (TextView) findViewById(R.id.watertax_arrears_total);
        tvArrearsPenalty = (TextView) findViewById(R.id.watertax_arrears_penalty);
        tvCurrentTotal = (TextView) findViewById(R.id.watertax_current_total);
        tvCurrentPenalty = (TextView) findViewById(R.id.watertax_current_penalty);
        tvTotal = (TextView) findViewById(R.id.watertax_total);


        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        final CustomEditText searchEditText = (CustomEditText) findViewById(R.id.editTextSearch);
        searchEditText.setVisibility(View.VISIBLE);
        searchEditText.setHint(R.string.watertax_search_hint);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit(searchEditText.getText().toString().trim());
                    hideSoftKeyboard(imm);
                }
                return true;
            }
        });
        searchEditText.setDrawableClickListener(new CustomEditText.DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {
                    submit(searchEditText.getText().toString().trim());
                    hideSoftKeyboard(imm);
                }
            }
        });

        btnBreakups=(Button)findViewById(R.id.btnbreakups);

        btnBreakups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentViewBreakup = new Intent(WaterTaxSearchActivity.this, PropertyTaxBreakupsDetails.class);
                intentViewBreakup.putExtra("breakupsList", new Gson().toJson(listBreakups));
                startActivity(intentViewBreakup);
            }
        });

        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(0);


    }


    private void hideSoftKeyboard(InputMethodManager imm)
    {
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    private void submit(final String code) {

        fabPayWaterTax.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        if (code.length() < 10) {
            Toast toast = Toast.makeText(WaterTaxSearchActivity.this, "Consumer code must be at least 10 characters", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        waterTaxCardView.setVisibility(View.GONE);

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
                                    ownerContact.setText(taxCallback.getOwnerName()+" / "+taxCallback.getMobileNo());

                                    String currentInstallmentText=getCurrentInstallmentText();

                                    double arrearsTotal=0, arrearsPenalty=0, currentTotal=0, currentPenalty=0, Total=0;

                                    for (TaxDetail taxDetail : taxCallback.getTaxDetails()) {
                                        if(currentInstallmentText.equals(taxDetail.getInstallment()))
                                        {
                                            currentTotal=taxDetail.getTaxAmount();
                                            currentPenalty=taxDetail.getPenalty();
                                        }
                                        else
                                        {
                                            arrearsTotal+=taxDetail.getTaxAmount();
                                            arrearsPenalty+=taxDetail.getPenalty();
                                        }
                                        Total=arrearsTotal+arrearsPenalty+currentPenalty+currentTotal;
                                    }

                                    if(Total>0)
                                    {
                                        fabPayWaterTax.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        float scale = getResources().getDisplayMetrics().density;
                                        int dpAsPixels = (int) (10*scale + 0.5f);
                                        scrollViewWaterTax.setPadding(0,0,0,dpAsPixels);
                                    }

                                    NumberFormat nf1 = NumberFormat.getInstance(new Locale("hi","IN"));
                                    nf1.setMinimumFractionDigits(2);
                                    nf1.setMaximumFractionDigits(2);

                                    tvArrearsTotal.setText(nf1.format(arrearsTotal));
                                    tvArrearsPenalty.setText(nf1.format(arrearsPenalty));
                                    tvCurrentTotal.setText(nf1.format(currentTotal));
                                    tvCurrentPenalty.setText(nf1.format(currentPenalty));
                                    tvTotal.setText(nf1.format(Total));
                                    listBreakups=taxCallback.getTaxDetails();
                                    waterTaxCardView.setVisibility(View.VISIBLE);

                                } else {
                                    Toast toast = Toast.makeText(WaterTaxSearchActivity.this, taxCallback.getTaxErrorDetails().getErrorMessage(), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    listBreakups.clear();
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

    public String getCurrentInstallmentText()
    {

        String installmentText;

        List<Integer> firstInstallment= Arrays.asList(3, 4, 5, 6, 7, 8);
        //List<Integer> secondInstallment=Arrays.asList(9, 10, 11, 0, 1, 2);

        Calendar now = Calendar.getInstance();
        int currentMonth=now.get(Calendar.MONTH);
        int currentYear=now.get(Calendar.YEAR);
        int nextYear=currentYear+1;
        int prevYear=currentYear-1;
        if(firstInstallment.contains(currentMonth))
        {
            installmentText=currentYear+"-"+nextYear+"-1";
        }
        else
        {
            if(currentMonth<=2)
            {
                installmentText=prevYear+"-"+currentYear+"-2";
            }
            else
            {
                installmentText=currentYear+"-"+nextYear+"-2";
            }
        }

        return installmentText;

    }

}

