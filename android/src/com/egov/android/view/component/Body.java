package com.egov.android.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.egov.android.R;

public class Body extends LinearLayout {

    public Body(Context context) {
        super(context);
        _init(context, null);
    }

    public Body(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init(context, attrs);
    }

    public Body(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _init(context, attrs);
    }

    private void _init(Context context, AttributeSet attrs) {

        setOrientation(VERTICAL);
        if (isInEditMode()) {
            return;
        }

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    new int[] { android.R.attr.background });
            if (a.getResourceId(0, -1) == -1 && a.getColor(0, -1) == -1
                    && (a.getString(0) == null || a.getString(0).equals(""))) {
                setBackgroundColor(getResources().getColor(R.color.background));
            }
        }
    }

}
