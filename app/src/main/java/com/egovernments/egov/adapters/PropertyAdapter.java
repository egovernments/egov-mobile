package com.egovernments.egov.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.egovernments.egov.helper.CardViewOnClickListener;
import com.egovernments.egov.models.Property;
import com.egovernments.egov.R;

import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private List<Property> propertyList;
    private CardViewOnClickListener.OnItemClickCallback onItemClickCallback;

    public PropertyAdapter(List<Property> propertyList, CardViewOnClickListener.OnItemClickCallback onItemClickCallback) {
        this.propertyList = propertyList;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    @Override
    public void onBindViewHolder(PropertyViewHolder propertyViewHolder, int i) {

        Property property = propertyList.get(i);
        propertyViewHolder.propertyName.setText(property.getPropertyName());
        propertyViewHolder.propertyAddress.setText(property.getPropertyAddress());
        propertyViewHolder.propertyOwner.setText("Owner: " + property.getPropertyOwner());
        propertyViewHolder.propertyTax.setText("Tax due: " + property.getPropertyTax());
        propertyViewHolder.propertyCardView.setOnClickListener(new CardViewOnClickListener(i, onItemClickCallback));

    }

    @Override
    public PropertyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_property, viewGroup, false);


        return new PropertyViewHolder(itemView);
    }


    public static class PropertyViewHolder extends RecyclerView.ViewHolder {

        private TextView propertyName;
        private TextView propertyAddress;
        private TextView propertyOwner;
        private TextView propertyTax;
        private CardView propertyCardView;

        public PropertyViewHolder(View v) {
            super(v);

            propertyName = (TextView) v.findViewById(R.id.list_property_name);
            propertyAddress = (TextView) v.findViewById(R.id.list_property_address);
            propertyOwner = (TextView) v.findViewById(R.id.list_property_owner);
            propertyTax = (TextView) v.findViewById(R.id.list_property_tax);
            propertyCardView = (CardView) v.findViewById(R.id.property_card);
        }
    }


}
