package com.egovernments.egov.adapters;

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

import com.egovernments.egov.R;
import com.egovernments.egov.helper.CardViewOnClickListener;
import com.egovernments.egov.models.Grievance;
import com.egovernments.egov.network.ApiUrl;
import com.egovernments.egov.network.SessionManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GrievanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Grievance> grievanceList;
    private CardViewOnClickListener.OnItemClickCallback onItemClickCallback;
    private WeakReference<Context> contextWeakReference;

    private SessionManager sessionManager;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

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

                ((GrievanceViewHolder) viewHolder).complaintDate.setText(timeDifference(calendar));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ((GrievanceViewHolder) viewHolder).complaintLocation.setText(ci.getLocationName());

            final String url = ApiUrl.api_baseUrl + "/complaint/" + ci.getCrn() + "/downloadSupportDocument?isThumbnail=true&access_token=" + sessionManager.getAccessToken();

            if (ci.getSupportDocsSize() == 0) {
                ((GrievanceViewHolder) viewHolder).complaintImage.setImageResource(R.drawable.complaint_default);
            } else {
                Picasso.with(contextWeakReference.get())
                        .load(url)
                        .noFade()
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .error(R.drawable.complaint_default_grey)
                        .into(((GrievanceViewHolder) viewHolder).complaintImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(contextWeakReference.get())
                                        .load(url)
                                        .noFade()
                                        .error(R.drawable.complaint_default)
                                        .into(((GrievanceViewHolder) viewHolder).complaintImage);
                            }
                        });
            }

            ((GrievanceViewHolder) viewHolder).complaintcardview.setOnClickListener(new CardViewOnClickListener(i, onItemClickCallback));
            ((GrievanceViewHolder) viewHolder).complaintNo.setText("Grievance No.: " + ci.getCrn());
            ((GrievanceViewHolder) viewHolder).complaintStatus.setImageDrawable(getStatusIcon(ci.getStatus()));
        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }

    }

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

    //TODO find out different complaint stasuseses

    private Drawable getStatusIcon(String status) {
        Drawable drawable;

        switch (status) {
            case "REGISTERED":
                drawable = ContextCompat.getDrawable(contextWeakReference.get(), R.drawable.ic_cancel_white_24dp);
                drawable.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                return drawable;
            case "Under review":
                drawable = ContextCompat.getDrawable(contextWeakReference.get(), R.drawable.ic_report_problem_white_24dp);
                drawable.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.MULTIPLY);
                return drawable;
            case "Solved":
                drawable = ContextCompat.getDrawable(contextWeakReference.get(), R.drawable.ic_done_white_24dp);
                drawable.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                return drawable;
        }
        return ContextCompat.getDrawable(contextWeakReference.get(), R.drawable.ic_cancel_white_24dp);

    }


    private String timeDifference(Calendar calendar) {


        Calendar now = Calendar.getInstance();
        int difference;

        difference = now.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
        if (difference > 1)
            return (difference + " years ago");
        else if (difference == 1)
            return (difference + " year ago");

        difference = now.get(Calendar.MONTH) - calendar.get(Calendar.MONTH);
        if (difference > 1)
            return (difference + " months ago");
        else if (difference == 1)
            return (difference + " month ago");

        difference = now.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH);
        if (difference == 1)
            return (difference + " day ago");

        if (difference == 0)
            return ("Today");

        return difference + " days ago";

    }

    @Override
    public int getItemViewType(int position) {
        return grievanceList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }


    public static class GrievanceViewHolder extends RecyclerView.ViewHolder {

        private TextView complaintType;
        private TextView complaintDate;
        private ImageView complaintImage;
        private TextView complaintLocation;
        private TextView complaintNo;
        private ImageView complaintStatus;
        private CardView complaintcardview;

        public GrievanceViewHolder(View v) {
            super(v);
            complaintcardview = (CardView) v.findViewById(R.id.complaint_card);
            complaintType = (TextView) v.findViewById(R.id.complaint_type);
            complaintDate = (TextView) v.findViewById(R.id.complaint_date);
            complaintImage = (ImageView) v.findViewById(R.id.complaint_image);
            complaintLocation = (TextView) v.findViewById(R.id.complaint_location);
            complaintStatus = (ImageView) v.findViewById(R.id.complaint_status);
            complaintNo = (TextView) v.findViewById(R.id.complaint_no);
        }

    }
}
