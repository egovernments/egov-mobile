/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.view.activity;

import org.egov.android.view.activity.AndroidFragmentActivity;
import org.egov.android.view.activity.helper.ActivityHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class BaseFragmentActivity extends AndroidFragmentActivity implements OnClickListener {

    private ActivityHelper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new ActivityHelper(this);
        helper.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        helper.onStart();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        helper.setContentView(layoutResID);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        helper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        helper.onClick(v);
    }

    @Override
    public void finish() {
        super.finish();
        helper.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        helper.onStop();
    }

    @Override
    public void onBackPressed() {
        if (!helper.onBackPressed()) {
            super.onBackPressed();
        }
    }
}