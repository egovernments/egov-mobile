package com.egovernments.egov.helper;

import android.view.View;


public class CardViewOnClickListener implements View.OnClickListener {
    private int position;
    private OnItemClickCallback onItemClickCallback;

    public CardViewOnClickListener(int position, OnItemClickCallback onItemClickCallback) {
        this.position = position;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public void onClick(View view) {
        if(onItemClickCallback!=null) {
            onItemClickCallback.onItemClicked(view, position);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(View view, int position);
    }
}