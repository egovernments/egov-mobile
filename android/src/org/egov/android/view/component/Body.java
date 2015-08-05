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
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class Body extends LinearLayout {
    /**
     * The constructor of the Body class and we supplied the Context as a parameter.
     * initialize the application context.
     */

    public Body(Context context) {
        super(context);
        _init(context, null);
    }

    public Body(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init(context, attrs);
    }

    @SuppressLint("NewApi")
	public Body(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _init(context, attrs);
    }

    /**
     * Function used to set common background to the entire app.
     * 
     * @param context
     * @param attrs
     */
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
