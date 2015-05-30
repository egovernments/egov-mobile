package com.egov.android.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.egov.android.R;

public class EGovTextView extends TextView {

    private Context context = null;

    public EGovTextView(Context context) {
        super(context);
        _init(context, null, 0);
    }

    public EGovTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init(context, attrs, 0);
    }

    public EGovTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _init(context, attrs, defStyle);
    }

    private void _init(Context context, AttributeSet attrs, int defStyle) {
        this.context = context;
        if (isInEditMode()) {
            return;
        }
        if (attrs != null) {
            TypedArray arr = this.context.obtainStyledAttributes(attrs, R.styleable.EGovTextView);
            String customFont = arr.getString(R.styleable.EGovTextView_fontName);

            if (customFont == null) {
                customFont = getResources().getString(R.string.appFont);
            }
            try {
                setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + customFont));
            } catch (Exception ex) {

            }

            arr.recycle();
        }

    }
}