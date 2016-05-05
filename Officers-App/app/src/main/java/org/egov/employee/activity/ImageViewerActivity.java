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

import org.egov.employee.utils.PicassoTrustAll;

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
}
