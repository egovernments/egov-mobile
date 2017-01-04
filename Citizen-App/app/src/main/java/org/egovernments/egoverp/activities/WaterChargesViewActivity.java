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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.api.ApiController;
import org.egovernments.egoverp.config.Config;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.helper.KeyboardUtils;
import org.egovernments.egoverp.models.PaymentHistoryRequest;
import org.egovernments.egoverp.models.TaxDetail;
import org.egovernments.egoverp.models.WaterTaxCallback;
import org.egovernments.egoverp.models.WaterTaxRequest;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

import static org.egovernments.egoverp.config.Config.REFERER_IP_CONFIG_KEY;

public class WaterChargesViewActivity extends BaseActivity {

    List<TaxDetail> listBreakups;
    Button btnBreakups;
    FloatingActionButton fabPayWaterTax;
    ScrollView scrollViewWaterTax;
    CardView paymentCardView;
    EditText etAmountToPay;
    EditText etMobileNo;
    EditText etMailAddress;
    double arrearsTotal=0, arrearsPenalty=0, currentTotal=0, currentPenalty=0, total =0;
    boolean isKeyboardVisible=false;
    String consumerNo;
    ConfigManager configManager;
    Call<WaterTaxCallback> waterTaxCall;
    Button paymentHistoryViewButton;
    private TextView tvConsumerNo;
    private TextView address;
    private TextView locality;
    private TextView ownerContact;
    private ProgressBar progressBar;
    private CardView waterTaxCardView;
    private TextView tvArrearsTotal, tvArrearsPenalty, tvCurrentTotal, tvCurrentPenalty, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_charges_view);

        consumerNo=getIntent().getStringExtra(SearchResultActivity.CONSUMER_NO);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listBreakups= new ArrayList<>();
        progressBar = (ProgressBar) findViewById(R.id.watertax_progressbar);

        scrollViewWaterTax = (ScrollView) findViewById(R.id.scrollviewwatertax);

        fabPayWaterTax=(FloatingActionButton)findViewById(R.id.fabpay);
        fabPayWaterTax.setVisibility(View.GONE);

        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener()
        {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible)
            {
                isKeyboardVisible=isVisible;
            }
        });

        try
        {
            configManager= AppUtils.getConfigManager(getApplicationContext());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        fabPayWaterTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(etMobileNo.getText()) || TextUtils.isEmpty(etAmountToPay.getText()) || TextUtils.isEmpty(etMailAddress.getText()))
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.please_fill_payment_details), Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!AppUtils.isValidEmail(etMailAddress.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(etMobileNo.getText().toString().length()<10)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.please_enter_valid_mobile_no), Toast.LENGTH_SHORT).show();
                    return;
                }

                int amountToPay= Integer.parseInt(etAmountToPay.getText().toString());
                if(amountToPay<=0)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.payment_amount_greater_than_0), Toast.LENGTH_SHORT).show();
                }
                else if(amountToPay> total)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.payment_amount_should_not_greater) + "(" + Math.round(total) + ")", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String paymentGatewayUrl=configManager.getString(Config.APP_PAYMENT_GATEWAY_WATER_TAX);
                    paymentGatewayUrl=sessionManager.getBaseURL()+paymentGatewayUrl;

                    paymentGatewayUrl=paymentGatewayUrl.replace("{consumerNo}", tvConsumerNo.getText().toString());
                    paymentGatewayUrl = paymentGatewayUrl.replace("{ulbCode}", String.format(Locale.getDefault(), "%04d", sessionManager.getUrlLocationCode()));
                    paymentGatewayUrl=paymentGatewayUrl.replace("{amountToPay}", String.valueOf(amountToPay));
                    paymentGatewayUrl=paymentGatewayUrl.replace("{mobileNo}", etMobileNo.getText().toString());
                    paymentGatewayUrl=paymentGatewayUrl.replace("{emailId}", etMailAddress.getText().toString());

                    Intent intent=new Intent(WaterChargesViewActivity.this, PaymentGatewayActivity.class);
                    intent.putExtra(PaymentGatewayActivity.PAYMENT_GATEWAY_URL, paymentGatewayUrl);
                    startActivityForResult(intent,1);
                }

            }
        });

        tvConsumerNo = (TextView) findViewById(R.id.watertax_assessmentno);
        address = (TextView) findViewById(R.id.watertax_address);
        locality = (TextView) findViewById(R.id.watertax_locality);
        ownerContact=(TextView)findViewById(R.id.watertax_ownernamecontact);
        waterTaxCardView=(CardView)findViewById(R.id.propertywatertax_layout);
        tvArrearsTotal = (TextView) findViewById(R.id.watertax_arrears_total);
        tvArrearsPenalty = (TextView) findViewById(R.id.watertax_arrears_penalty);
        tvCurrentTotal = (TextView) findViewById(R.id.watertax_current_total);
        tvCurrentPenalty = (TextView) findViewById(R.id.watertax_current_penalty);
        tvTotal = (TextView) findViewById(R.id.watertax_total);

        paymentCardView = (CardView)findViewById(R.id.cvPayment);
        paymentCardView.setVisibility(View.GONE);

        etAmountToPay=(EditText) findViewById(R.id.etAmount);
        etMobileNo=(EditText) findViewById(R.id.etMobileNo);
        etMailAddress=(EditText)findViewById(R.id.etMail);

        btnBreakups=(Button)findViewById(R.id.btnbreakups);
        paymentHistoryViewButton = (Button) findViewById(R.id.btnViewPaymentHistory);

        btnBreakups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentViewBreakup = new Intent(WaterChargesViewActivity.this, PropertyTaxBreakupsDetails.class);
                intentViewBreakup.putExtra("breakupsList", new Gson().toJson(listBreakups));
                startActivity(intentViewBreakup);
            }
        });

        paymentHistoryViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPaymentHistory = new Intent(WaterChargesViewActivity.this, PaymentHistoryActivity.class);
                intentPaymentHistory.putExtra(PaymentHistoryActivity.REFERRER_IP, configManager.getString(REFERER_IP_CONFIG_KEY));
                intentPaymentHistory.putExtra(PaymentHistoryActivity.SERVICE_NAME,
                        PaymentHistoryRequest.ServiceName.WATER_TAX.name());
                intentPaymentHistory.putExtra(PaymentHistoryActivity.CONSUMER_CODE, consumerNo);

                startActivity(intentPaymentHistory);
            }
        });


        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(0);


        viewWaterConnection(consumerNo);

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

    private void viewWaterConnection(final String code) {

        fabPayWaterTax.setVisibility(View.GONE);
        paymentCardView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        if (code.length() < 10) {
            Toast toast = Toast.makeText(WaterChargesViewActivity.this, R.string.consumer_code_10_chars, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        waterTaxCardView.setVisibility(View.GONE);


        waterTaxCall = ApiController.getRetrofit2API(getApplicationContext())
                .getWaterTax(configManager.getString(REFERER_IP_CONFIG_KEY),
                        new WaterTaxRequest(String.format(Locale.getDefault(), "%04d", sessionManager.getUrlLocationCode()), code));

        waterTaxCall.enqueue(new retrofit2.Callback<WaterTaxCallback>() {
            @Override
            public void onResponse(Call<WaterTaxCallback> call, retrofit2.Response<WaterTaxCallback> response) {
                showWaterTaxDetails(response);
            }

            @Override
            public void onFailure(Call<WaterTaxCallback> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void showWaterTaxDetails(retrofit2.Response<WaterTaxCallback> response) {
        WaterTaxCallback taxCallback = response.body();

        if (taxCallback.getTaxErrorDetails().getErrorMessage().equals("SUCCESS")) {

            tvConsumerNo.setText(taxCallback.getConsumerNo());
            address.setText(taxCallback.getPropertyAddress());
            locality.setText(taxCallback.getLocalityName());

            ownerContact.setText(taxCallback.getOwnerName() + " / " + taxCallback.getMobileNo());

            if (!TextUtils.isEmpty(taxCallback.getMobileNo())) {
                etMobileNo.setText(taxCallback.getMobileNo());
            }

            arrearsTotal = 0;
            arrearsPenalty = 0;
            currentTotal = 0;
            currentPenalty = 0;
            total = 0;

            for (TaxDetail taxDetail : taxCallback.getTaxDetails()) {
                if (getCurrentFinancialYearInstallments().contains(taxDetail.getInstallment())) {
                    currentTotal = currentTotal + taxDetail.getTaxAmount();
                    currentPenalty = currentPenalty + taxDetail.getPenalty();
                } else {
                    arrearsTotal += taxDetail.getTaxAmount();
                    arrearsPenalty += taxDetail.getPenalty();
                }
            }

            total = arrearsTotal + arrearsPenalty + currentPenalty + currentTotal;

            if (total > 0) {
                fabPayWaterTax.setVisibility(View.VISIBLE);
                paymentCardView.setVisibility(View.VISIBLE);
            } else {
                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (10 * scale + 0.5f);
                scrollViewWaterTax.setPadding(0, 0, 0, dpAsPixels);
            }

            NumberFormat nf1 = NumberFormat.getInstance(new Locale("hi", "IN"));
            nf1.setMinimumFractionDigits(0);
            nf1.setMaximumFractionDigits(0);

            etAmountToPay.setText(String.valueOf(Math.round(total)));
            if (TextUtils.isEmpty(etMobileNo.getText())) {
                etMobileNo.setText(sessionManager.getMobile());
            }

            if (!TextUtils.isEmpty(sessionManager.getEmail())) {
                etMailAddress.setText(sessionManager.getEmail());
            }

            tvArrearsTotal.setText(getString(R.string.rupee_value, nf1.format(arrearsTotal)));
            tvArrearsPenalty.setText(getString(R.string.rupee_value, nf1.format(arrearsPenalty)));
            tvCurrentTotal.setText(getString(R.string.rupee_value, nf1.format(currentTotal)));
            tvCurrentPenalty.setText(getString(R.string.rupee_value, nf1.format(currentPenalty)));
            tvTotal.setText(getString(R.string.rupee_value, nf1.format(Math.round(total))));
            listBreakups = taxCallback.getTaxDetails();
            waterTaxCardView.setVisibility(View.VISIBLE);
            waterTaxCardView.requestFocus();

        } else {
            Toast toast = Toast.makeText(WaterChargesViewActivity.this, taxCallback.getTaxErrorDetails().getErrorMessage(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            listBreakups.clear();
        }
        progressBar.setVisibility(View.GONE);
    }

    public ArrayList<String> getCurrentFinancialYearInstallments()
    {

        ArrayList<String> currentYearInstallments=new ArrayList<>();

        List<Integer> firstInstallment= Arrays.asList(3, 4, 5, 6, 7, 8);
        //List<Integer> secondInstallment=Arrays.asList(9, 10, 11, 0, 1, 2);

        Calendar now = Calendar.getInstance();
        int currentMonth=now.get(Calendar.MONTH);
        int currentYear=now.get(Calendar.YEAR);
        int nextYear=currentYear+1;
        int prevYear=currentYear-1;
        if(firstInstallment.contains(currentMonth))
        {
            currentYearInstallments.add(currentYear+"-"+nextYear+"-1");
            currentYearInstallments.add(currentYear+"-"+nextYear+"-2");
        }
        else
        {
            if(currentMonth<=2)
            {
                currentYearInstallments.add(prevYear+"-"+currentYear+"-1");
                currentYearInstallments.add(prevYear+"-"+currentYear+"-2");
            }
            else
            {
                currentYearInstallments.add(currentYear+"-"+nextYear+"-1");
                currentYearInstallments.add(currentYear+"-"+nextYear+"-2");
            }
        }

        return currentYearInstallments;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewWaterConnection(consumerNo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waterTaxCall != null && !waterTaxCall.isCanceled())
            waterTaxCall.cancel();
    }
}

