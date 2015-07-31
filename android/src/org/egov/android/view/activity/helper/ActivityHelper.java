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

    private void _startLoginActivity() {
        AndroidLibrary.getInstance().getSession().edit().putString("access_token", "").commit();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    private void showMsg(String message) {
        if (message != null && !message.equals("")) {
            Toast toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
        }
    }
}