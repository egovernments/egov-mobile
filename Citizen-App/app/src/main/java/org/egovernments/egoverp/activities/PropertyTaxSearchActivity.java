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
import android.widget.Toast;

import org.egovernments.egoverp.R;

public class PropertyTaxSearchActivity extends BaseActivity {

    EditText etAssessmentNo;
    EditText etOwnerName;
    EditText etMobileNo;
    FloatingActionButton fabSearchProperty;

    public static String paramUlbCode="ulbCode";
    public static String paramAssessmentNo="assessmentNo";
    public static String paramOwnerName="ownerName";
    public static String paramMobileNo="mobileNo";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_tax_search);

        etAssessmentNo=(EditText)findViewById(R.id.etAssessmentNo);
        etOwnerName=(EditText)findViewById(R.id.etOwnerName);
        etMobileNo=(EditText)findViewById(R.id.etMobileNo);

        fabSearchProperty=(FloatingActionButton)findViewById(R.id.fabSearchProperty);


        fabSearchProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateInputSearchFields())
                {
                    Intent openSearchResult=new Intent(PropertyTaxSearchActivity.this, SearchResultActivity.class);
                    openSearchResult.putExtra(paramUlbCode,sessionManager.getUrlLocationCode());
                    openSearchResult.putExtra(paramAssessmentNo,etAssessmentNo.getText().toString());
                    openSearchResult.putExtra(paramOwnerName,etOwnerName.getText().toString());
                    openSearchResult.putExtra(paramMobileNo,etMobileNo.getText().toString());
                    startActivity(openSearchResult);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "At least, Please fill any one field to search", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    boolean validateInputSearchFields()
    {
        return (isNotEmpty(etAssessmentNo.getText().toString()) || isNotEmpty(etOwnerName.getText().toString()) || isNotEmpty(etMobileNo.getText().toString()));
    }

    boolean isNotEmpty(String string)
    {
        return !TextUtils.isEmpty(string);
    }


}
