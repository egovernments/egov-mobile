package org.egov.employee.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

import org.egov.employee.utils.PicassoTrustAll;

import offices.org.egov.egovemployees.R;

/**
 * Created by egov on 7/10/16.
 */

public class ImageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View swipeView = inflater.inflate(R.layout.fragment_image, container, false);
        final ImageView imageView = (ImageView) swipeView.findViewById(R.id.image_viewpager_item);
        final ProgressBar pb=(ProgressBar)swipeView.findViewById(R.id.progressBar);
        final Bundle bundle = getArguments();

        PicassoTrustAll.getInstance(getActivity())
                .load(bundle.getString("imageUrl"))
                .error(R.drawable.ic_broken_image_white_18dp)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        pb.setVisibility(View.GONE);
                    }
                });

        swipeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return swipeView;
    }

    static ImageFragment instantiateItem(String imageUrl) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        imageFragment.setArguments(args);
        return imageFragment;
    }

}