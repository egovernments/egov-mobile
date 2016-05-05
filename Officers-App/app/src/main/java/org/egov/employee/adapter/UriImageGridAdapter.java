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
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import offices.org.egov.egovemployees.R;

/**
 * Created by egov on 27/4/16.
 */
public class UriImageGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<Uri> gridViewImages;

    public UriImageGridAdapter(Context c, List<Uri> gridViewImages){
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
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            /*imageView.setPadding(2, 2, 2, 2);*/
        }
        else{
            imageView = (ImageView) view;
        }

        if(gridViewImages.get(i).toString().startsWith("file://"))
        {
            try {
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), gridViewImages.get(i));
                Matrix m = new Matrix();
                m.setRectToRect(new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight()), new RectF(0, 0, 300, 300), Matrix.ScaleToFit.CENTER);
                imageView.setImageBitmap(Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),mBitmap.getHeight(), m, true));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Picasso.with(mContext).load(gridViewImages.get(i)).centerCrop().resize(300, 300).into(imageView);
        }

        return imageView;
    }

    @Override
    public int getCount() {
        return gridViewImages.size();
    }
}
