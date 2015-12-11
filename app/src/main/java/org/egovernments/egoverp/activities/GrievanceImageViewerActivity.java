package org.egovernments.egoverp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.models.Grievance;
import org.egovernments.egoverp.network.SessionManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class GrievanceImageViewerActivity extends FragmentActivity {

    public static final String COMPLAINT = "";

    public static final String POSITION = "position";

    private Grievance grievance;

    private static SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        int position = getIntent().getExtras().getInt(POSITION);

        grievance = (Grievance) getIntent().getSerializableExtra(COMPLAINT);

        sessionManager = new SessionManager(this);

        ImageFragmentPagerAdapter imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.fullview_viewpager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
        viewPager.setCurrentItem(position);
    }

    private class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return grievance.getSupportDocsSize();
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.instantiateItem(sessionManager.getAccessToken(),
                    grievance.getCrn(), String.valueOf(grievance.getSupportDocsSize() - position));
        }
    }

    public static class ImageFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {

            View swipeView = inflater.inflate(R.layout.fragment_grievance_image, container, false);
            final ImageView imageView = (ImageView) swipeView.findViewById(R.id.image_viewpager_item);
            Bundle bundle = getArguments();

            final String url = sessionManager.getBaseURL() + "/api/v1.0/complaint/"
                    + bundle.getString("crn") + "/downloadSupportDocument?access_token="
                    + bundle.getString("access_token") + "&fileNo="
                    + bundle.getString("fileNo");

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

            swipeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            return swipeView;
        }

        static ImageFragment instantiateItem(String access_token, String crn, String fileNo) {
            ImageFragment imageFragment = new ImageFragment();

            Bundle args = new Bundle();
            args.putString("access_token", access_token);
            args.putString("crn", crn);
            args.putString("fileNo", fileNo);
            args.putString("type", "download");
            imageFragment.setArguments(args);

            return imageFragment;
        }

    }

}



