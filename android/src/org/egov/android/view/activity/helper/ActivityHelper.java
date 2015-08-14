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

package org.egov.android.view.activity.helper;

import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.AndroidLibrary;
import org.egov.android.api.ApiResponse;
import org.egov.android.api.IApiListener;
import org.egov.android.listener.Event;
import org.egov.android.view.component.slider.ISlidingDrawerListener;
import org.egov.android.view.component.slider.SlidingDrawerLayout;
import org.egov.android.view.activity.FreqComplaintTypeActivity;
import org.egov.android.view.activity.LoginActivity;
import org.egov.android.view.activity.ProfileActivity;
import org.egov.android.view.activity.SearchActivity;
import org.egov.android.view.component.Header;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityHelper implements OnClickListener, ISlidingDrawerListener {

    private Activity activity = null;

    private SlidingDrawerLayout slidingDrawer = null;

    public ActivityHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * To set the layout for the ActivityHelper.
     * 
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        this.activity.overridePendingTransition(R.anim.slide_in_from_right,
                R.anim.slide_out_to_left);
        ApiController.getInstance().setContext(this.activity);
    }

    public void onResume() {
        ApiController.getInstance().setContext(this.activity);
    }

    public void onStart() {
        ApiController.getInstance().setContext(this.activity);
    }

    /**
     * Set click listener to the views in header
     * 
     * @param layoutResID
     */
    public void setContentView(int layoutResID) {
        Header header = (Header) this.activity.findViewById(R.id.header);
        if (header != null) {
            header.setActionListener((OnClickListener) this.activity);
        }
    }

    /**
     * Event triggered when click on the item having click listener. When click on more icon show
     * the right slider. When click on back icon call super.onBackPressed(). Click on edit icon
     * redirect to the CreateComplaintActivity. Click on search icon redirect to the SearchActivity.
     * Click on profile in slider redirect to ProfileActivity. Click on logout in slider call
     * _logoutPopup() function.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case Header.ACTION_SETTING:
                slidingDrawer = (SlidingDrawerLayout) this.activity
                        .findViewById(R.id.slidingDrawer);
                if (slidingDrawer != null) {
                    slidingDrawer.setListener(this);
                    slidingDrawer.open();
                }
                break;
            case Header.ACTION_BACK:
                this.activity.onBackPressed();
                break;
            case Header.ACTION_ADD_COMPLAINT:
                this.activity.startActivity(new Intent(this.activity,
                        FreqComplaintTypeActivity.class));
                break;
            case Header.ACTION_SEARCH:
                this.activity.startActivity(new Intent(this.activity, SearchActivity.class));
                break;
            case R.id.setting_profile:
                this.activity.startActivity(new Intent(this.activity, ProfileActivity.class));
                break;
            case R.id.setting_logout:
                _logoutPopup();
                break;
        }
    }

    @SuppressWarnings("static-access")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ApiController.getInstance().setContext(this.activity);
        if (resultCode != this.activity.RESULT_OK) {
            return;
        }
    }

    public void onStop() {
        if (slidingDrawer != null) {
            slidingDrawer.close(false);
        }
    }

    public void finish() {
        this.activity.overridePendingTransition(R.anim.slide_in_from_left,
                R.anim.slide_out_to_right);
    }

    @Override
    public void onOpen() {
        String className = this.activity.getClass().getSimpleName();
        ((TextView) this.activity.findViewById(R.id.user_name)).setText(AndroidLibrary
                .getInstance().getSession().getString("user_name", ""));
        if (className.equalsIgnoreCase("ProfileActivity")
                || className.equalsIgnoreCase("EditProfileActivity")) {
            ((TextView) this.activity.findViewById(R.id.setting_profile)).setTextColor(Color.GRAY);
        } else {
            ((TextView) this.activity.findViewById(R.id.setting_profile)).setTextColor(Color.WHITE);
            ((TextView) this.activity.findViewById(R.id.setting_profile)).setOnClickListener(this);
        }
        ((TextView) this.activity.findViewById(R.id.setting_logout)).setOnClickListener(this);
    }

    @Override
    public void onClose() {
    }

    public boolean onBackPressed() {
        if (slidingDrawer != null && slidingDrawer.isOpen()) {
            slidingDrawer.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Function called when click on logout in right slider. Show popup for confirmation. If click
     * on 'yes' then call logout api and on response empty the access_token and move to the
     * LoginActivity. If click on 'no' then dismiss the popup.
     */
    private void _logoutPopup() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.activity);
        dialog.setMessage(R.string.logout_message);
        dialog.setCancelable(true);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                ApiController.getInstance().logout(new IApiListener() {
                    @Override
                    public void onResponse(Event<ApiResponse> event) {
                        String status = event.getData().getApiStatus().getStatus();
                        String msg = event.getData().getApiStatus().getMessage();
                        if (status.equalsIgnoreCase("success")) {
                            _startLoginActivity();
                        } else {
                            if (msg.matches(".*Invalid access token.*")) {
                                showMsg("Session expired");
                                _startLoginActivity();
                            } else {
                                showMsg(msg);
                            }
                        }
                    }
                });
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = dialog.create();
        alert.show();
    }

    /**
     * Function used to finish all previous activities from the history and start the LoginActivity
     */
    private void _startLoginActivity() {
        AndroidLibrary.getInstance().getSession().edit().putString("access_token", "").commit();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * Function used to show message in toast
     * 
     * @param message
     */
    private void showMsg(String message) {
        if (message != null && !message.equals("")) {
            Toast toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
        }
    }
}