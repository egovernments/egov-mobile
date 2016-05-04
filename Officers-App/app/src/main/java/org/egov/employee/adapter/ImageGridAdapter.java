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
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
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
