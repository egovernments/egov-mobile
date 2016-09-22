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
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.models.WaterConnectionSearchRequest;

public class WaterChargesSearchActivity extends BaseActivity implements View.OnClickListener {

    EditText etAssessmentNo;
    EditText etConsumerNo;
    EditText etOwnerName;
    EditText etMobileNo;
    ConfigManager configManager;

    TextView tvReceiptInfoConsumerNo, tvReceiptInfoAssessmentNo;

    public static final String PARAM_IS_WATER_CON_SEARCH="isWaterConSearch";
    public static final String PARAM_WATER_CON_SEARCH_REQUEST ="WaterConSearchObj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_charges_search);

        etAssessmentNo=(EditText)findViewById(R.id.etAssessmentNo);
        etConsumerNo=(EditText)findViewById(R.id.etConsumerNo);
        etOwnerName=(EditText)findViewById(R.id.etOwnerName);
        etMobileNo=(EditText)findViewById(R.id.etMobileNo);
        tvReceiptInfoConsumerNo =(TextView)findViewById(R.id.tvReceiptInfoConsumerNo);
        tvReceiptInfoAssessmentNo =(TextView)findViewById(R.id.tvReceiptInfoAssessmentNo);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSearchWaterConnection);

        if(fab!=null)
        {
            fab.setOnClickListener(this);
        }

        try
        {
            configManager= AppUtils.getConfigManager(getApplicationContext());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        tvReceiptInfoConsumerNo.setOnClickListener(this);
        tvReceiptInfoAssessmentNo.setOnClickListener(this);
    }

    void searchWaterConnections()
    {
        if(validateInputSearchFields()) {
            Intent openSearchResult=new Intent(WaterChargesSearchActivity.this, SearchResultActivity.class);
            openSearchResult.putExtra(PARAM_WATER_CON_SEARCH_REQUEST, new WaterConnectionSearchRequest(sessionManager.getUrlLocationCode(),
                    etAssessmentNo.getText().toString(),etConsumerNo.getText().toString(), etOwnerName.getText().toString(),etMobileNo.getText().toString()));
            openSearchResult.putExtra(PARAM_IS_WATER_CON_SEARCH, true);
            openSearchResult.putExtra(SearchResultActivity.REFERER_IP_CONFIG_KEY, configManager.getString(SearchResultActivity.REFERER_IP_CONFIG_KEY));
            startActivity(openSearchResult);
        }
        else
        {
            Toast.makeText(getApplicationContext(), R.string.fill_any_one_details, Toast.LENGTH_SHORT).show();
        }
    }

    boolean validateInputSearchFields()
    {
        return (isNotEmpty(etAssessmentNo.getText().toString().trim()) || isNotEmpty(etConsumerNo.getText().toString().trim()) || isNotEmpty(etOwnerName.getText().toString().trim()) || isNotEmpty(etMobileNo.getText().toString().trim()));
    }

    boolean isNotEmpty(String string)
    {
        return !TextUtils.isEmpty(string);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.fabSearchWaterConnection:
                searchWaterConnections();
                break;
            case R.id.tvReceiptInfoConsumerNo:
                AppUtils.showImageDialog(WaterChargesSearchActivity.this, getString(R.string.where_can_i_find_my_consumer_no), getString(R.string.consumerno_taxreceipt), R.drawable.wc_bill, getString(R.string.ok_got_it));
                break;
            case R.id.tvReceiptInfoAssessmentNo:
                AppUtils.showImageDialog(WaterChargesSearchActivity.this, getString(R.string.where_can_i_find_my_assessment_no), getString(R.string.assementno_taxreceipt), R.drawable.pt_bill, getString(R.string.ok_got_it));
                break;
        }

    }
}
