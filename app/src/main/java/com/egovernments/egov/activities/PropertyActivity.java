package com.egovernments.egov.activities;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.egovernments.egov.adapters.PropertyAdapter;
import com.egovernments.egov.helper.CardViewOnClickListener;
import com.egovernments.egov.models.Property;
import com.egovernments.egov.R;

import java.util.ArrayList;
import java.util.List;


public class PropertyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recyclerview_template);
        RecyclerView recList = (RecyclerView) findViewById(R.id.recylerview);
        recList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.recylerview_placeholder);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.recylerview_data);

        final List<Property> properties = createList();

        CardViewOnClickListener.OnItemClickCallback onItemClickCallback = new CardViewOnClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {

                Intent intent = new Intent(PropertyActivity.this, PropertyDetailsActivity.class);
                intent.putExtra(PropertyDetailsActivity.PROPERTYITEM, properties.get(position));
                startActivity(intent);

            }
        };

        PropertyAdapter propertyAdapter = new PropertyAdapter(createList(), onItemClickCallback);
        recList.setAdapter(propertyAdapter);
        relativeLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        FloatingActionButton newPropertyButton = (FloatingActionButton) findViewById(R.id.list_fab);
        com.melnykov.fab.FloatingActionButton newPropertybuttoncompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.list_fabcompat);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(PropertyActivity.this, NewPropertyActivity.class));

            }
        };
        if (Build.VERSION.SDK_INT >= 21) {

            newPropertyButton.setOnClickListener(onClickListener);
        } else {
            newPropertyButton.setVisibility(View.GONE);
            newPropertybuttoncompat.setVisibility(View.VISIBLE);
            newPropertybuttoncompat.setOnClickListener(onClickListener);
        }


    }


    private List<Property> createList() {

        List<Property> result = new ArrayList<>();

        result.add(new Property("Khosla labs", "18/2A, GRS Towers, Second Floor, Above Spencer's HyperMart, Sarjapur Main Road, Bengaluru, Karnataka 560103", "Vinod Khosla", "Rs. 100000"));
        result.add(new Property("Somewhere", "1-1-A, Some Road", "Somebody", "Rs.17563564"));

        return result;
    }
}