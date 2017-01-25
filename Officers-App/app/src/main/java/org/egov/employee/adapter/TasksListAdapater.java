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

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.egov.employee.data.Task;
import org.egov.employee.interfaces.TasksItemClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import offices.org.egov.egovemployees.R;

/**
 * Created by egov on 15/12/15.
 */
public class TasksListAdapater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String PRIORITY_STR_FORMAT = "Priority is";
    static TasksItemClickListener.TaskItemClickedIndex tasksItemListener;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    List<Task> tasks;

    public TasksListAdapater(List<Task> tasks, TasksItemClickListener.TaskItemClickedIndex tasksItemClickListener)
    {
        this.tasks=tasks;
        this.tasksItemListener=tasksItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh=null;
        if(viewType==VIEW_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_task_list, viewGroup, false);
            vh = new TaskViewHolder(v);
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
        return tasks.get(position)!=null? VIEW_ITEM: VIEW_PROG;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof TaskViewHolder)
        {
            Task currentTask=tasks.get(position);
            TaskViewHolder taskViewHolder=(TaskViewHolder)holder;
            String taskName=currentTask.getTask().toUpperCase();
            String[] taskNameFilter=taskName.split("::");

            if(taskNameFilter.length>1)
            {
                taskName=taskNameFilter[taskNameFilter.length-1];
            }

            String taskDate=currentTask.getRefDate();

            try {
                taskDate=dateTimeInfo(taskDate,"dd/MM/yyyy hh:mm a");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String itemDetails=currentTask.getItemDetails();
            //grievance list item condition for displaying complaint no instead of nature of task
            if(taskName.toUpperCase().equals("GRIEVANCE"))
            {
                int endIdx = itemDetails.indexOf(PRIORITY_STR_FORMAT) > -1 ? itemDetails.indexOf(PRIORITY_STR_FORMAT) : itemDetails.length();

                if (endIdx < itemDetails.length()) {
                    taskViewHolder.tvPriority.setVisibility(View.VISIBLE);
                    String priorityStr = itemDetails.substring(itemDetails.indexOf(PRIORITY_STR_FORMAT), itemDetails.length());
                    priorityStr = priorityStr.replace(PRIORITY_STR_FORMAT, "").trim();
                    taskViewHolder.tvPriority.setText(priorityStr);

                    int priorityColor = R.color.bluecolor;

                    if (priorityStr.toLowerCase().equals("high")) {
                        priorityColor = R.color.redcolor;
                        taskViewHolder.tvPriority.setText("Pri");
                    } else if (priorityStr.toLowerCase().equals("normal"))
                        priorityColor = R.color.bluecolor;
                    else if (priorityStr.toLowerCase().equals("low"))
                        priorityColor = R.color.greencolor;

                    Drawable clone = taskViewHolder.tvPriority.getBackground().getConstantState().newDrawable();
                    DrawableCompat.setTint(clone, ContextCompat.getColor(taskViewHolder.tvPriority.getContext(), priorityColor));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        taskViewHolder.tvPriority.setBackground(clone);
                    else
                        taskViewHolder.tvPriority.setBackgroundDrawable(clone);

                }

                itemDetails = itemDetails.substring(itemDetails.indexOf("for"), endIdx);

            }

            taskViewHolder.tvNatureOfTask.setText(currentTask.getRefNum());
            taskViewHolder.tvSender.setText(currentTask.getCitizenName() + "(" + currentTask.getCitizenPhoneno() + ")");
            taskViewHolder.tvTaskDateTime.setText(taskDate);
            taskViewHolder.tvTaskDetails.setText(itemDetails);
            taskViewHolder.tvTaskStatus.setText(currentTask.getStatus());
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
        return tasks.size();
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

    public List<Task> getTasks() {
        return tasks;
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }

    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvNatureOfTask;
        private TextView tvSender;
        private TextView tvTaskDateTime;
        private TextView tvTaskDetails;
        private TextView tvTaskStatus;
        private TextView tvPriority;
        private CardView cardView;
        private View itemView;

        TaskViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            cardView = (CardView) itemView.findViewById(R.id.taskcvlist);
            cardView.setOnClickListener(this);
            tvNatureOfTask = (TextView) itemView.findViewById(R.id.tvnatureoftsk);
            tvSender = (TextView) itemView.findViewById(R.id.tvsender);
            tvTaskDateTime = (TextView) itemView.findViewById(R.id.tvdate);
            tvTaskDetails = (TextView) itemView.findViewById(R.id.tvdetails);
            tvTaskStatus = (TextView) itemView.findViewById(R.id.tvstatus);
            tvPriority = (TextView) itemView.findViewById(R.id.tvPriority);
        }

        @Override
        public void onClick(View v) {
            tasksItemListener.onTaskItemClickedIdx(tasks.get(getAdapterPosition()));
        }

    }
}
