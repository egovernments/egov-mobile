package org.egov.android.view.component;

import org.egov.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

public class EGovButton extends Button implements OnTouchListener {

    private Context context = null;

    public EGovButton(Context context) {
        super(context);
        _init(context, null);
    }

    public EGovButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _init(context, attrs);
    }

    public EGovButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init(context, attrs);
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                AlphaAnimation alpha = new AlphaAnimation(1.0F, 0.5F);
                alpha.setDuration(300);
                alpha.setFillAfter(true);
                this.startAnimation(alpha);
                break;
            case MotionEvent.ACTION_UP:
                alpha = new AlphaAnimation(0.5F, 1.0F);
                alpha.setDuration(300);
                alpha.setFillAfter(true);
                this.startAnimation(alpha);
                break;
        }
        return false;
    }

    private void _init(Context context, AttributeSet attrs) {
        this.context = context;
        if (isInEditMode()) {
            return;
        }
        this.setOnTouchListener(this);
        if (attrs != null) {
            TypedArray arr = this.context.obtainStyledAttributes(attrs, R.styleable.EGovButton);
            String customFont = arr.getString(R.styleable.EGovButton_fontName);

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
