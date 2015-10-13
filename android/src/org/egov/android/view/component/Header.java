/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.view.component;

import org.egov.android.R;

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

public class Header extends RelativeLayout implements OnClickListener {

    public final static int ACTION_BACK = 1;

    public final static int ACTION_SETTING = 2;

    public final static int ACTION_SEARCH = 8;

    public final static int ACTION_ADD_COMPLAINT = 4;

    public final static int ACTION_REFRESH = 16;

    private OnClickListener actionListener = null;
    /**
     * The constructor of the Header class and we supplied the Context as a parameter.
     * initialize the application context.
     */

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

    /**
     * Set the layout of the header.
     * Set the background to the header resource. 
     * @param context
     * @param attrs
     */
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
        sia.put(ACTION_SEARCH, R.drawable.header_search_icon);
        sia.put(ACTION_SETTING, R.drawable.setting_white);
        sia.put(ACTION_ADD_COMPLAINT, R.drawable.ic_add_white_24dp);
        sia.put(ACTION_REFRESH, R.drawable.refresh_icon);

        int size = sia.size();
        for (int i = 0; i < size; i++) {
            id = action & ((int) Math.pow(2, i + 1));
            if (id > 0) {
                rootView.addView(this._getImageView(id, Integer.valueOf(sia.get(id).toString())), 0);
            }
        }
    }

    /**
     * Interface definition for a callback to be invoked when a view is clicked.
     * 
     * @return
     */
    public OnClickListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(OnClickListener actionListener) {
        this.actionListener = actionListener;
    }

    /**
     * Function used to get the image view having the given id and resource id.
     * add click listener and Sets the padding.
     * 
     * @param id
     * @param resId
     * @return
     */
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

    /**
     * Function used to get the text view having the given id and text.
     * add click listener and sets the padding.
     * add layout param such as wrap content and match parent.
     * 
     * @param id
     * @param text
     * @return
     */
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

    /**
     * Function used to get pixel value from dp unit
     * 
     * @param dp
     * @return
     */
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