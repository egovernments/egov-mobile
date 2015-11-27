package com.egovernments.egov.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.egovernments.egov.R;
import com.egovernments.egov.activities.GrievanceDetailsActivity;
import com.egovernments.egov.activities.ImageViewerActivity;
import com.egovernments.egov.network.SessionManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Fragment used by GrievanceDetailsActivity viewpager
 **/

public class GrievanceImageFragment extends Fragment {

    //Default constructor
    public GrievanceImageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_grievance_image, container, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.image_viewpager_item);

        final Bundle arg = this.getArguments();

        SessionManager sessionManager = new SessionManager(getActivity());

        final String url = sessionManager.getBaseURL() + "api/v1.0/complaint/"
                + arg.getString("crn") + "/downloadSupportDocument?access_token="
                + arg.getString("access_token") + "&fileNo="
                + arg.getString("fileNo");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
                intent.putExtra(ImageViewerActivity.POSITION, arg.getInt("position"));
                intent.putExtra(ImageViewerActivity.COMPLAINT, GrievanceDetailsActivity.getGrievance());
                startActivity(intent);
            }
        });

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

    //Sets up a new fragment instance
    public static Fragment instantiateItem(int position, String access_token, String crn, String fileNo) {
        GrievanceImageFragment grievanceImageFragment = new GrievanceImageFragment();

        Bundle args = new Bundle();
        args.putString("access_token", access_token);
        args.putString("crn", crn);
        args.putString("fileNo", fileNo);
        args.putString("type", "download");
        args.putInt("position", position);
        grievanceImageFragment.setArguments(args);

        return grievanceImageFragment;
    }


}
