package com.egov.android.view.activity;

import java.util.ArrayList;
import java.util.List;

import com.egov.android.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.egov.android.model.Complaint;
import com.egov.android.view.adapter.ComplaintAdapter;
import com.egov.android.view.component.SearchListView;

public class ComplaintListActivity extends BaseActivity implements OnItemClickListener {

    private ComplaintAdapter<Complaint> adapter = null;
    private List<Complaint> listItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_list);

        String[] values = new String[] { "Burning Of Solid Wastes", "Cleaners Not Coming",
                "Clearing off the Dead Animals", "Clearing Off The Dust",
                "Hospitals and Dispensaries", "Clearing Off The Cow Dung" };

        listItem = new ArrayList<Complaint>();
        Complaint item = null;

        for (int i = 0; i < values.length; i++) {
            item = new Complaint();
            item.setName(values[i]);
            listItem.add(item);
        }

        adapter = new ComplaintAdapter<Complaint>(this, listItem, true);
        SearchListView listView = (SearchListView) findViewById(R.id.list_complaint);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
        Intent intent = new Intent(this, ComplaintDetailActivity.class);
        intent.putExtra("name", listItem.get(position).getName());
        startActivity(intent);
    }
}
