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

package org.egov.employee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.egov.employee.data.SearchResultItem;
import org.egov.employee.data.Task;
import org.egov.employee.utils.AppUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import offices.org.egov.egovemployees.R;

/**
 * Created by egov on 15/12/15.
 */
public class SearchListAdapater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    List<SearchResultItem> resultItems;
    Context context;
    ItemClickListener itemClickListener;


    public SearchListAdapater(Context context, List<SearchResultItem> resultItems, ItemClickListener itemClickListener)
    {
        this.context=context;
        this.resultItems=resultItems;
        this.itemClickListener=itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh=null;
        if(viewType==VIEW_ITEM) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_result, viewGroup, false);
            vh = new SearchResultItemViewHolder(v);
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
        return resultItems.get(position)!=null? VIEW_ITEM: VIEW_PROG;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof SearchResultItemViewHolder)
        {

            final Task task=new Task();
            SearchResultItem searchResultItem=resultItems.get(position);

            task.setRefNum(searchResultItem.getResource().getClauses().getCrn());
            task.setCitizenName(searchResultItem.getResource().getCommon().getCitizen().getName());
            task.setCitizenPhoneno(searchResultItem.getResource().getCommon().getCitizen().getMobile());
            task.setStatus(searchResultItem.getResource().getClauses().getStatus().getName());

            //hardcoded value need to change future
            task.setTask("Grievance");

            SearchResultItemViewHolder searchResultItemViewHolder=(SearchResultItemViewHolder)holder;
            searchResultItemViewHolder.tvRefNum.setText(searchResultItem.getResource().getClauses().getCrn());
            searchResultItemViewHolder.tvItemType.setText(searchResultItem.getType().toUpperCase());
            try {
                searchResultItemViewHolder.tvDate.setText(dateTimeInfo(searchResultItem.getResource().getCommon().getCreatedDate(),
                        "yyyy-MM-dd'T'HH:mm'Z'"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            searchResultItemViewHolder.tvCitizen.setText(searchResultItem.getResource().getCommon().getCitizen().getName()
            +" ("+searchResultItem.getResource().getCommon().getCitizen().getMobile()+")");
            searchResultItemViewHolder.tvDesc.setText(searchResultItem.getResource().getSearchable().getDetails());
            searchResultItemViewHolder.tvSubType.setText(searchResultItem.getResource().getSearchable().getComplaintType().getName());
            searchResultItemViewHolder.tvStatus.setText(searchResultItem.getResource().getClauses().getStatus().getName());

            searchResultItemViewHolder.resultItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   itemClickListener.onClick(task);
                }
            });

            /*if(position<10)
            runEnterAnimation(((SearchResultItemViewHolder) holder).itemView);*/

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
        return resultItems.size();
    }

    public class SearchResultItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout resultItemView;
        public TextView tvRefNum;
        public TextView tvItemType;
        public TextView tvDate;
        public TextView tvCitizen;
        public TextView tvDesc;
        public TextView tvSubType;
        public TextView tvStatus;
        public View itemView;

        SearchResultItemViewHolder(View itemView)
        {
            super(itemView);
            this.itemView=itemView;
            resultItemView=(LinearLayout)itemView.findViewById(R.id.resultItemView);
            tvRefNum=(TextView)itemView.findViewById(R.id.tvRefNum);
            tvItemType=(TextView)itemView.findViewById(R.id.tvItemType);
            tvDate=(TextView)itemView.findViewById(R.id.tvDate);
            tvCitizen=(TextView)itemView.findViewById(R.id.tvCitizen);
            tvDesc=(TextView)itemView.findViewById(R.id.tvDesc);
            tvSubType=(TextView)itemView.findViewById(R.id.tvSubType);
            tvStatus=(TextView)itemView.findViewById(R.id.tvStatus);
        }

        @Override
        public void onClick(View v) {
            /*tasksItemListener.onTaskItemClickedIdx(tasks.get(getAdapterPosition()));*/
        }

    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder{

        ProgressBar progressBar;
        ProgressViewHolder(View itemView)
        {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        }

    }

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


    public interface ItemClickListener{
        void onClick(Task task);
    }

    public List<SearchResultItem> getResultItems() {
        return resultItems;
    }

    private void runEnterAnimation(View view) {


        view.setTranslationY(AppUtils.getScreenHeight(context));

        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(2000)
                .setStartDelay(100)
                .start();
    }

}
