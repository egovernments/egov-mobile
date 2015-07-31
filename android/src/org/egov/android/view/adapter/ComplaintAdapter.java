package org.egov.android.view.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.egov.android.R;
import org.egov.android.listener.IActionListener;
import org.egov.android.model.Complaint;
import org.ocpsoft.prettytime.PrettyTime;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ComplaintAdapter extends BaseAdapter implements OnClickListener {

    private Activity activity;
    private ArrayList<Complaint> listItem;
    private ViewHolder holder = null;
    private IActionListener iListener = null;
    private boolean isPagination = false;
    private boolean isUsername = false;
    private int apiLevel = 0;

    public ComplaintAdapter(Activity activity, ArrayList<Complaint> listItem, boolean isPagination,
            boolean isUsername, int apiLevel, IActionListener iListener) {
        this.activity = activity;
        this.listItem = listItem;
        this.iListener = iListener;
        this.isPagination = isPagination;
        this.apiLevel = apiLevel;
        this.isUsername = isUsername;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (apiLevel > 13) {
                convertView = inflater.inflate(R.layout.complaint_list_item, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.complaint_lower_version_list_item, parent,
                        false);
            }
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.status = (TextView) convertView.findViewById(R.id.complaint_status);
            holder.loadMore = (TextView) convertView.findViewById(R.id.load_more);
            holder.loadMore.setOnClickListener(this);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Complaint data = listItem.get(position);
        if (isUsername) {
            holder.name.setText(data.getCreatedBy());
            holder.name.setTypeface(holder.name.getTypeface(), Typeface.BOLD);
        } else {
            holder.name.setVisibility(View.GONE);
        }
        File file = new File(data.getImagePath());

        if (file.exists()) {
            holder.image.setImageBitmap(_getBitmapImage(data.getImagePath()));
        } else {
            holder.image.setImageResource(R.drawable.complaint);
        }
        holder.description.setText(data.getDetails());
        holder.date.setText((data.getCreatedDate().equals("")) ? "" : getActualTime(data
                .getCreatedDate()));
        holder.status.setText(data.getStatus().toLowerCase());
        holder.loadMore.setTypeface(holder.loadMore.getTypeface(), Typeface.BOLD);

        if (getCount() > 5 && (getCount() % 5 == 1) && (position == getCount() - 1) && isPagination) {
            holder.loadMore.setVisibility(View.VISIBLE);
            ((ViewGroup) holder.loadMore.getParent()).getChildAt(0).setVisibility(View.GONE);
        } else {
            holder.loadMore.setVisibility(View.GONE);
            ((ViewGroup) holder.loadMore.getParent()).getChildAt(0).setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private Bitmap _getBitmapImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        return BitmapFactory.decodeFile(path, options);
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
        String currentTime = p.format(new Date(System.currentTimeMillis() - 1000 * 60 * min));
        if (currentTime.equalsIgnoreCase("moments from now")) {
            return currentTime.replaceAll(currentTime, "just now");
        }
        if (currentTime.equalsIgnoreCase("moments ago")) {
            return currentTime.replaceAll(currentTime, "1 minute ago");
        }
        return currentTime;
    }

    static class ViewHolder {
        TextView name;
        TextView description;
        TextView date;
        TextView loadMore;
        TextView status;
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
        if (iListener != null) {
            iListener.actionPerformed("LOAD_MORE", 0);
        }
    }
}
