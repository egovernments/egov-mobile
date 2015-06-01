package com.egov.android.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egov.android.R;

public class Header extends RelativeLayout implements OnClickListener {

    public final static int ACTION_BACK = 1;

    public final static int ACTION_SETTING = 2;

    public final static int ACTION_SEARCH = 4;

    public final static int ACTION_ADD_COMPLAINT = 8;

    private OnClickListener actionListener = null;

    public Header(Context context) {
        super(context);
        _init(context, null);
    }

    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init(context, attrs);
    }

    public Header(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _init(context, attrs);
    }

    private void _init(Context context, AttributeSet attrs) {
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_header, this);
        setId(R.id.header);
        setGravity(Gravity.CENTER_VERTICAL);

        if (isInEditMode()) {
            return;
        }

        setBackgroundResource(R.drawable.header);

        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Header);
        ((TextView) findViewById(R.id.hdr_title)).setText(a.getString(R.styleable.Header_title));

        int action = a.getInt(R.styleable.Header_actionButton, 0);

        int id = action & ((int) Math.pow(2, 0));
        LinearLayout rootView = (LinearLayout) this.getChildAt(0);
        if (id == 1) {
            rootView.addView(this._getImageView(ACTION_BACK, R.drawable.back_icon), 0);
        }

        rootView = (LinearLayout) this.getChildAt(1);

        /**
         * Button order from left to right
         */

        SparseArray<Object> sia = new SparseArray<Object>();
        sia.put(ACTION_SETTING, R.drawable.setting_white);
        sia.put(ACTION_SEARCH, 0);
        sia.put(ACTION_ADD_COMPLAINT, R.drawable.add_icon);

        int size = sia.size();
        for (int i = 0; i < size; i++) {
            id = action & ((int) Math.pow(2, i + 1));
            if (id > 0) {
                rootView.addView(this._getImageView(id, Integer.valueOf(sia.get(id).toString())), 0);
            }
        }
    }

    public OnClickListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(OnClickListener actionListener) {
        this.actionListener = actionListener;
    }

    public View _getImageView(int id, int resId) {
        ImageView img = new ImageView(getContext());
        img.setId(id);
        img.setPadding(dpToPixel(8), dpToPixel(15), dpToPixel(8), dpToPixel(15));
        img.setImageResource(resId);
        img.setLayoutParams(new ViewGroup.LayoutParams(dpToPixel(36),
                ViewGroup.LayoutParams.MATCH_PARENT));
        img.setOnClickListener(this);
        return img;
    }

    public View _getTextView(int id, String text) {
        EGovTextView txt = new EGovTextView(new ContextThemeWrapper(getContext(),
                R.style.Header_Text));
        txt.setId(id);
        txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        txt.setText(text);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setOnClickListener(this);
        txt.setPadding(0, 0, dpToPixel(5), 0);
        return txt;
    }

    private int dpToPixel(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public void onClick(View v) {
        /**
         * You can perform any common task here
         */
        if (this.actionListener != null) {
            this.actionListener.onClick(v);
        }
    }

}