package org.egov.employee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.egov.employee.data.MultiDistrictsAPIResponse;

import java.util.List;

/**
 * Created by egov on 25/4/16.
 */
public class CitiesAdapter extends ArrayAdapter<Object> {

    List<?> autoSuggestionOptions;
    Context context;
    int resourceId;

    public CitiesAdapter(Context context, int resourceId, List<?> autoSuggestionOptions) {
        super(context, resourceId);
        this.context=context;
        this.resourceId=resourceId;
        this.autoSuggestionOptions=autoSuggestionOptions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, parent, false);
        }

        Object option=autoSuggestionOptions.get(position);

        TextView textViewItem = (TextView) convertView;

        if(option instanceof MultiDistrictsAPIResponse)
        {
            textViewItem.setText(((MultiDistrictsAPIResponse) option).getDistrictName());
        }
        else if(option instanceof MultiDistrictsAPIResponse.City){
            textViewItem.setText(((MultiDistrictsAPIResponse.City) option).getCityName());
        }

        return super.getView(position, convertView, parent);
    }
}
