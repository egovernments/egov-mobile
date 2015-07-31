package com.egov.android.view.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.egov.android.R;
import com.egov.android.model.Complaint;
import com.egov.android.model.ComplaintType;

public class ComplaintAdapter extends BaseExpandableListAdapter {

    private Activity activity;
    private LayoutInflater inflater;

    private ArrayList<ComplaintType> complaintType;
    private ArrayList<Complaint> complaint;

    public ComplaintAdapter(ArrayList<ComplaintType> complaintType, ArrayList<Complaint> complaint) {
        this.complaint = complaint;
        this.complaintType = complaintType;
    }

    public void setInflater(LayoutInflater inflater, Activity activity) {
        this.inflater = inflater;
        this.activity = activity;
    }

    @Override
    public View getChildView(int groupPosition,
                             int childPosition,
                             boolean isLastChild,
                             View convertView,
                             ViewGroup parent) {

        TextView textView = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.complaint_list_group, null);
        }

        textView = (TextView) convertView.findViewById(R.id.child_name);
        textView.setText(complaint.get(childPosition).getName());

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition,
                             boolean isExpanded,
                             View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.complaint_list_child, null);
        }

        ImageView indicator = (ImageView) convertView.findViewById(R.id.group_indicator);
        ImageView image = (ImageView) convertView.findViewById(R.id.group_image);
        TextView text = (TextView) convertView.findViewById(R.id.group_name);

        if (isExpanded) {
            indicator.setImageResource(R.drawable.arrow_down);
        } else {
            indicator.setImageResource(R.drawable.arrow_left);
        }

        /*if (complaintType.get(groupPosition).getImage().equals("sweeper")) {
            image.setBackgroundResource(R.drawable.sweeper);
        }
        if (complaintType.get(groupPosition).getImage().equals("water")) {
            image.setBackgroundResource(R.drawable.water);
        }
        if (complaintType.get(groupPosition).getImage().equals("drainage")) {
            image.setBackgroundResource(R.drawable.drainage);
        }
        if (complaintType.get(groupPosition).getImage().equals("street")) {
            image.setBackgroundResource(R.drawable.street);
        }
        if (complaintType.get(groupPosition).getImage().equals("animal")) {
            image.setBackgroundResource(R.drawable.animal);
        }*/

        text.setText(complaintType.get(groupPosition).getName());
        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 5;
        //return ((ArrayList<Object>) complaint.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return complaintType.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
