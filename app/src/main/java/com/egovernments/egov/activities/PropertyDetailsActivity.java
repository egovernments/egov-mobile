package com.egovernments.egov.activities;


import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.egovernments.egov.models.Property;
import com.egovernments.egov.R;

public class PropertyDetailsActivity extends BaseActivity {

    public static final String PROPERTYITEM ="PropertyItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);
        Property property = (Property) getIntent().getSerializableExtra(PROPERTYITEM);

        TextView propertyName = (TextView) findViewById(R.id.property_name);
        TextView propertyAddress = (TextView) findViewById(R.id.property_address);
        TextView propertyOwner = (TextView) findViewById(R.id.property_owner);
        TextView propertyTax = (TextView) findViewById(R.id.property_tax);

        propertyName.setText(property.getPropertyName());
        propertyAddress.setText(property.getPropertyAddress());
        propertyOwner.setText(property.getPropertyOwner());
        propertyTax.setText(property.getPropertyTax());

        FloatingActionButton propertyFab = (FloatingActionButton) findViewById(R.id.property_fab);
        com.melnykov.fab.FloatingActionButton propertyFabcompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.property_fabcompat);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PropertyDetailsActivity.this, "Redirect to payment portal", Toast.LENGTH_SHORT).show();
            }
        };
        if (Build.VERSION.SDK_INT >= 21) {

            propertyFab.setOnClickListener(onClickListener);
        } else {
            propertyFab.setVisibility(View.GONE);
            propertyFabcompat.setVisibility(View.VISIBLE);

            propertyFabcompat.setOnClickListener(onClickListener);
        }
    }
}
