package com.egov.android.view.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egov.android.R;
import com.egov.android.model.Complaint;
import com.egov.android.library.view.adapter.BaseListAdapter;

public class SearchListAdapter<E extends Complaint> extends BaseListAdapter<E> {

    private ViewHolder holder = null;

    public SearchListAdapter(Context context, List<E> listItem) {
        super(context, listItem);
    }

    public SearchListAdapter(Context context, List<E> listItem, boolean useFilter) {
        super(context, listItem, useFilter);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_simple_list_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.vsli_text);
            holder.image = (ImageView) convertView.findViewById(R.id.vsli_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Complaint data = listItem.get(position);
        holder.name.setText(data.getName());
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        ImageView image;
    }
}
