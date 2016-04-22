package org.egov.employee.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import offices.org.egov.egovemployees.R;

/**
 * Created by egov on 19/4/16.
 */
public class ImageViewerActivity extends BaseActivity {

    List<String> imageUrls=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrls=getIntent().getStringArrayListExtra("imageUrls");
        ImageFragmentPagerAdapter imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerimage);
        viewPager.setAdapter(imageFragmentPagerAdapter);
        viewPager.setCurrentItem(getIntent().getExtras().getInt("position"));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_image_view;
    }

    private class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.instantiateItem(imageUrls.get(position));
        }
    }

    public static class ImageFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {

            View swipeView = inflater.inflate(R.layout.fragment_image, container, false);
            final ImageView imageView = (ImageView) swipeView.findViewById(R.id.image_viewpager_item);
            final ProgressBar pb=(ProgressBar)swipeView.findViewById(R.id.progressBar);
            final Bundle bundle = getArguments();

            Picasso.with(getActivity())
                    .load(bundle.getString("imageUrl"))
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            pb.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            pb.setVisibility(View.GONE);
                            Picasso.with(getActivity())
                                    .load(bundle.getString("imageUrl"))
                                    .placeholder(R.drawable.ic_schedule_white_18dp)
                                    .error(R.drawable.ic_broken_image_white_18dp)
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

        static ImageFragment instantiateItem(String imageUrl) {
            ImageFragment imageFragment = new ImageFragment();
            Bundle args = new Bundle();
            args.putString("imageUrl", imageUrl);
            imageFragment.setArguments(args);
            return imageFragment;
        }

    }
}
