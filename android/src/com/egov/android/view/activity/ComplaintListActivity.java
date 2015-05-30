package com.egov.android.view.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.egov.android.R;
import com.egov.android.model.Complaint;
import com.egov.android.view.adapter.SearchListAdapter;

public class ComplaintListActivity extends BaseActivity implements OnItemClickListener {

    private SearchListAdapter<Complaint> adapter = null;
    private List<Complaint> listItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_list);

        String[] values = new String[] { "Complaint 1", "Complaint 2", "Complaint 3",
                "Complaint 4", "Complaint 5", "Complaint 6", "Complaint 7", "Complaint 8",
                "Complaint 9", "Complaint 10", "Complaint 11", "Complaint 12", "Complaint 13",
                "Complaint 14", "Complaint 15", "Complaint 16" };

        listItem = new ArrayList<Complaint>();
        Complaint item = null;

        for (int i = 0; i < values.length; i++) {
            item = new Complaint();
            item.setName(values[i]);
            listItem.add(item);
        }

        adapter = new SearchListAdapter<Complaint>(this, listItem);
        ListView listView = (ListView) findViewById(R.id.list_complaint);
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
