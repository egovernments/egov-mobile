package com.egovernments.egov.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.egovernments.egov.models.Notification;
import com.egovernments.egov.R;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

public class NotificationsAdapter extends BaseAdapter {

    private WeakReference<Context> contextWeakReference;
    private List<Notification> notifications;

    public NotificationsAdapter(List<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NotificationsViewHolder notificationsViewHolder = null;
        View view = convertView;
        if (convertView == null) {

            LayoutInflater layoutInflater = LayoutInflater.from(contextWeakReference.get());
            view = layoutInflater.inflate(R.layout.item_notification, parent, false);


            notificationsViewHolder = new NotificationsViewHolder();
            notificationsViewHolder.notificationTitle = (TextView) view.findViewById(R.id.notification_title);
            notificationsViewHolder.notificationDetails = (TextView) view.findViewById(R.id.notification_details);
            notificationsViewHolder.notificationTime = (TextView) view.findViewById(R.id.notification_time);
            notificationsViewHolder.notificationIcon = (ImageView) view.findViewById(R.id.notification_icon);

            view.setTag(notificationsViewHolder);
        }

        if(notificationsViewHolder==null){
            notificationsViewHolder = (NotificationsViewHolder) view.getTag();
        }
            Notification notification = (Notification) getItem(position);

            notificationsViewHolder.notificationTitle.setText(notification.getTitle());
            notificationsViewHolder.notificationDetails.setText(notification.getDetails());
            notificationsViewHolder.notificationTime.setText(time(notification.getTime()));
            notificationsViewHolder.notificationIcon.setImageResource(getIcon(notification.getForwarding_activity()));

            return view;
        }

    private int getIcon(int forwarding_activity) {
        switch (forwarding_activity) {
            case 0:
                return R.drawable.ic_feedback_black_24dp;

            case 1:
                return R.drawable.ic_home_black_24dp;

            case 6:
                return R.drawable.ic_person_black_24dp;

            case 7:
                return R.drawable.ic_notifications_black_24dp;
        }

        return R.drawable.ic_mail_black_24dp;
    }

    private String time(Calendar calendar) {
        Calendar now = Calendar.getInstance();
        int difference;

        difference = now.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
        if (difference > 1)
            return (difference + " years ago");
        else if (difference == 1)
            return (difference + " year ago");

        difference = now.get(Calendar.MONTH) - calendar.get(Calendar.MONTH);
        if (difference > 1)
            return (difference + " months ago");
        else if (difference == 1)
            return (difference + " month ago");

        difference = now.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH);
        if (difference == 1)
            return (difference + " day ago");

        else if (difference > 1)
            return difference + " days ago";

        difference = now.get(Calendar.HOUR) - calendar.get(Calendar.HOUR);
        if (difference == 1)
            return (difference + " hour ago");

        else if (difference > 1)
            return difference + " hours ago";

        difference = now.get(Calendar.MINUTE) - calendar.get(Calendar.MINUTE);
        if (difference == 1)
            return (difference + " minute ago");

        else if (difference > 1)
            return difference + " minutes ago";

        difference = now.get(Calendar.SECOND) - calendar.get(Calendar.SECOND);
        if (difference == 1)
            return (difference + " second ago");

        return difference + " seconds ago";
    }


    public static class NotificationsViewHolder {

        private TextView notificationTitle;
        private TextView notificationDetails;
        private TextView notificationTime;
        private ImageView notificationIcon;
    }
}
