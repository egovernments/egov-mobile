package org.egov.android.view.activity;

import java.util.ArrayList;

import org.egov.android.R;
import org.egov.android.model.Category;
import org.egov.android.view.adapter.GridViewAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;

public class PopularCategoryActivity extends BaseActivity implements OnItemClickListener {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ArrayList<Category> category = new ArrayList<Category>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_category);

        String[] name = { "Photoles", "Garbage", "Maintenance", "Sewage drains", "Water Supply",
                "Traffic" };
        int[] image = { R.drawable.pic_1, R.drawable.pic_2, R.drawable.pic_3, R.drawable.pic_4,
                R.drawable.pic_5, R.drawable.pic_6 };

        Category cat = null;
        for (int i = 0; i < name.length; i++) {
            cat = new Category();
            cat.setTitle(name[i]);
            cat.setImage(image[i]);
            category.add(cat);
        }
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, category);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(this);

        ((RelativeLayout) findViewById(R.id.all_category)).setOnClickListener(this);
    }

    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.all_category:
                startActivity(new Intent(this, AllCategoryActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent(this, CreateComplaintActivity.class);
        intent.putExtra("complaint_type", category.get(position).getTitle());
        startActivity(intent);
    }
}
