package com.egovernments.egov.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.egovernments.egov.R;
import com.egovernments.egov.network.ApiUrl;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class GrievanceImageFragment extends Fragment {

    //Default constructor
    public GrievanceImageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_grievance_image, container, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.image_viewpager_item);

        Bundle arg = this.getArguments();

        final String url = ApiUrl.api_baseUrl
                + "/complaint/"
                + arg.getString("crn") + "/downloadSupportDocument?access_token="
                + arg.getString("access_token") + "&fileNo="
                + arg.getString("fileNo");

        Picasso.with(getActivity())
                .load(url)
                .placeholder(R.drawable.placeholder)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.broken_icon)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(getActivity())
                                .load(url)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.broken_icon)
                                .into(imageView);
                    }
                });

        return view;

    }

    public static Fragment instantiateItem(String access_token, String crn, String fileNo) {
        GrievanceImageFragment grievanceImageFragment = new GrievanceImageFragment();

        Bundle args = new Bundle();
        args.putString("access_token", access_token);
        args.putString("crn", crn);
        args.putString("fileNo", fileNo);
        args.putString("type", "download");
        grievanceImageFragment.setArguments(args);

        return grievanceImageFragment;
    }


}
