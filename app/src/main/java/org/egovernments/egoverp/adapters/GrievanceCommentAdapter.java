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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.models.GrievanceComment;

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

    private class GrievanceCommentViewHolder {

        private TextView commentName;
        private TextView commentText;
        private TextView commentDate;
        private TextView commentStatus;
    }
}
