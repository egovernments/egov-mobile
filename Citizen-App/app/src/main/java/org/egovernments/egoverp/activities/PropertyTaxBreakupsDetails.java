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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.TaxAdapter;
import org.egovernments.egoverp.models.TaxDetail;

import java.util.ArrayList;

public class PropertyTaxBreakupsDetails extends AppCompatActivity {

    ArrayList<TaxDetail> taxBreakups;
    ListView lvBreakups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_tax_breakups_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvBreakups=(ListView)findViewById(R.id.lvbreakups);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String jsonObject=getIntent().getExtras().getString("breakupsList");
        taxBreakups=new Gson().fromJson(jsonObject, new TypeToken<ArrayList<TaxDetail>>(){}.getType());

        lvBreakups.setAdapter(new TaxAdapter(taxBreakups, PropertyTaxBreakupsDetails.this));


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

}
