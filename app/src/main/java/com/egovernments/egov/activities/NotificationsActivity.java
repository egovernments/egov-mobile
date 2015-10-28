package com.egovernments.egov.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.egovernments.egov.adapters.NotificationsAdapter;
import com.egovernments.egov.models.Notification;
import com.egovernments.egov.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class NotificationsActivity extends BaseActivity {


    private static List<Notification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notifications);

        final ListView listView = (ListView) findViewById(R.id.notifications_list);

        NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notifications, this);

        listView.setAdapter(notificationsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Notification notification = (Notification) listView.getItemAtPosition(position);
                int forward = notification.getForwarding_activity();
                Intent intent;
                switch (forward) {

                    case 0:
                        intent = new Intent(NotificationsActivity.this, GrievanceActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    case 1:
                        intent = new Intent(NotificationsActivity.this, PropertyActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    case 6:
                        intent = new Intent(NotificationsActivity.this, ProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    case 7:
                        intent = new Intent(NotificationsActivity.this, NotificationsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    case 9:
                        intent = new Intent(NotificationsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                }

            }
        });


    }

    public static void createList() {

        notifications.add(new Notification("Setup your profile", "Something", Calendar.getInstance(), 6));
        notifications.add(new Notification("Grievance under review", "Something", new GregorianCalendar(2015, 8, 23), 0));
        notifications.add(new Notification("Property added", "Something", new GregorianCalendar(2015, 6, 31), 1));
    }

    public static int getCount() {
        return notifications.size();
    }


}
