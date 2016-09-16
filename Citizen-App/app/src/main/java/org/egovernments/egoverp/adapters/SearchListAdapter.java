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

package org.egovernments.egoverp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.models.SearchResultItem;

import java.util.List;

/**
 * Created by egov on 15/12/15.
 */
public class SearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<SearchResultItem> results;
    SearchItemClickListener itemClickListener;

    public SearchListAdapter(Context context, List<SearchResultItem> results, SearchItemClickListener itemClickListener)
    {
        this.results =results;
        this.itemClickListener=itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType==VIEW_ITEM) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_list, viewGroup, false);
            vh = new SearchResultViewHolder(v);
        }
        else
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_common_progress_item, viewGroup, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        return results.get(position)!=null? VIEW_ITEM: VIEW_PROG;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof SearchResultViewHolder)
        {
            SearchResultViewHolder searchViewHolder=(SearchResultViewHolder) holder;

            SearchResultItem searchResultItem=results.get(position);

            searchViewHolder.tvtitle.setText(searchResultItem.getTitleText());
            searchViewHolder.tvSecondary.setText(searchResultItem.getSecondaryText());
            searchViewHolder.tvOther.setText(searchResultItem.getOtherText());

            if(!TextUtils.isEmpty(searchResultItem.getRightInfoText()))
            {
              searchViewHolder.tvRightInfo.setText(searchViewHolder.tvRightInfo.getContext().getString(R.string.Rs)+" "+searchResultItem.getRightInfoText());
            }

            searchViewHolder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(position);
                }
            });
        }
        else
        {
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout parent;
        private TextView tvtitle;
        private TextView tvSecondary;
        private TextView tvOther;
        private TextView tvRightInfo;

        public SearchResultViewHolder(View v) {
            super(v);
            v.setClickable(true);
            parent=(LinearLayout)v.findViewById(R.id.parent);
            tvtitle = (TextView) v.findViewById(R.id.tvTitle);
            tvSecondary = (TextView) v.findViewById(R.id.tvSecondary);
            tvOther = (TextView) v.findViewById(R.id.tvOther);
            tvRightInfo=(TextView)v.findViewById(R.id.tvRightInfo);
        }

    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder{

        ProgressBar progressBar;
        ProgressViewHolder(View itemView)
        {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        }

    }

    public interface SearchItemClickListener{
        void onItemClick(int position);
    }


}
