package org.egovernments.egoverp.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(homeItem.getIcon(),0,0,0);
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

        private CardView cardView;

        public HomeViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.home_item_title);
            description = (TextView) v.findViewById(R.id.home_item_text);
            cardView = (CardView) v.findViewById(R.id.home_card);
        }

    }
}
