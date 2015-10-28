package com.egovernments.egov.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.egovernments.egov.R;
import com.egovernments.egov.adapters.PropertyAdapter;
import com.egovernments.egov.helper.CardViewOnClickListener;
import com.egovernments.egov.models.Property;

import java.util.ArrayList;
import java.util.List;

public class NewPropertyActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_property);

        final RecyclerView recList = (RecyclerView) findViewById(R.id.propertysearchlist);
        recList.setHasFixedSize(true);


        final List<Property> propertyList = new ArrayList<>();

        final CardViewOnClickListener.OnItemClickCallback onItemClickCallback = new CardViewOnClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {

                Intent intent = new Intent(NewPropertyActivity.this, NewPropertyDetailsActivity.class);
                intent.putExtra(PropertyDetailsActivity.PROPERTYITEM, propertyList.get(position));
                startActivity(intent);

            }
        };

        EditText searchEditText = (EditText) findViewById(R.id.searchTextbox);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    propertyList.add(new Property("A Place", "Nearby, Yolo Swaggins road, mg street,Bangalore, 532048", "The People", "Rs. 1456378"));
                    recList.setAdapter(new PropertyAdapter(propertyList, onItemClickCallback));
                    return true;
                }
                return false;
            }
        });

        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(0);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


    }

}

