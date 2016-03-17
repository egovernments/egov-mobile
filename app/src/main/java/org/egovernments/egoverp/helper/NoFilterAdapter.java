package org.egovernments.egoverp.helper;


import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.List;


/**
 * Adapter used on autocompleteTextView to prevent results from being cut off
 **/

public class NoFilterAdapter<T> extends ArrayAdapter<T> {
    private Filter filter = new KNoFilter();
    private List<T> items;

    @Override
    public Filter getFilter() {
        return filter;
    }

    public NoFilterAdapter(Context context, int textViewResourceId,
                           List<T> objects) {
        super(context, android.R.layout.simple_spinner_dropdown_item, objects);
        items = objects;
    }

    public List<T> getItems() {
        return items;
    }

    private class KNoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence arg0) {
            FilterResults result = new FilterResults();
            result.values = items;
            result.count = items.size();
            return result;
        }

        @Override
        protected void publishResults(CharSequence arg0, FilterResults arg1) {
            notifyDataSetChanged();
        }
    }
}