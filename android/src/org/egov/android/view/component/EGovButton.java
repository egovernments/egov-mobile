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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

public class EGovButton extends Button implements OnTouchListener {

    private Context context = null;
    /**
     * The constructor of the EGovButton class and we supplied the Context as a parameter.
     * initialize the application context.
     */

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

    /**
     * Event triggered when touch the EgovButton component. Apply effects when touch the component.
     */
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

    /**
     * Function used to set common font style to the buttons in entire app
     * OnTouchListener to Detect Common Gestures Like Tap and Swipes on Android
     * Apply the fontface  using customFont. 
     * If the customfont value is null then we apply the default app font
     * @param context
     * @param attrs
     */
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
