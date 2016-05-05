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

package org.egov.employee.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.egov.employee.utils.PicassoTrustAll;

import java.util.List;

import offices.org.egov.egovemployees.R;

/**
 * Created by egov on 27/4/16.
 */
public class ImageGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> gridViewImages;

    public ImageGridAdapter(Context c, List<String> gridViewImages){
        mContext = c;
        this.gridViewImages=gridViewImages;
    }
    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ImageView imageView;
        if(view == null){
            imageView = new ImageView(mContext);
            int imageSize=(int) mContext.getResources().getDimension(R.dimen.imagegridsize);
            imageView.setLayoutParams(new GridView.LayoutParams(imageSize, imageSize));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //imageView.setPadding(2, 2, 2, 2);
        }
        else{
            imageView = (ImageView) view;
        }

        PicassoTrustAll.getInstance(mContext)
                .load(gridViewImages.get(i))
                .centerCrop()
                .resize(250, 250)
                .error(R.drawable.ic_broken_image_white_18dp)
                .placeholder(R.drawable.loaderbg)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                    @Override
                    public void onError() {

                    }
                });
        return imageView;
    }

    @Override
    public int getCount() {
        return gridViewImages.size();
    }
}
