/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egovernments.egoverp.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.activities.BaseActivity;
import org.egovernments.egoverp.helper.GrievanceItemInterface;
import org.egovernments.egoverp.models.Grievance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Complaint list adapter
 */
public class GrievanceListAdapater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private GrievanceItemInterface grievanceItemInterface;
    private List<Grievance> grievances;

    public GrievanceListAdapater(List<Grievance> tasks, GrievanceItemInterface grievanceItemInterface)
    {
        this.grievances =tasks;
        this.grievanceItemInterface =grievanceItemInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType==VIEW_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grievance, viewGroup, false);
            vh = new GrievanceViewHolder(v);
        }
        else
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_common_progress_item, viewGroup, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        return grievances.get(position)!=null? VIEW_ITEM: VIEW_PROG;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof GrievanceViewHolder)
        {
            final Grievance ci = grievances.get(position);

            final GrievanceViewHolder viewHolder=(GrievanceViewHolder)holder;

            if (!Locale.getDefault().getDisplayLanguage().equals(BaseActivity.LOCALE_ENGLISH) &&
                    !TextUtils.isEmpty(ci.getComplaintTypeLocalName()))
                viewHolder.complaintType.setText(ci.getComplaintTypeLocalName());
            else
                viewHolder.complaintType.setText(ci.getComplaintTypeName());


            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS", Locale.ENGLISH).parse(ci.getCreatedDate()));

                viewHolder.complaintDate.setText(dateTimeInfo(ci.getCreatedDate(), "yyyy-MM-dd hh:mm:ss.SSS"));



            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Location name is null if lat/lng is provided
            if (ci.getLocationName() != null)
                viewHolder.complaintLocation.setText(ci.getChildLocationName() + " - " + ci.getLocationName());

            viewHolder.complaintCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    grievanceItemInterface.clickedItem(ci);
                }
            });
            viewHolder.complaintNo.setText(ci.getCrn());
        }
        else
        {
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return grievances.size();
    }

    private String dateTimeInfo(String dateStr, String dateFormat) throws ParseException {

        String formatIfToday="hh:mm a";
        String formatIfCurrentYear="MMM dd";
        String formatIfNotCurrentYear="dd/MM/yyyy";

        SimpleDateFormat df = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        Date infoDate=df.parse(dateStr);
        Date nowDate=new Date();

        String resultFormat=formatIfNotCurrentYear;

        df = new SimpleDateFormat(formatIfCurrentYear, Locale.ENGLISH);

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

        df = new SimpleDateFormat(resultFormat, Locale.ENGLISH);
        return df.format(infoDate);

    }

    private static class GrievanceViewHolder extends RecyclerView.ViewHolder {

        private TextView complaintType;
        private TextView complaintDate;
        private TextView complaintLocation;
        private TextView complaintNo;
        private CardView complaintCardView;

        GrievanceViewHolder(View v) {
            super(v);
            complaintCardView = (CardView) v.findViewById(R.id.complaint_card);
            complaintType = (TextView) v.findViewById(R.id.complaint_type);
            complaintDate = (TextView) v.findViewById(R.id.complaint_date);
            complaintLocation = (TextView) v.findViewById(R.id.complaint_location);
            complaintNo = (TextView) v.findViewById(R.id.complaint_no);
        }

    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }

    }

}
