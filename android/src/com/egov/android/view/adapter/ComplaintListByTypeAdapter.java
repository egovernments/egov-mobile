package com.egov.android.view.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.egov.android.R;
import com.egov.android.model.ComplaintListByType;
import com.egov.android.view.activity.ComplaintDetailActivity;

public class ComplaintListByTypeAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater inflater = null;
    private List<ComplaintListByType> listItem = null;

    public ComplaintListByTypeAdapter(Context context, List<ComplaintListByType> listItem) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listItem = listItem;
        this.context = context;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listItem.get(groupPosition).getComplaintList();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition,
                             int childPosition,
                             boolean isLastChild,
                             View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.complaint_list_group, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.child_name);
        textView.setText(listItem.get(groupPosition).getComplaintList().get(childPosition)
                .getName());
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ComplaintDetailActivity.class));
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listItem.get(groupPosition).getGroupCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listItem.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return listItem.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
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
        if (listItem.get(groupPosition).getImage().equals("sweeper")) {
            image.setBackgroundResource(R.drawable.sweeper);
        }
        if (listItem.get(groupPosition).getImage().equals("water")) {
            image.setBackgroundResource(R.drawable.water);
        }
        if (listItem.get(groupPosition).getImage().equals("drainage")) {
            image.setBackgroundResource(R.drawable.drainage);
        }
        if (listItem.get(groupPosition).getImage().equals("street")) {
            image.setBackgroundResource(R.drawable.street);
        }
        if (listItem.get(groupPosition).getImage().equals("animal")) {
            image.setBackgroundResource(R.drawable.animal);
        }
        text.setText(listItem.get(groupPosition).getName());
        return convertView;
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
