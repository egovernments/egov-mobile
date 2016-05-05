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
