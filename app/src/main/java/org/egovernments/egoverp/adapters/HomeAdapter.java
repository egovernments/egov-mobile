/*
 *    eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (c) 2016  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egovernments.egoverp.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.CardViewOnClickListener;
import org.egovernments.egoverp.models.HomeItem;

import java.util.List;

/**
 * Custom adapter for the home activity recycler view
 **/

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private List<HomeItem> homeItemList;
    private CardViewOnClickListener.OnItemClickCallback onItemClickCallback;

    public HomeAdapter(List<HomeItem> homeItemList, CardViewOnClickListener.OnItemClickCallback onItemClickCallback) {
        this.homeItemList = homeItemList;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public int getItemCount() {
        return homeItemList.size();
    }

    @Override
    public void onBindViewHolder(final HomeViewHolder viewHolder, final int i) {

        HomeItem homeItem = homeItemList.get(i);

        viewHolder.title.setText(homeItem.getTitle());
        //viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(homeItem.getIcon(),0,0,0);
        viewHolder.cardIcon.setImageResource(homeItem.getIcon());
        viewHolder.description.setText(homeItem.getDescription());

        viewHolder.cardView.setOnClickListener(new CardViewOnClickListener(i, onItemClickCallback));

    }

    //ViewHolder for the recycler view. Inflates view to be grievance or progress indicator depending on type
    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_home, viewGroup, false);
        return new HomeViewHolder(itemView);

    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView description;
        private ImageView cardIcon;

        private CardView cardView;

        public HomeViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.home_item_title);
            description = (TextView) v.findViewById(R.id.home_item_text);
            cardIcon=(ImageView)v.findViewById(R.id.home_item_icon);
            cardView = (CardView) v.findViewById(R.id.home_card);
        }

    }
}
