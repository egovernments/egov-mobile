/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egov.employee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.egov.employee.data.MultiDistrictsAPIResponse;

import java.util.List;

/**
 * Created by egov on 25/4/16.
 */
public class CitiesAdapter extends ArrayAdapter<Object> {

    List<?> autoSuggestionOptions;
    Context context;
    int resourceId;

    public CitiesAdapter(Context context, int resourceId, List<?> autoSuggestionOptions) {
        super(context, resourceId);
        this.context=context;
        this.resourceId=resourceId;
        this.autoSuggestionOptions=autoSuggestionOptions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, parent, false);
        }

        Object option=autoSuggestionOptions.get(position);

        TextView textViewItem = (TextView) convertView;

        if(option instanceof MultiDistrictsAPIResponse)
        {
            textViewItem.setText(((MultiDistrictsAPIResponse) option).getDistrictName());
        }
        else if(option instanceof MultiDistrictsAPIResponse.City){
            textViewItem.setText(((MultiDistrictsAPIResponse.City) option).getCityName());
        }

        return super.getView(position, convertView, parent);
    }
}
