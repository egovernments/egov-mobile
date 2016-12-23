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

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.config.SessionManager;
import org.egovernments.egoverp.models.SupportDoc;

import java.util.ArrayList;

public class GrievanceImageViewerActivity extends FragmentActivity {

    public static final String COMPLAINT_SUPPORT_DOCS = "grievanceSupportDocs";

    public static final String POSITION = "position";
    private static SessionManager sessionManager;
    private ArrayList<SupportDoc> supportDocs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        int position = getIntent().getExtras().getInt(POSITION);

        supportDocs=(ArrayList<SupportDoc>)getIntent().getExtras().get(COMPLAINT_SUPPORT_DOCS);

        sessionManager = new SessionManager(getApplicationContext());

        ImageFragmentPagerAdapter imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.fullview_viewpager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
        viewPager.setCurrentItem(position);
    }

    public static class ImageFragment extends Fragment {
        static ImageFragment instantiateItem(String access_token, String fileId) {
            ImageFragment imageFragment = new ImageFragment();
            Bundle args = new Bundle();
            args.putString("access_token", access_token);
            args.putString("fileId", fileId);
            args.putString("type", "download");
            imageFragment.setArguments(args);
            return imageFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {

            View swipeView = inflater.inflate(R.layout.fragment_grievance_image, container, false);
            final ImageView imageView = (ImageView) swipeView.findViewById(R.id.image_viewpager_item);
            Bundle bundle = getArguments();

            final String url = sessionManager.getBaseURL()
                    + "/api/v1.0/complaint/downloadfile/"
                    + bundle.get("fileId")
                    + "?access_token=" + bundle.getString("access_token");

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

    }

    private class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return supportDocs.size();
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.instantiateItem(sessionManager.getAccessToken(), supportDocs.get(position).getFileId());
        }
    }

}



