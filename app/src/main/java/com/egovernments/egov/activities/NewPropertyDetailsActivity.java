package com.egovernments.egov.activities;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.egovernments.egov.models.Property;
import com.egovernments.egov.R;

public class NewPropertyDetailsActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_property_details);
        Property property = (Property) getIntent().getSerializableExtra(PropertyDetailsActivity.PROPERTY_ITEM);


        TextView propertyName = (TextView) findViewById(R.id.searchdetails_property_name);
        TextView propertyAddress = (TextView) findViewById(R.id.searchdetails_property_address);
        TextView propertyOwner = (TextView) findViewById(R.id.searchdetails_property_owner);

        propertyName.setText(property.getPropertyName());
        propertyAddress.setText(property.getPropertyAddress());
        propertyOwner.setText(property.getPropertyOwner());


FloatingActionButton searchDetailsFab = (FloatingActionButton) findViewById(R.id.searchdetails_property_fab);
com.melnykov.fab.FloatingActionButton searchDetailsFabCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.searchdetails_property_fabcompat);

View.OnClickListener onClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Toast.makeText(NewPropertyDetailsActivity.this, "Property added", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NewPropertyDetailsActivity.this, PropertyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
};

if (Build.VERSION.SDK_INT >= 21) {

        searchDetailsFab.setOnClickListener(onClickListener);
        } else {
        searchDetailsFab.setVisibility(View.GONE);
        searchDetailsFabCompat.setVisibility(View.VISIBLE);
        searchDetailsFabCompat.setOnClickListener(onClickListener);
        }
        }
        }
