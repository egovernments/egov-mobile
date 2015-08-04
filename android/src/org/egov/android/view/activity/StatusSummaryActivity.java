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

    /**
     * To set the layout for the StatusSummaryActivity and call complaint status api
     */
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

    /**
     * Complaint status api response handler. If the response is success the show the pictorial
     * representation of the complaint status from response and also check the error is invalid
     * access token to redirect to login page.
     */
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
                                register.setText(_getDateTextFormat(jo.getString("createdDate"))
                                        + " Complaint registered");
                                _drawgraph(jo.getString("value"));
                            }

                            if (jo.getString("value").equalsIgnoreCase("forwarded")) {
                                forwarded.setText(_getDateTextFormat(jo
                                        .getString("lastModifiedDate")) + " Complaint forwarded");
                                _drawgraph(jo.getString("value"));
                            } else {
                                forwarded.setVisibility(View.GONE);
                            }

                            if (complaintStatus.equalsIgnoreCase("forwarded")) {
                                forwarded.setText(_getFormatDate(lastModifiedDate)
                                        + " Complaint forwarded");
                                _drawgraph(complaintStatus);
                            } else if (complaintStatus.equalsIgnoreCase("withdrawn")) {
                                closed.setText(_getFormatDate(lastModifiedDate)
                                        + " Complaint closed");
                                _drawgraph(complaintStatus);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        register.setText(_getFormatDate(complaintDate) + " Complaint registered");
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

    /**
     * Function used to show the pictorial representation of the complaint status.
     * 
     * @param status
     *            => registered/forwarded/withdrawn
     */
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

    /**
     * Function used to format the date like Friday 31 July 2015 01:30 PM
     * 
     * @param datetime
     * @return formated date string
     * @throws ParseException
     */
    @SuppressLint("SimpleDateFormat")
    private String _getDateTextFormat(String datetime) throws ParseException {
        String newdate = datetime;
        SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date newdate1 = dateformat2.parse(newdate);
        Format formatter1 = new SimpleDateFormat("EEEE dd MMMM yyyy hh:mm a");
        String date1 = formatter1.format(newdate1);
        return date1;
    }

    /**
     * Function used to format the date(different date format) like Friday 31 July 2015 01:30 PM
     * 
     * @param datetime
     * @return formated date string
     * @throws ParseException
     */
    @SuppressLint("SimpleDateFormat")
    private String _getFormatDate(String datetime) throws ParseException {
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
