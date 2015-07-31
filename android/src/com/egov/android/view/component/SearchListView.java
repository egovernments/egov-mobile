package com.egov.android.view.component;

import java.util.List;

import com.egov.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.egov.android.library.view.adapter.BaseListAdapter;
import com.egov.android.model.Complaint;
import com.egov.android.view.adapter.ComplaintAdapter1;

public class SearchListView extends LinearLayout implements OnItemClickListener {

    public static String Tag = "Header";

    private ComplaintAdapter1<?> adapter = null;
    private ListView listView = null;
    private OnItemClickListener itemClickListener = null;

    public SearchListView(Context context) {
        super(context);
        _init(context, null);
    }

    public SearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init(context, attrs);
    }

    public SearchListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _init(context, attrs);
    }

    @SuppressWarnings("unchecked")
    public void setListItem(List<? extends Complaint> listItem) {
        this.adapter = new ComplaintAdapter1<Complaint>(getContext(), (List<Complaint>) listItem);
        listView.setAdapter(this.adapter);

    }

    private void _init(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_search_listview, this);
        setOrientation(LinearLayout.VERTICAL);

        listView = (ListView) getChildAt(1);
        listView.setOnItemClickListener(this);

        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.SearchListView);
            if (arr.getBoolean(R.styleable.SearchListView_useSearchFeature, true)) {
                RelativeLayout ly = (RelativeLayout) getChildAt(0);
                ly.setVisibility(RelativeLayout.VISIBLE);
                EGovEditText txt = (EGovEditText) ly.getChildAt(0);
                txt.setHint(arr.getString(R.styleable.SearchListView_typeHint));
                txt.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
                        adapter.getFilter().filter(text);
                        if (adapter.getSelectedItemPosition() != -1) {
                            adapter.setSelectedItemPosition(-1);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                    }
                });
            }

            arr.recycle();
        }
    }

    public BaseListAdapter<?> getAdapter() {
        return adapter;
    }

    public void setAdapter(ComplaintAdapter1<?> adapter) {
        this.adapter = adapter;
        listView.setAdapter(adapter);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public ListView getListView() {
        return this.listView;
    }

    public Object getSelectedItem() {
        return adapter.getSelectedItem();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        adapter.setSelectedItemPosition(position);
        adapter.notifyDataSetChanged();
        if (this.itemClickListener != null) {
            this.itemClickListener.onItemClick(adapterView, view, position, arg3);
        }
    }
}