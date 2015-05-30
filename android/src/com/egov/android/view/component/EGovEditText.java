package com.egov.android.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.egov.android.R;

public class EGovEditText extends EditText {

    private Context context = null;

    public EGovEditText(Context context) {
        super(context);
        _init(context, null);
    }

    public EGovEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init(context, attrs);
    }

    public EGovEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _init(context, attrs);
    }

    private void _init(Context context, AttributeSet attrs) {
        this.context = context;
        if (isInEditMode()) {
            return;
        }
        if (attrs != null) {
            TypedArray arr = this.context.obtainStyledAttributes(attrs, R.styleable.EGovEditText);
            String customFont = arr.getString(R.styleable.EGovEditText_fontName);

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
