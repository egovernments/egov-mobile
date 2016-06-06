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

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.activities.HomeActivity;
import org.egovernments.egoverp.helper.CardViewOnClickListener;
import org.egovernments.egoverp.models.HomeItem;
import org.egovernments.egoverp.network.UpdateService;

import java.util.List;

/**
 * Custom adapter for the home activity recycler view
 **/

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private List<HomeItem> homeItemList;
    private CardViewOnClickListener.OnItemClickCallback onItemClickCallback;
    private Context context;

    public HomeAdapter(Context context, List<HomeItem> homeItemList, CardViewOnClickListener.OnItemClickCallback onItemClickCallback) {
        this.homeItemList = homeItemList;
        this.onItemClickCallback = onItemClickCallback;
        this.context=context;
    }

    @Override
    public int getItemCount() {
        return homeItemList.size();
    }

    public HomeItem getItem(int position)
    {
        return homeItemList.get(position);
    }

    @Override
    public void onBindViewHolder(final HomeViewHolder viewHolder, final int i) {

        HomeItem homeItem = homeItemList.get(i);

        viewHolder.title.setText(homeItem.getTitle());
        //viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(homeItem.getIcon(),0,0,0);
        viewHolder.cardIcon.setImageResource(homeItem.getIcon());
        viewHolder.description.setText(homeItem.getDescription());

        if(homeItem.isGrievanceItem())
        {
            viewHolder.layoutGrievance.setVisibility(View.VISIBLE);
            DrawableCompat.setTint(viewHolder.pbGrievanceFiled.getProgressDrawable(), context.getResources().getColor(R.color.blue));
            DrawableCompat.setTint(viewHolder.pbGrievanceResolved.getProgressDrawable(), context.getResources().getColor(R.color.green));

            viewHolder.pbGrievanceFiled.setIndeterminate(true);
            viewHolder.pbGrievanceResolved.setIndeterminate(true);

            //service receiver event
            BroadcastReceiver mGrievanceInfoReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {


                    boolean isSuccess=intent.getBooleanExtra("success",false);
                    if(isSuccess)
                    {
                        JsonObject data=new JsonParser().parse(intent.getStringExtra("data")).getAsJsonObject();
                        int totalComplaint=data.get("FILED").getAsInt();
                        int totalResolvedComplaint=data.get("RESOLVED").getAsInt();

                        viewHolder.pbGrievanceFiled.setIndeterminate(false);
                        viewHolder.pbGrievanceResolved.setIndeterminate(false);

                        animateTextView(viewHolder.tvGrievanceFiledCount, totalComplaint);
                        animateTextView(viewHolder.tvGrievanceResolvedCount, totalResolvedComplaint);
                        animateProgressBar(viewHolder.pbGrievanceFiled, totalComplaint, totalComplaint);
                        animateProgressBar(viewHolder.pbGrievanceResolved, totalComplaint, totalResolvedComplaint);

                    }
                    else{
                        viewHolder.pbGrievanceFiled.setIndeterminate(false);
                        viewHolder.pbGrievanceResolved.setIndeterminate(false);
                        viewHolder.tvGrievanceFiledCount.setText("<ERROR>");
                        viewHolder.tvGrievanceResolvedCount.setText("<ERROR>");
                    }

                }
            };

            LocalBroadcastManager.getInstance(context).registerReceiver(mGrievanceInfoReceiver,
                    new IntentFilter(HomeActivity.GRIEVANCE_INFO_BROADCAST));

            //start service for get complaints total count info
            context.startService(new Intent(context, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.GET_GRIEVANCE_COUNT_INFO));


            /*animateProgressBar(viewHolder.pbGrievanceFiled, 6000, 4850);
            animateProgressBar(viewHolder.pbGrievanceResolved, 6000, 1250);
            animateTextView(viewHolder.tvGrievanceFiledCount, 4850);
            animateTextView(viewHolder.tvGrievanceResolvedCount, 1250);*/

        }

        viewHolder.cardView.setOnClickListener(new CardViewOnClickListener(i, onItemClickCallback));

    }

    public void animateProgressBar(ProgressBar progressBar, int max, int progress)
    {
        progressBar.setMax(max);
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, progress);
        progressAnimator.setDuration(2000);
        progressAnimator.start();
    }

    public void animateTextView(final TextView textView, int count){
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(0, count);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animator.setDuration(2000);
        animator.start();
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

        private LinearLayout layoutGrievance;
        private TextView tvGrievanceFiledCount;
        private TextView tvGrievanceResolvedCount;
        private ProgressBar pbGrievanceFiled;
        private ProgressBar pbGrievanceResolved;

        public HomeViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.home_item_title);
            description = (TextView) v.findViewById(R.id.home_item_text);
            cardIcon=(ImageView)v.findViewById(R.id.home_item_icon);
            cardView = (CardView) v.findViewById(R.id.home_card);
            layoutGrievance=(LinearLayout)v.findViewById(R.id.layout_grievance);
            layoutGrievance.setVisibility(View.GONE);
            tvGrievanceFiledCount =(TextView) v.findViewById(R.id.tvTotFiledCompCount);
            tvGrievanceResolvedCount =(TextView)v.findViewById(R.id.tvTotResolCompCount);
            pbGrievanceFiled =(ProgressBar) v.findViewById(R.id.pbCompFiled);
            pbGrievanceResolved =(ProgressBar) v.findViewById(R.id.pbCompResolved);
        }

    }



}
