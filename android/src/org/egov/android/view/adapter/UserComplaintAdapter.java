package org.egov.android.view.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.egov.android.R;
import org.egov.android.listener.IActionListener;
import org.egov.android.model.Complaint;
import org.ocpsoft.prettytime.PrettyTime;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserComplaintAdapter extends BaseAdapter implements OnClickListener {

    private Activity activity;
    private ArrayList<Complaint> listItem;
    private ViewHolder holder = null;
    private boolean type = false;
    private IActionListener iListener = null;

    public UserComplaintAdapter(Activity activity, ArrayList<Complaint> listItem, boolean type,
            IActionListener iListener) {
        this.activity = activity;
        this.listItem = listItem;
        this.type = type;
        this.iListener = iListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.complaint_list_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.status = (TextView) convertView.findViewById(R.id.complaint_status);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.loadMore = (TextView) convertView.findViewById(R.id.load_more);
            holder.loadMore.setOnClickListener(this);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Complaint data = listItem.get(position);
        if (!type) {
            holder.name.setTypeface(holder.name.getTypeface(), Typeface.BOLD);
        } else {
            holder.name.setVisibility(View.GONE);
        }
        holder.image.setImageResource(R.drawable.complaint);
        holder.description.setText(data.getDetails());
        holder.date.setText(getActualTime(data.getCreatedDate()));
        holder.status.setText(data.getStatus());

        if (getCount() > 10 && (getCount() % 10 == 1) && (position == getCount() - 1)) {
            holder.loadMore.setVisibility(View.VISIBLE);
        } else {
            holder.loadMore.setVisibility(View.GONE);
        }
        return convertView;
    }

    private String formatdate(String datetime) {
        String s = datetime;
        String[] parts = s.split("\\.");
        String part1 = parts[0];
        String s1 = part1.replace('T', '\t');
        String[] parts1 = s1.split("\\t");
        String date = parts1[0];
        String time = parts1[1];
        String newdate = date + " " + time;
        return newdate;
    }

    private String getActualTime(String createdAt) {
        Date date = new Date();
        String dateStart = formatdate(createdAt);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current_date = format.format(date);

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(current_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        long min = (diffHours * 60) + diffMinutes + (diffSeconds / 60);
        PrettyTime p = new PrettyTime();
        return p.format(new Date(System.currentTimeMillis() - 1000 * 60 * min));
    }

    static class ViewHolder {
        TextView name;
        TextView title;
        TextView description;
        TextView date;
        TextView status;
        TextView loadMore;
        ImageView image;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void onClick(View v) {
        iListener.actionPerformed("CALL_API", 0);
    }
}
