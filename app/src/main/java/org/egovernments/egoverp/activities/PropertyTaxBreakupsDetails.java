package org.egovernments.egoverp.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.TaxAdapter;
import org.egovernments.egoverp.models.TaxDetail;

import java.util.ArrayList;

public class PropertyTaxBreakupsDetails extends AppCompatActivity {

    ArrayList<TaxDetail> taxBreakups;
    ListView lvBreakups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_tax_breakups_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvBreakups=(ListView)findViewById(R.id.lvbreakups);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String jsonObject=getIntent().getExtras().getString("breakupsList");
        taxBreakups=new Gson().fromJson(jsonObject, new TypeToken<ArrayList<TaxDetail>>(){}.getType());

        lvBreakups.setAdapter(new TaxAdapter(taxBreakups, PropertyTaxBreakupsDetails.this));


    }

}
