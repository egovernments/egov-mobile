package org.egov.android.view.component;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class EGovImageView extends ImageView {
    public EGovImageView(Context context) {
        super(context);
    }

    public EGovImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EGovImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            int w = MeasureSpec.getSize(widthMeasureSpec);
            int h = w * d.getIntrinsicHeight() / d.getIntrinsicWidth();
            setMeasuredDimension(w, h);
        } else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
