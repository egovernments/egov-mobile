package com.egov.android.view.activity.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.egov.android.R;
import com.egov.android.controller.ApiController;
import com.egov.android.library.view.component.slider.ISlidingDrawerListener;
import com.egov.android.library.view.component.slider.SlidingDrawerLayout;
import com.egov.android.view.activity.ComplaintActivity;
import com.egov.android.view.activity.LoginActivity;
import com.egov.android.view.activity.ProfileActivity;
import com.egov.android.view.component.Header;

public class ActivityHelper implements OnClickListener, ISlidingDrawerListener {

    private Activity activity = null;

    private SlidingDrawerLayout slidingDrawer = null;

    public ActivityHelper(Activity activity) {
        this.activity = activity;
    }

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

    public void setContentView(int layoutResID) {
        Header header = (Header) this.activity.findViewById(R.id.header);
        if (header != null) {
            header.setActionListener((OnClickListener) this.activity);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case Header.ACTION_SETTING:
                /**
                 * Check to move inistaniation at tob (onCreate)
                 */
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
                this.activity.startActivity(new Intent(this.activity, ComplaintActivity.class));
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
        /**
         * To avoid memory leak in ApiClientBase.java since using dialog window.
         */
        ApiController.getInstance().setContext(this.activity);
        if (resultCode != this.activity.RESULT_OK) {
            return;
        }
    }

    public void onStop() {
        if (slidingDrawer != null) {
            /**
             * Close SlidingDrawer without animation
             */
            slidingDrawer.close(false);
        }
    }

    public void finish() {
        this.activity.overridePendingTransition(R.anim.slide_in_from_left,
                R.anim.slide_out_to_right);
    }

    @Override
    public void onOpen() {
        ((TextView) this.activity.findViewById(R.id.setting_profile)).setOnClickListener(this);
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

    private void _logoutPopup() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.activity);
        dialog.setMessage(R.string.logout_message);
        dialog.setCancelable(true);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.startActivity(new Intent(activity, LoginActivity.class));
                dialog.cancel();
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

}