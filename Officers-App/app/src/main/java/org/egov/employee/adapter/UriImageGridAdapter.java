package org.egov.employee.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;

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
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            /*imageView.setPadding(2, 2, 2, 2);*/
        }
        else{
            imageView = (ImageView) view;
        }

        try {
            Bitmap bitmap=MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), gridViewImages.get(i));
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageView;
    }

    @Override
    public int getCount() {
        return gridViewImages.size();
    }
}
