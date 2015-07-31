package org.egov.android.view.activity;

import java.util.ArrayList;

import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.library.api.ApiResponse;
import org.egov.android.library.api.IApiListener;
import org.egov.android.library.listener.Event;
import org.egov.android.model.Category;
import org.egov.android.view.adapter.CategoryAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AllCategoryActivity extends BaseActivity implements IApiListener, OnItemClickListener {

    private ArrayList<Category> listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category);

        ApiController.getInstance().getAllCategory(this);
    }

    private void _displayListView() {
        ListView list = (ListView) findViewById(R.id.all_category_list);
        CategoryAdapter adapter = new CategoryAdapter(this, listItem);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);
    }

    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        if (status.equalsIgnoreCase("success")) {
            try {
                JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                listItem = new ArrayList<Category>();
                Category item = null;
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    item = new Category();
                    item.setTitle(jo.getString("name"));
                    listItem.add(item);
                }
                _displayListView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent(this, CreateComplaintActivity.class);
        intent.putExtra("complaint_type", listItem.get(position).getTitle());
        startActivity(intent);
    }
}
