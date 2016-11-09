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


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.egov.employee.api.ApiUrl;
import org.egov.employee.data.SupportDoc;

import java.util.ArrayList;

import offices.org.egov.egovemployees.R;

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

        final String url = ((BaseActivity)getActivity()).preference.getActiveCityUrl()
                + "/api/v1.0/complaint/downloadfile/"
                + arg.get("fileId")
                + "?access_token=" + arg.getString("access_token");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ArrayList<String> complaintImgsUrl=new ArrayList<>();
                for(SupportDoc doc:GrievanceDetailsActivity.getGrievance().getSupportDocs())
                {
                    complaintImgsUrl.add(((BaseActivity)getActivity()).preference.getActiveCityUrl()+ ApiUrl.PGR_DOWNLOAD_IMAGE+doc.getFileId()+"?access_token="+((BaseActivity)getActivity()).preference.getApiAccessToken());
                }

                Intent intOpenImageView=new Intent(getActivity(), ImageViewerActivity.class);
                intOpenImageView.putExtra("position", arg.getInt("position"));
                intOpenImageView.putStringArrayListExtra("imageUrls", complaintImgsUrl);
                startActivity(intOpenImageView);
            }
        });

        Picasso.with(getActivity())
                .load(url)
                .placeholder(R.drawable.placeholder)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.ic_broken_image_white_18dp)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(getActivity())
                                .load(url)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.ic_broken_image_white_18dp)
                                .into(imageView);
                    }
                });

        return view;

    }

    //Sets up a new fragment instance
    public static Fragment instantiateItem(int position, String access_token, String fileId) {
        GrievanceImageFragment grievanceImageFragment = new GrievanceImageFragment();

        Bundle args = new Bundle();
        args.putString("access_token", access_token);
        args.putString("fileId", fileId);
        args.putString("type", "download");
        args.putInt("position", position);
        grievanceImageFragment.setArguments(args);

        return grievanceImageFragment;
    }


}
