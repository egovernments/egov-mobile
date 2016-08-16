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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.models.PropertySearchRequest;

public class PropertyTaxSearchActivity extends BaseActivity {

    EditText etAssessmentNo;
    EditText etOwnerName;
    EditText etMobileNo;
    EditText etDoorNo;

    LinearLayout layoutDoorNoContainer;

    TextView tvSearchTitle;
    FloatingActionButton fabSearchProperty;

    public static final String PARAM_PROPERTY_SEARCH_REQUEST="propertySearchObj";

    public static String IS_VACANT_LAND="isVacantLand";

    public static String PT_CATEGORY_VALUE="PT";
    public static String VLT_CATEGORY_VALUE="VLT";

    boolean isVacantLand=false;

    ConfigManager configManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_tax_search);

        etAssessmentNo=(EditText)findViewById(R.id.etAssessmentNo);
        etOwnerName=(EditText)findViewById(R.id.etOwnerName);
        etMobileNo=(EditText)findViewById(R.id.etMobileNo);
        etDoorNo=(EditText)findViewById(R.id.etDoorNo);

        layoutDoorNoContainer=(LinearLayout)findViewById(R.id.doorNoContainer);

        try
        {
            configManager= AppUtils.getConfigManager(getApplicationContext());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        tvSearchTitle=(TextView)findViewById(R.id.tvSearchTitle);

        if(isVacantLand)
        {
            tvSearchTitle.setText(R.string.search_vacant_land);
        }
        else
        {
            layoutDoorNoContainer.setVisibility(View.VISIBLE);
        }

        fabSearchProperty=(FloatingActionButton)findViewById(R.id.fabSearchProperty);


        fabSearchProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateInputSearchFields())
                {
                    Intent openSearchResult=new Intent(PropertyTaxSearchActivity.this, SearchResultActivity.class);

                    openSearchResult.putExtra(PARAM_PROPERTY_SEARCH_REQUEST, new PropertySearchRequest(sessionManager.getUrlLocationCode(),
                            etAssessmentNo.getText().toString(), etOwnerName.getText().toString(), etMobileNo.getText().toString(),
                            etDoorNo.getText().toString(),(isVacantLand?VLT_CATEGORY_VALUE:PT_CATEGORY_VALUE)));
                    openSearchResult.putExtra(IS_VACANT_LAND, isVacantLand);
                    openSearchResult.putExtra(SearchResultActivity.REFERER_IP_CONFIG_KEY, configManager.getString(SearchResultActivity.REFERER_IP_CONFIG_KEY));
                    startActivity(openSearchResult);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "At least, Please fill any one field to search", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public String getToolbarTitle(String titleFromResource) {
        isVacantLand=getIntent().getBooleanExtra(IS_VACANT_LAND, false);
        if(isVacantLand)
        {
            return getString(R.string.vacantlandtax_label);
        }
        return getString(R.string.propertytax_label);
    }

    boolean validateInputSearchFields()
    {
        return (isNotEmpty(etAssessmentNo.getText().toString().trim()) || isNotEmpty(etOwnerName.getText().toString().trim()) || isNotEmpty(etMobileNo.getText().toString().trim()) || (isNotEmpty(etDoorNo.getText().toString().trim())&&!isVacantLand));
    }

    boolean isNotEmpty(String string)
    {
        return !TextUtils.isEmpty(string);
    }
}
