package org.egov.android.view.activity;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.api.ApiResponse;
import org.egov.android.listener.Event;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StatusSummaryActivity extends BaseActivity {

    private TextView closed;
    private TextView heading;
    private TextView register;
    private TextView forwarded;
    private String complaintDate = "";
    private String complaintStatus = "";
    private String lastModifiedDate = "";
    private String complaintTypeName = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_summary);

        heading = (TextView) findViewById(R.id.status_heading);
        heading.setTypeface(heading.getTypeface(), Typeface.BOLD);

        register = (TextView) findViewById(R.id.register_info);
        forwarded = (TextView) findViewById(R.id.forworded_info);
        closed = (TextView) findViewById(R.id.closed_info);

        String id = getIntent().getExtras().getString("complaintId");
        complaintTypeName = getIntent().getExtras().getString("complaintTypeName");
        complaintStatus = getIntent().getExtras().getString("status");
        complaintDate = getIntent().getExtras().getString("created_date");
        lastModifiedDate = getIntent().getExtras().getString("lastModifiedDate");

        heading.setText(complaintTypeName);

        ApiController.getInstance().getComplaintStatus(this, id);
    }

    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        if (status.equalsIgnoreCase("success")) {
            try {
                JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                if (ja.length() > 0) {
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        try {
                            if (jo.getString("value").equalsIgnoreCase("registered")) {
                                register.setText(getDateTextFormat(jo.getString("createdDate"))
                                        + " Complaint registered");
                                _drawgraph(jo.getString("value"));
                            }

                            if (jo.getString("value").equalsIgnoreCase("forwarded")) {
                                forwarded.setText(getDateTextFormat(jo
                                        .getString("lastModifiedDate")) + " Complaint forwarded");
                                _drawgraph(jo.getString("value"));
                            } else {
                                forwarded.setVisibility(View.GONE);
                            }

                            if (complaintStatus.equalsIgnoreCase("forwarded")) {
                                forwarded.setText(getFormatDate(lastModifiedDate)
                                        + " Complaint forwarded");
                                _drawgraph(complaintStatus);
                            } else if (complaintStatus.equalsIgnoreCase("withdrawn")) {
                                closed.setText(getFormatDate(lastModifiedDate)
                                        + " Complaint closed");
                                _drawgraph(complaintStatus);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        register.setText(getFormatDate(complaintDate) + " Complaint registered");
                        _drawgraph(complaintStatus);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (msg.matches(".*Invalid access token.*")) {
                showMessage("Session expired");
                startLoginActivity();
            } else {
                showMessage(msg);
            }
        }
    }

    private void _drawgraph(String status) {
        View registered = findViewById(R.id.register);
        View forwarded_bg = findViewById(R.id.forwarded_bg);
        View forwarded = findViewById(R.id.forwarded);
        View closed_bg = findViewById(R.id.close_bg);
        View closed = findViewById(R.id.closed);

        if (status.equalsIgnoreCase("registered")) {
            registered.setBackgroundResource(R.drawable.status_summary_active_circle);
        } else if (status.equalsIgnoreCase("forwarded")) {
            registered.setBackgroundResource(R.drawable.status_summary_active_circle);
            forwarded_bg.setBackgroundColor(getResources().getColor(R.color.orange));
            forwarded.setBackgroundResource(R.drawable.status_summary_active_circle);
        } else if (status.equalsIgnoreCase("withdrawn")) {
            registered.setBackgroundResource(R.drawable.status_summary_active_circle);
            forwarded_bg.setBackgroundColor(getResources().getColor(R.color.orange));
            forwarded.setBackgroundResource(R.drawable.status_summary_active_circle);
            closed_bg.setBackgroundColor(getResources().getColor(R.color.orange));
            closed.setBackgroundResource(R.drawable.status_summary_active_circle);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static String getDateTextFormat(String datetime) throws ParseException {
        String newdate = datetime;
        SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date newdate1 = dateformat2.parse(newdate);
        Format formatter1 = new SimpleDateFormat("EEEE dd MMMM yyyy hh:mm a");
        String date1 = formatter1.format(newdate1);
        return date1;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFormatDate(String datetime) throws ParseException {
        String s = datetime;
        String[] parts = s.split("\\."); // escape .
        String part1 = parts[0];
        String s1 = part1.replace('T', '\t');
        String[] parts1 = s1.split("\\t");
        String date = parts1[0];
        String time = parts1[1];
        String newdate = date + " " + time;
        SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date newdate1 = dateformat2.parse(newdate);
        Format formatter1 = new SimpleDateFormat("EEEE dd MMMM yyyy hh:mm a");
        String date1 = formatter1.format(newdate1);
        return date1;
    }
}
