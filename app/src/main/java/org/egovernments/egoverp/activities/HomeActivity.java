package org.egovernments.egoverp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.HomeAdapter;
import org.egovernments.egoverp.helper.CardViewOnClickListener;
import org.egovernments.egoverp.models.HomeItem;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        List<HomeItem> homeItemList = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.home_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        CardViewOnClickListener.OnItemClickCallback onItemClickCallback = new CardViewOnClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {

                switch (position) {
                    case 0:
                        startActivity(new Intent(HomeActivity.this, GrievanceActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(HomeActivity.this, PropertyTaxSearchActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(HomeActivity.this, WaterTaxSearchActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                        break;
                }

            }
        };

        homeItemList.add(new HomeItem("Grievances", R.drawable.ic_announcement_black_36dp, "File grievances or review and update previously filed grievances"));
        homeItemList.add(new HomeItem("Property tax", R.drawable.ic_business_black_36dp, "View property tax details for an assessment number"));
        homeItemList.add(new HomeItem("Water tax", R.drawable.ic_local_drink_black_36dp, "View water tax details for a consumer code"));
        homeItemList.add(new HomeItem("Profile", R.drawable.ic_person_black_36dp, "Update or review your profile details."));

        HomeAdapter homeAdapter = new HomeAdapter(homeItemList, onItemClickCallback);
        recyclerView.setAdapter(homeAdapter);

    }
}
