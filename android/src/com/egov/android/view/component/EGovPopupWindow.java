package com.egov.android.view.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egov.android.R;
import com.egov.android.library.listener.Event;
import com.egov.android.library.listener.IEventDispatcher;

public class EGovPopupWindow extends Dialog implements OnClickListener {

    private String title = "";
    private boolean showTitleBar = true;
    private IEventDispatcher eventDispatcher = null;

    public EGovPopupWindow(Context context, int theme) {
        super(context, R.style.ThemeDialogCustom);
        _init();
    }

    protected EGovPopupWindow(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        _init();
    }

    public EGovPopupWindow(Context context) {
        super(context);
        _init();
    }

    private void _init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);

        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getWindow().setGravity(Gravity.CENTER);
        //        getWindow().setLayout(Util.dpToPx(300, getContext().getResources()),
        //                Util.dpToPx(250, getContext().getResources()));

        setContentView(R.layout.popup_window);
        setTitle(this.title);

        if (showTitleBar) {
            ImageView close = (ImageView) this.findViewById(R.id.popup_close);
            close.setOnClickListener(this);
        } else {
            RelativeLayout titleBar = (RelativeLayout) this.findViewById(R.id.popup_titleBar);
            titleBar.setVisibility(View.GONE);
        }
    }

    public void setContentView(View view) {
        ((LinearLayout) this.findViewById(R.id.popup_container)).addView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (layoutResID == R.layout.popup_window) {
            super.setContentView(layoutResID);
        } else {
            LinearLayout viewRoot = (LinearLayout) this.findViewById(R.id.popup_container);
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(layoutResID, viewRoot);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        TextView txtTitle = (TextView) this.findViewById(R.id.popup_title);
        if (txtTitle == null) {
            this.title = title.toString();
        } else {
            txtTitle.setText(title);
        }
    }

    public void showTitleBar(boolean flag) {
        this.showTitleBar = flag;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void show() {
        super.show();
        if (this.eventDispatcher != null) {
            Event<Object> e = new Event<Object>();
            e.setType("open");
            this.eventDispatcher.dispatchEvent(e);
        }
    }

    @Override
    public void dismiss() {
        if (this.eventDispatcher != null) {
            Event<Object> e = new Event<Object>();
            e.setType("close");
            this.eventDispatcher.dispatchEvent(e);
        }
        super.dismiss();
    }

    public IEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public void setEventDispatcher(IEventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }
}
