package com.egovernments.egov.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.egovernments.egov.R;
import com.egovernments.egov.models.GrievanceComment;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Custom adapter for the grievance details screen comments
 **/

public class GrievanceCommentAdapter extends BaseAdapter {

    private WeakReference<Context> contextWeakReference;
    private List<GrievanceComment> grievanceComments;

    public GrievanceCommentAdapter(List<GrievanceComment> grievanceComments, Context context) {
        this.grievanceComments = grievanceComments;
        this.contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public int getCount() {
        return grievanceComments.size();
    }

    @Override
    public Object getItem(int position) {
        return grievanceComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GrievanceCommentViewHolder grievanceCommentViewHolder = null;
        View view = convertView;
        if (convertView == null) {

            LayoutInflater layoutInflater = LayoutInflater.from(contextWeakReference.get());
            view = layoutInflater.inflate(R.layout.item_grievance_comment, parent, false);


            grievanceCommentViewHolder = new GrievanceCommentViewHolder();
            grievanceCommentViewHolder.commentName = (TextView) view.findViewById(R.id.comment_name);
            grievanceCommentViewHolder.commentText = (TextView) view.findViewById(R.id.comment_text);
            grievanceCommentViewHolder.commentDate = (TextView) view.findViewById(R.id.comment_date);
            grievanceCommentViewHolder.commentStatus = (TextView) view.findViewById(R.id.comment_status);

            view.setTag(grievanceCommentViewHolder);
        }

        if (grievanceCommentViewHolder == null) {
            grievanceCommentViewHolder = (GrievanceCommentViewHolder) view.getTag();
        }
        GrievanceComment grievanceComment = (GrievanceComment) getItem(position);

        grievanceCommentViewHolder.commentName.setText(grievanceComment.getUpdatedBy());
        grievanceCommentViewHolder.commentText.setText(grievanceComment.getComments());
        grievanceCommentViewHolder.commentDate.setText(grievanceComment.getDate());
        grievanceCommentViewHolder.commentStatus.setText(resolveStatus(grievanceComment.getStatus()));
        if (grievanceComment.getUpdatedUserType().equals("EMPLOYEE"))
            grievanceCommentViewHolder.commentName.setTextColor(Color.RED);
        else if (grievanceComment.getUpdatedUserType().equals("CITIZEN"))
            grievanceCommentViewHolder.commentName.setTextColor(Color.BLUE);

        return view;
    }

    private int resolveStatus(String s) {
        if (s.equals("REGISTERED"))
            return R.string.registered_info;
        if (s.equals("PROCESSING"))
            return R.string.processing_label;
        if (s.equals("COMPLETED"))
            return R.string.completed_label;
        if (s.equals("FORWARDED"))
            return R.string.forwarded_label;
        if (s.equals("WITHDRAWN"))
            return R.string.withdrawn_label;
        if (s.equals("REJECTED"))
            return R.string.rejected_label;
        if (s.equals("REOPENED"))
            return R.string.reopend_label;

        return 0;
    }

    public static class GrievanceCommentViewHolder {

        private TextView commentName;
        private TextView commentText;
        private TextView commentDate;
        private TextView commentStatus;
    }
}
