/*
 *    eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (c) 2016  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egovernments.egoverp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.CardViewOnClickListener;
import org.egovernments.egoverp.models.Grievance;
import org.egovernments.egoverp.network.SessionManager;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Custom adapter for the grievance activity recycler view
 **/

public class GrievanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Grievance> grievanceList;
    private CardViewOnClickListener.OnItemClickCallback onItemClickCallback;
    private WeakReference<Context> contextWeakReference;

    private SessionManager sessionManager;

    private final int VIEW_ITEM = 1;

    public GrievanceAdapter(Context context, List<Grievance> grievanceList, CardViewOnClickListener.OnItemClickCallback onItemClickCallback) {
        this.grievanceList = grievanceList;
        this.contextWeakReference = new WeakReference<>(context);
        this.onItemClickCallback = onItemClickCallback;

        sessionManager = new SessionManager(context);
    }


    @Override
    public int getItemCount() {
        return grievanceList.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {


        if (viewHolder instanceof GrievanceViewHolder) {
            Grievance ci = grievanceList.get(i);

            ((GrievanceViewHolder) viewHolder).complaintType.setText(ci.getComplaintTypeName());

            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS", Locale.ENGLISH).parse(ci.getCreatedDate()));

                ((GrievanceViewHolder) viewHolder).complaintDate.setText(dateTimeInfo(ci.getCreatedDate(), "yyyy-MM-dd hh:mm:ss.SSS"));



            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Location name is null if lat/lng is provided
            if (ci.getLocationName() != null)
                ((GrievanceViewHolder) viewHolder).complaintLocation.setText(ci.getChildLocationName() + " - " + ci.getLocationName());



            if (ci.getSupportDocsSize() == 0) {
                Picasso.with(contextWeakReference.get())
                        .load(R.drawable.complaint_default)
                        .into(((GrievanceViewHolder) viewHolder).complaintImage);
            } else {
                final String url = sessionManager.getBaseURL()
                        + "/api/v1.0/complaint/downloadfile/"
                        + ci.getSupportDocs().get(0).getFileId()
                        + "?access_token=" + sessionManager.getAccessToken();
                //When loading image, first attempt to retrieve from disk or memory before attempting to download.
                //Still requires internet connection as the plugin attempts to contact the server to see if the image must be revalidated
                Picasso.with(contextWeakReference.get())
                        .load(url)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.broken_icon)
                        .into(((GrievanceViewHolder) viewHolder).complaintImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(contextWeakReference.get())
                                        .load(url)
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.broken_icon)
                                        .into(((GrievanceViewHolder) viewHolder).complaintImage);
                            }
                        });
            }

            ((GrievanceViewHolder) viewHolder).complaintCardView.setOnClickListener(new CardViewOnClickListener(i, onItemClickCallback));
            ((GrievanceViewHolder) viewHolder).complaintNo.setText("Grievance No.: " + ci.getCrn());
            ((GrievanceViewHolder) viewHolder).complaintStatus.setImageDrawable(getStatusIcon(ci.getStatus()));
        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }

    }

    //ViewHolder for the recycler view. Inflates view to be grievance or progress indicator depending on type
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_grievance, viewGroup, false);
            return new GrievanceViewHolder(itemView);
        }

        View v = LayoutInflater.from(contextWeakReference.get())
                .inflate(R.layout.item_progress, viewGroup, false);

        return new ProgressViewHolder(v);
    }

    //Draws and colors icons for different statuses
    private Drawable getStatusIcon(String status) {
        Drawable drawable;

        switch (status) {
            case "REJECTED":
                drawable = ContextCompat.getDrawable(contextWeakReference.get(), R.drawable.ic_cancel_white_24dp);
                drawable.mutate();
                drawable.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                break;
            case "REGISTERED":
                drawable = ContextCompat.getDrawable(contextWeakReference.get(), R.drawable.ic_report_problem_white_24dp);
                drawable.mutate();
                drawable.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.MULTIPLY);
                break;
            case "FORWARDED":
                drawable = ContextCompat.getDrawable(contextWeakReference.get(), R.drawable.ic_report_problem_white_24dp);
                drawable.mutate();
                drawable.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.MULTIPLY);
                break;
            case "REOPENED":
                drawable = ContextCompat.getDrawable(contextWeakReference.get(), R.drawable.ic_report_problem_white_24dp);
                drawable.mutate();
                drawable.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.MULTIPLY);
                break;
            case "PROCESSING":
                drawable = ContextCompat.getDrawable(contextWeakReference.get(), R.drawable.ic_report_problem_white_24dp);
                drawable.mutate();
                drawable.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.MULTIPLY);
                break;
            case "COMPLETED":
                drawable = ContextCompat.getDrawable(contextWeakReference.get(), R.drawable.ic_done_white_24dp);
                drawable.mutate();
                drawable.setColorFilter(Color.parseColor("#4CAF50"), PorterDuff.Mode.MULTIPLY);
                break;
            case "WITHDRAWN":
                drawable = ContextCompat.getDrawable(contextWeakReference.get(), R.drawable.ic_done_white_24dp);
                drawable.mutate();
                drawable.setColorFilter(Color.parseColor("#1B5E20"), PorterDuff.Mode.MULTIPLY);
                break;
            default:
                drawable = ContextCompat.getDrawable(contextWeakReference.get(), R.drawable.ic_cancel_white_24dp);
                break;

        }
        return drawable;
    }


    //Resolves numerical date to the form n days/months/years ago
    private String timeDifference(Calendar calendar) {

        Calendar now = Calendar.getInstance();
        int difference = Math.abs(now.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR));

        if (difference == 0)
            return "Today";

        if (difference <= 30)
            return difference + " days ago";

        if (difference <= 365)
            return difference + "months ago";

        return difference + "years ago";

    }

    //get DateTime Info into Meaningful
    public String dateTimeInfo(String dateStr, String dateFormat) throws ParseException {

        String formatIfToday="hh:mm a";
        String formatIfCurrentYear="MMM dd";
        String formatIfNotCurrentYear="dd/MM/yyyy";

        SimpleDateFormat df=new SimpleDateFormat(dateFormat);
        Date infoDate=df.parse(dateStr);
        Date nowDate=new Date();

        String resultFormat=formatIfNotCurrentYear;

        df=new SimpleDateFormat(formatIfCurrentYear);

        Calendar infocal = Calendar.getInstance();
        infocal.setTime(infoDate);
        Calendar nowcal = Calendar.getInstance();

        if(df.format(infoDate).equals(df.format(nowDate)))
        {
            //info date is today
            resultFormat=formatIfToday;
        }
        else if(String.valueOf(infocal.get(Calendar.YEAR)).equals(String.valueOf(nowcal.get(Calendar.YEAR))))
        {
            //info date in current year
            resultFormat=formatIfCurrentYear;
        }

        df=new SimpleDateFormat(resultFormat);
        return df.format(infoDate);

    }

    //If the item is null, return the progress indicator, i.e., if user has moved past all list items
    @Override
    public int getItemViewType(int position) {
        return grievanceList.get(position) != null ? VIEW_ITEM : 0;
    }

    //View holder for the progress indicator
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }


    //ViewHolder for the grievance items
    public static class GrievanceViewHolder extends RecyclerView.ViewHolder {

        private TextView complaintType;
        private TextView complaintDate;
        private ImageView complaintImage;
        private TextView complaintLocation;
        private TextView complaintNo;
        private ImageView complaintStatus;
        private CardView complaintCardView;

        public GrievanceViewHolder(View v) {
            super(v);
            complaintCardView = (CardView) v.findViewById(R.id.complaint_card);
            complaintType = (TextView) v.findViewById(R.id.complaint_type);
            complaintDate = (TextView) v.findViewById(R.id.complaint_date);
            complaintImage = (ImageView) v.findViewById(R.id.complaint_image);
            complaintLocation = (TextView) v.findViewById(R.id.complaint_location);
            complaintStatus = (ImageView) v.findViewById(R.id.complaint_status);
            complaintNo = (TextView) v.findViewById(R.id.complaint_no);
        }

    }
}
